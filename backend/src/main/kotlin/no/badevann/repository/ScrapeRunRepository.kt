package no.badevann.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.ScrapeRun
import no.badevann.domain.ScrapeRunStatus
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ScrapeRunRepository : CrudRepository<ScrapeRun, UUID> {
    fun findByScraperId(scraperId: UUID): List<ScrapeRun>
    fun findByStatus(status: ScrapeRunStatus): List<ScrapeRun>
    fun findByScraperIdOrderByStartedAtDesc(scraperId: UUID): List<ScrapeRun>
}
