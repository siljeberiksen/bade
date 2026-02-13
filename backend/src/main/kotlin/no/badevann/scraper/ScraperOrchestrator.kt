package no.badevann.scraper

import jakarta.inject.Singleton
import no.badevann.domain.*
import no.badevann.repository.*
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

@Singleton
class ScraperOrchestrator(
    private val scrapers: List<BathingDataScraper>,
    private val scraperRepository: ScraperRepository,
    private val scrapeRunRepository: ScrapeRunRepository,
    private val artifactRepository: ScrapeArtifactRepository,
    private val bathingSiteRepository: BathingSiteRepository,
    private val measurementRepository: MeasurementRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val dataSourceRepository: DataSourceRepository
) {
    private val log = LoggerFactory.getLogger(ScraperOrchestrator::class.java)

    fun getAvailableScrapers(): List<String> = scrapers.map { it.scraperName }

    suspend fun runScraper(scraperName: String, trigger: ScrapeTrigger = ScrapeTrigger.MANUAL): ScrapeRun {
        val scraper = scrapers.find { it.scraperName == scraperName }
            ?: throw IllegalArgumentException("Unknown scraper: $scraperName")

        val scraperEntity = scraperRepository.findByName(scraperName)
            ?: throw IllegalStateException("Scraper '$scraperName' not registered in database")

        val run = scrapeRunRepository.save(
            ScrapeRun(
                scraperId = scraperEntity.id!!,
                trigger = trigger,
                status = ScrapeRunStatus.RUNNING
            )
        )

        val context = ScrapeContext(run.id!!, artifactRepository)

        return try {
            val result = scraper.scrape(context)
            val savedCount = persistResults(result, scraperEntity, run.id)

            val status = when {
                result.errors.isNotEmpty() && result.measurements.isEmpty() -> ScrapeRunStatus.FAILED
                result.errors.isNotEmpty() -> ScrapeRunStatus.PARTIAL
                else -> ScrapeRunStatus.SUCCESS
            }

            scrapeRunRepository.update(
                run.copy(
                    finishedAt = Instant.now(),
                    status = status,
                    message = (result.warnings + result.errors).joinToString("; ").takeIf { it.isNotBlank() },
                    sitesFound = result.sitesFound,
                    measurementsSaved = savedCount
                )
            )
        } catch (e: Exception) {
            log.error("Scraper '$scraperName' failed unexpectedly", e)
            scrapeRunRepository.update(
                run.copy(
                    finishedAt = Instant.now(),
                    status = ScrapeRunStatus.FAILED,
                    message = "Unexpected error: ${e.message}"
                )
            )
        }
    }

    private fun persistResults(result: ScrapeResult, scraperEntity: Scraper, runId: UUID): Int {
        if (result.measurements.isEmpty()) return 0

        val source = dataSourceRepository.findById(scraperEntity.sourceId).orElse(null)
            ?: throw IllegalStateException("DataSource not found for scraper ${scraperEntity.name}")

        val municipality = resolveOrCreateMunicipality(scraperEntity.name)
        var savedCount = 0

        for (parsed in result.measurements) {
            val site = resolveOrCreateSite(parsed, municipality)

            val exists = measurementRepository.existsByBathingSiteIdAndMeasurementTypeAndObservedAtAndSourceId(
                bathingSiteId = site.id!!,
                measurementType = parsed.type,
                observedAt = parsed.observedAt,
                sourceId = source.id!!
            )

            if (!exists) {
                measurementRepository.save(
                    Measurement(
                        bathingSiteId = site.id,
                        sourceId = source.id,
                        scrapeRunId = runId,
                        observedAt = parsed.observedAt,
                        measurementType = parsed.type,
                        value = parsed.value,
                        unit = parsed.unit,
                        qualifier = parsed.qualifier,
                        rawText = parsed.rawText
                    )
                )
                savedCount++
            }
        }

        return savedCount
    }

    private fun resolveOrCreateMunicipality(scraperName: String): Municipality {
        val municipalityName = when {
            scraperName.contains("oslo", ignoreCase = true) -> "Oslo"
            scraperName.contains("trondheim", ignoreCase = true) -> "Trondheim"
            scraperName.contains("bergen", ignoreCase = true) -> "Bergen"
            scraperName.contains("ringsaker", ignoreCase = true) -> "Ringsaker"
            else -> scraperName.replaceFirstChar { it.uppercase() }
        }

        val code = when (municipalityName) {
            "Oslo" -> "0301"
            "Trondheim" -> "5001"
            "Bergen" -> "4601"
            "Ringsaker" -> "3411"
            else -> "0000"
        }

        return municipalityRepository.findByCode(code)
            ?: municipalityRepository.save(
                Municipality(name = municipalityName, code = code)
            )
    }

    private fun resolveOrCreateSite(parsed: ParsedMeasurement, municipality: Municipality): BathingSite {
        val existing = bathingSiteRepository.findByMunicipalityIdAndName(municipality.id!!, parsed.siteName)
        if (existing != null) return existing

        return bathingSiteRepository.save(
            BathingSite(
                municipalityId = municipality.id,
                name = parsed.siteName,
                slug = parsed.siteSlug,
                lat = parsed.lat ?: 59.91,
                lon = parsed.lon ?: 10.75,
                waterType = WaterType.UNKNOWN
            )
        )
    }
}
