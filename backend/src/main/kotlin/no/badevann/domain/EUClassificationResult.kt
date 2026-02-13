package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Serdeable
@MappedEntity("eu_classification_result")
data class EUClassificationResult(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val bathingSiteId: UUID,
    val seasonYear: Int,
    val windowStart: LocalDate,
    val windowEnd: LocalDate,
    val nSamples: Int,
    val waterType: WaterType,
    val rating: QualityRating = QualityRating.UNRATED,
    val ecoliP95: BigDecimal? = null,
    val enterococciP95: BigDecimal? = null,
    val ecoliP90: BigDecimal? = null,
    val enterococciP90: BigDecimal? = null,
    val algorithmVersion: String = "1.0.0",
    val computedAt: Instant = Instant.now()
)
