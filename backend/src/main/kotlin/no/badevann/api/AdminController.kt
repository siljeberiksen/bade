package no.badevann.api

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import kotlinx.coroutines.runBlocking
import no.badevann.api.dto.ScrapeTriggerRequest
import no.badevann.api.dto.ScrapeTriggerResponse
import no.badevann.api.dto.ScrapeRunDto
import no.badevann.domain.ScrapeTrigger
import no.badevann.repository.ScrapeRunRepository
import no.badevann.repository.ScraperRepository
import no.badevann.scraper.ScraperOrchestrator
import no.badevann.service.StatusCalculationService

@Controller("/v1/admin")
class AdminController(
    private val orchestrator: ScraperOrchestrator,
    private val scrapeRunRepository: ScrapeRunRepository,
    private val scraperRepository: ScraperRepository,
    private val statusCalculationService: StatusCalculationService
) {
    @Post("/scrapers/{scraperName}/run")
    fun triggerScraper(@PathVariable scraperName: String): HttpResponse<ScrapeTriggerResponse> {
        return try {
            val run = runBlocking {
                orchestrator.runScraper(scraperName, ScrapeTrigger.MANUAL)
            }
            HttpResponse.ok(
                ScrapeTriggerResponse(
                    runId = run.id!!.toString(),
                    status = run.status.name,
                    message = run.message
                )
            )
        } catch (e: IllegalArgumentException) {
            HttpResponse.badRequest(
                ScrapeTriggerResponse(
                    runId = "",
                    status = "ERROR",
                    message = e.message
                )
            )
        }
    }

    @Get("/scrape-runs")
    fun listScrapeRuns(
        @QueryValue scraperId: String?,
        @QueryValue status: String?
    ): List<ScrapeRunDto> {
        val runs = if (scraperId != null) {
            scrapeRunRepository.findByScraperIdOrderByStartedAtDesc(
                java.util.UUID.fromString(scraperId)
            )
        } else {
            scrapeRunRepository.findAll().toList()
        }

        return runs.map { run ->
            val scraper = scraperRepository.findById(run.scraperId).orElse(null)
            ScrapeRunDto(
                id = run.id!!.toString(),
                scraperName = scraper?.name,
                startedAt = run.startedAt.toString(),
                finishedAt = run.finishedAt?.toString(),
                status = run.status.name,
                trigger = run.trigger.name,
                message = run.message,
                sitesFound = run.sitesFound,
                measurementsSaved = run.measurementsSaved
            )
        }
    }

    @Get("/scrapers")
    fun listScrapers(): List<String> {
        return orchestrator.getAvailableScrapers()
    }

    @Post("/recompute-status")
    fun recomputeAllStatus(): HttpResponse<Map<String, String>> {
        statusCalculationService.recomputeAll()
        return HttpResponse.ok(mapOf("status" to "completed"))
    }
}
