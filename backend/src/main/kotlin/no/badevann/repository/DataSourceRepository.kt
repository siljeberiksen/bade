package no.badevann.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.DataSource
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface DataSourceRepository : CrudRepository<DataSource, UUID> {
    fun findByOwner(owner: String): List<DataSource>
}
