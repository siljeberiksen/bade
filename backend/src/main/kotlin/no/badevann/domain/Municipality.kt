package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("municipality")
data class Municipality(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val name: String,
    val code: String,
    val county: String? = null,
    val createdAt: Instant = Instant.now()
)
