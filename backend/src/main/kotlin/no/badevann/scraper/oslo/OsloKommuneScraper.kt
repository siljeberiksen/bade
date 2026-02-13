package no.badevann.scraper.oslo

import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import jakarta.inject.Singleton
import no.badevann.domain.MeasurementQualifier
import no.badevann.domain.MeasurementType
import no.badevann.domain.MeasurementUnit
import no.badevann.scraper.BathingDataScraper
import no.badevann.scraper.ParsedMeasurement
import no.badevann.scraper.ScrapeContext
import no.badevann.scraper.ScrapeResult
import no.badevann.scraper.SlugUtils
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Singleton
class OsloKommuneScraper(
    @Client("\${scraper.oslo.base-url:https://www.oslo.kommune.no}")
    private val httpClient: HttpClient
) : BathingDataScraper {

    private val log = LoggerFactory.getLogger(OsloKommuneScraper::class.java)

    override val scraperName = "oslo-kommune"
    override val version = "1.0.0"

    companion object {
        const val BATHING_PATH = "/natur-kultur-og-fritid/tur-og-friluftsliv/badeplasser-og-temperaturer/"
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        private val OSLO_ZONE = ZoneId.of("Europe/Oslo")
    }

    override suspend fun scrape(context: ScrapeContext): ScrapeResult {
        val measurements = mutableListOf<ParsedMeasurement>()
        val warnings = mutableListOf<String>()
        val errors = mutableListOf<String>()

        try {
            val html = httpClient.toBlocking().retrieve(BATHING_PATH)
            context.storeArtifact(BATHING_PATH, html)

            val doc = Jsoup.parse(html)
            val tables = doc.select("table")

            if (tables.isEmpty()) {
                warnings.add("No tables found on Oslo bathing page")
                return ScrapeResult(warnings = warnings)
            }

            for (table in tables) {
                val rows = table.select("tr")
                if (rows.size < 2) continue

                for (row in rows.drop(1)) {
                    val cells = row.select("td")
                    if (cells.size < 2) continue

                    try {
                        val siteName = cells[0].text().trim()
                        if (siteName.isBlank()) continue

                        val slug = SlugUtils.toSlug(siteName)

                        val tempText = cells.getOrNull(1)?.text()?.trim()
                        if (!tempText.isNullOrBlank()) {
                            val tempValue = parseTemperature(tempText)
                            if (tempValue != null) {
                                measurements.add(
                                    ParsedMeasurement(
                                        siteName = siteName,
                                        siteSlug = slug,
                                        lat = null,
                                        lon = null,
                                        observedAt = Instant.now(),
                                        type = MeasurementType.TEMPERATURE,
                                        value = tempValue,
                                        unit = MeasurementUnit.CELSIUS,
                                        rawText = tempText
                                    )
                                )
                            }
                        }

                        val bacteriaText = cells.getOrNull(2)?.text()?.trim()
                        if (!bacteriaText.isNullOrBlank()) {
                            val bacteriaResult = parseBacteriaCount(bacteriaText)
                            if (bacteriaResult != null) {
                                measurements.add(
                                    ParsedMeasurement(
                                        siteName = siteName,
                                        siteSlug = slug,
                                        lat = null,
                                        lon = null,
                                        observedAt = Instant.now(),
                                        type = MeasurementType.E_COLI,
                                        value = bacteriaResult.first,
                                        unit = MeasurementUnit.CFU_PER_100ML,
                                        qualifier = bacteriaResult.second,
                                        rawText = bacteriaText
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        warnings.add("Failed to parse row: ${row.text()} — ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Oslo scraper failed", e)
            errors.add("Failed to fetch/parse Oslo bathing page: ${e.message}")
        }

        val siteNames = measurements.map { it.siteName }.distinct()
        return ScrapeResult(
            sitesFound = siteNames.size,
            measurements = measurements,
            warnings = warnings,
            errors = errors
        )
    }

    private fun parseTemperature(text: String): BigDecimal? {
        val cleaned = text
            .replace(",", ".")
            .replace("°C", "")
            .replace("°", "")
            .replace("grader", "")
            .trim()

        return cleaned.toBigDecimalOrNull()
    }

    private fun parseBacteriaCount(text: String): Pair<BigDecimal, MeasurementQualifier>? {
        val cleaned = text.trim()

        return when {
            cleaned.startsWith("<") || cleaned.startsWith("&lt;") -> {
                val numStr = cleaned.removePrefix("<").removePrefix("&lt;").trim()
                numStr.toBigDecimalOrNull()?.let { it to MeasurementQualifier.LESS_THAN }
            }
            cleaned.startsWith(">") || cleaned.startsWith("&gt;") -> {
                val numStr = cleaned.removePrefix(">").removePrefix("&gt;").trim()
                numStr.toBigDecimalOrNull()?.let { it to MeasurementQualifier.GREATER_THAN }
            }
            else -> {
                val numStr = cleaned.replace(Regex("[^0-9.,]"), "").replace(",", ".")
                numStr.toBigDecimalOrNull()?.let { it to MeasurementQualifier.EXACT }
            }
        }
    }
}
