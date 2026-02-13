package no.badevann.repository

import io.micronaut.data.annotation.Query
import io.micronaut.data.jdbc.annotation.JdbcRepository
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.repository.CrudRepository
import no.badevann.domain.Measurement
import no.badevann.domain.MeasurementType
import java.time.Instant
import java.util.UUID

@JdbcRepository(dialect = Dialect.POSTGRES)
interface MeasurementRepository : CrudRepository<Measurement, UUID> {

    fun findByBathingSiteIdOrderByObservedAtDesc(bathingSiteId: UUID): List<Measurement>

    fun findByBathingSiteIdAndMeasurementTypeOrderByObservedAtDesc(
        bathingSiteId: UUID,
        measurementType: MeasurementType
    ): List<Measurement>

    @Query("""
        SELECT m.* FROM measurement m
        WHERE m.bathing_site_id = :siteId
        AND m.measurement_type = :type
        AND m.observed_at >= :from
        AND m.observed_at <= :to
        ORDER BY m.observed_at DESC
    """)
    fun findBySiteAndTypeInRange(
        siteId: UUID,
        type: MeasurementType,
        from: Instant,
        to: Instant
    ): List<Measurement>

    @Query("""
        SELECT m.* FROM measurement m
        WHERE m.bathing_site_id = :siteId
        AND m.measurement_type = :type
        ORDER BY m.observed_at DESC
        LIMIT 1
    """)
    fun findLatestBySiteAndType(siteId: UUID, type: MeasurementType): Measurement?

    @Query("""
        SELECT m.* FROM measurement m
        WHERE m.bathing_site_id = :siteId
        AND m.observed_at >= :windowStart
        ORDER BY m.observed_at ASC
    """)
    fun findBySiteFromDate(siteId: UUID, windowStart: Instant): List<Measurement>

    fun existsByBathingSiteIdAndMeasurementTypeAndObservedAtAndSourceId(
        bathingSiteId: UUID,
        measurementType: MeasurementType,
        observedAt: Instant,
        sourceId: UUID
    ): Boolean
}
