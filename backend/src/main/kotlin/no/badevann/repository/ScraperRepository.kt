package no.badevann.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.Scraper as ScraperEntity
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface ScraperRepository : CrudRepository<ScraperEntity, UUID> {
    fun findByName(name: String): ScraperEntity?
    fun findByEnabledTrue(): List<ScraperEntity>
}
