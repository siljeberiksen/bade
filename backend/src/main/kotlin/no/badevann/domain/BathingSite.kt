package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("bathing_site")
data class BathingSite(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val municipalityId: UUID,
    val name: String,
    val slug: String,
    val waterType: WaterType = WaterType.UNKNOWN,
    val lat: Double,
    val lon: Double,
    val isActive: Boolean = true,
    val publicNotes: String? = null,
    val sourceDisplayName: String? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)
