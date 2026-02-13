package no.badevann.scraper

interface BathingDataScraper {
    val scraperName: String
    val version: String
    suspend fun scrape(context: ScrapeContext): ScrapeResult
}
