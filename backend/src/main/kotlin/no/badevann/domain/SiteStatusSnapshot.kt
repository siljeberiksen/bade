package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.MappedProperty
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("site_status_snapshot")
data class SiteStatusSnapshot(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val bathingSiteId: UUID,
    val computedAt: Instant = Instant.now(),
    val latestSampleAt: Instant? = null,
    val safeToSwim: Boolean = false,
    val statusLabel: StatusLabel = StatusLabel.NO_DATA,
    val reasonCode: String? = null,
    val waterQualityRating: QualityRating = QualityRating.UNRATED,
    val ratingBasis: RatingBasis? = null,
    val temperatureCelsius: BigDecimal? = null,
    val trendDirection: TrendDirection = TrendDirection.UNKNOWN,
    @field:MappedProperty("trend_change_7d_celsius")
    val trendChange7dCelsius: BigDecimal? = null
)
