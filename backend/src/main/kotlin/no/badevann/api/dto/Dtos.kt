package no.badevann.api.dto

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class BeachSummaryDto(
    val id: String,
    val name: String,
    val slug: String,
    val municipalityName: String?,
    val waterType: String,
    val lat: Double,
    val lon: Double,
    val safeToSwim: Boolean,
    val statusLabel: String,
    val waterQualityRating: String,
    val temperatureCelsius: Double?,
    val trendDirection: String
)

@Serdeable
data class BeachDetailDto(
    val id: String,
    val name: String,
    val slug: String,
    val municipalityId: String,
    val municipalityName: String?,
    val waterType: String,
    val lat: Double,
    val lon: Double,
    val isActive: Boolean,
    val publicNotes: String?,
    val status: BeachStatusDto?,
    val latestMeasurements: List<MeasurementDto>
)

@Serdeable
data class BeachStatusDto(
    val bathingSiteId: String,
    val computedAt: String,
    val latestSampleAt: String?,
    val safeToSwim: Boolean,
    val statusLabel: String,
    val reasonCode: String?,
    val waterQualityRating: String,
    val ratingBasis: String?,
    val temperatureCelsius: Double?,
    val trendDirection: String,
    val trendChange7dCelsius: Double?
)

@Serdeable
data class MeasurementDto(
    val id: String,
    val bathingSiteId: String,
    val observedAt: String,
    val measurementType: String,
    val value: Double,
    val unit: String,
    val qualifier: String,
    val rawText: String?
)

@Serdeable
data class MunicipalityDto(
    val id: String,
    val name: String,
    val code: String,
    val county: String?
)

@Serdeable
data class BeachFilters(
    val municipalityId: String? = null,
    val waterType: String? = null,
    val status: String? = null,
    val q: String? = null
)

@Serdeable
data class ScrapeRunDto(
    val id: String,
    val scraperName: String?,
    val startedAt: String,
    val finishedAt: String?,
    val status: String,
    val trigger: String,
    val message: String?,
    val sitesFound: Int,
    val measurementsSaved: Int
)

@Serdeable
data class ScrapeTriggerRequest(
    val scraperName: String,
    val dryRun: Boolean = false
)

@Serdeable
data class ScrapeTriggerResponse(
    val runId: String,
    val status: String,
    val message: String?
)

@Serdeable
data class MetaDto(
    val statusLabels: List<String>,
    val qualityRatings: List<String>,
    val waterTypes: List<String>,
    val measurementTypes: List<String>,
    val trendDirections: List<String>,
    val availableScrapers: List<String>
)
