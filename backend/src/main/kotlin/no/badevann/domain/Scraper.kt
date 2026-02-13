package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("scraper")
data class Scraper(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val name: String,
    val sourceId: UUID,
    val version: String = "1.0.0",
    val enabled: Boolean = true,
    val schedule: String? = null,
    val createdAt: Instant = Instant.now()
)
