package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("scrape_run")
data class ScrapeRun(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val scraperId: UUID,
    val startedAt: Instant = Instant.now(),
    val finishedAt: Instant? = null,
    val status: ScrapeRunStatus = ScrapeRunStatus.RUNNING,
    val trigger: ScrapeTrigger = ScrapeTrigger.MANUAL,
    val message: String? = null,
    val sitesFound: Int = 0,
    val measurementsSaved: Int = 0,
    val createdAt: Instant = Instant.now()
)
