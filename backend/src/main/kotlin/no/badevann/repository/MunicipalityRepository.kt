package no.badevann.repository

import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.Municipality
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MunicipalityRepository : CrudRepository<Municipality, UUID> {
    fun findByCode(code: String): Municipality?
    fun findByNameIlike(name: String): List<Municipality>
}
