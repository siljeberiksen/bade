package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("measurement")
data class Measurement(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val bathingSiteId: UUID,
    val sourceId: UUID,
    val scrapeRunId: UUID? = null,
    val observedAt: Instant,
    val measurementType: MeasurementType,
    val value: BigDecimal,
    val unit: MeasurementUnit,
    val qualifier: MeasurementQualifier = MeasurementQualifier.EXACT,
    val rawText: String? = null,
    val sampleDepthCm: Int? = null,
    val sourceExternalId: String? = null,
    val createdAt: Instant = Instant.now()
)
