package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("scrape_artifact")
data class ScrapeArtifact(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val scrapeRunId: UUID,
    val url: String,
    val fetchedAt: Instant = Instant.now(),
    val httpStatus: Int? = null,
    val contentType: String? = null,
    val charset: String? = null,
    val bodySha256: String? = null,
    val body: String? = null,
    val parseWarnings: String? = null
)
