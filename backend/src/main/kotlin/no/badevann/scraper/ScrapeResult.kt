package no.badevann.scraper

import no.badevann.domain.BathingSite
import no.badevann.domain.Measurement
import java.util.UUID

data class ScrapeResult(
    val sitesFound: Int = 0,
    val measurements: List<ParsedMeasurement> = emptyList(),
    val warnings: List<String> = emptyList(),
    val errors: List<String> = emptyList()
) {
    val isSuccess: Boolean get() = errors.isEmpty()
    val isPartial: Boolean get() = errors.isNotEmpty() && measurements.isNotEmpty()
}

data class ParsedMeasurement(
    val siteName: String,
    val siteSlug: String,
    val lat: Double?,
    val lon: Double?,
    val observedAt: java.time.Instant,
    val type: no.badevann.domain.MeasurementType,
    val value: java.math.BigDecimal,
    val unit: no.badevann.domain.MeasurementUnit,
    val qualifier: no.badevann.domain.MeasurementQualifier = no.badevann.domain.MeasurementQualifier.EXACT,
    val rawText: String? = null
)
