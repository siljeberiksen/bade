package no.badevann.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.BathingSite
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface BathingSiteRepository : CrudRepository<BathingSite, UUID> {

    fun findByMunicipalityId(municipalityId: UUID): List<BathingSite>

    fun findBySlug(slug: String): BathingSite?

    fun findByIsActiveTrue(): List<BathingSite>

    fun findByNameIlikeAndIsActiveTrue(name: String): List<BathingSite>

    @Query("""
        SELECT bs.* FROM bathing_site bs
        WHERE bs.is_active = true
        AND ST_DWithin(
            bs.geog,
            ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
            :radiusMeters
        )
        ORDER BY bs.geog <-> ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography
        LIMIT :limit
    """)
    fun findNearby(lat: Double, lon: Double, radiusMeters: Double, limit: Int): List<BathingSite>

    @Query("""
        SELECT bs.* FROM bathing_site bs
        WHERE bs.is_active = true
        AND bs.lon BETWEEN :minLon AND :maxLon
        AND bs.lat BETWEEN :minLat AND :maxLat
    """)
    fun findInBoundingBox(minLon: Double, minLat: Double, maxLon: Double, maxLat: Double): List<BathingSite>

    fun findByMunicipalityIdAndName(municipalityId: UUID, name: String): BathingSite?
}
