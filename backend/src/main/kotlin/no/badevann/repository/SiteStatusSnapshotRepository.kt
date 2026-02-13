package no.badevann.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.SiteStatusSnapshot
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface SiteStatusSnapshotRepository : CrudRepository<SiteStatusSnapshot, UUID> {
    fun findByBathingSiteId(bathingSiteId: UUID): SiteStatusSnapshot?
}
