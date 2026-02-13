package no.badevann.service

import jakarta.inject.Singleton
import no.badevann.api.dto.*
import no.badevann.domain.*
import no.badevann.repository.*
import java.util.UUID

@Singleton
class BeachService(
    private val siteRepository: BathingSiteRepository,
    private val municipalityRepository: MunicipalityRepository,
    private val snapshotRepository: SiteStatusSnapshotRepository,
    private val measurementRepository: MeasurementRepository
) {
    fun listBeaches(filters: BeachFilters): List<BeachSummaryDto> {
        val sites = when {
            filters.municipalityId != null ->
                siteRepository.findByMunicipalityId(UUID.fromString(filters.municipalityId))
            filters.q != null ->
                siteRepository.findByNameIlikeAndIsActiveTrue("%${filters.q}%")
            else ->
                siteRepository.findByIsActiveTrue()
        }

        return sites.map { site ->
            val snapshot = snapshotRepository.findByBathingSiteId(site.id!!)
            val municipality = municipalityRepository.findById(site.municipalityId).orElse(null)
            toSummaryDto(site, snapshot, municipality)
        }
    }

    fun findNearby(lat: Double, lon: Double, radiusKm: Double, limit: Int): List<BeachSummaryDto> {
        val radiusMeters = radiusKm * 1000.0
        val sites = siteRepository.findNearby(lat, lon, radiusMeters, limit)

        return sites.map { site ->
            val snapshot = snapshotRepository.findByBathingSiteId(site.id!!)
            val municipality = municipalityRepository.findById(site.municipalityId).orElse(null)
            toSummaryDto(site, snapshot, municipality)
        }
    }

    fun getBeachDetail(beachId: UUID): BeachDetailDto? {
        val site = siteRepository.findById(beachId).orElse(null) ?: return null
        val snapshot = snapshotRepository.findByBathingSiteId(beachId)
        val municipality = municipalityRepository.findById(site.municipalityId).orElse(null)
        val latestMeasurements = measurementRepository.findByBathingSiteIdOrderByObservedAtDesc(beachId)
            .take(20)

        return BeachDetailDto(
            id = site.id!!.toString(),
            name = site.name,
            slug = site.slug,
            municipalityId = site.municipalityId.toString(),
            municipalityName = municipality?.name,
            waterType = site.waterType.name,
            lat = site.lat,
            lon = site.lon,
            isActive = site.isActive,
            publicNotes = site.publicNotes,
            status = snapshot?.let { toStatusDto(it) },
            latestMeasurements = latestMeasurements.map { toMeasurementDto(it) }
        )
    }

    fun getBeachStatus(beachId: UUID): BeachStatusDto? {
        val snapshot = snapshotRepository.findByBathingSiteId(beachId) ?: return null
        return toStatusDto(snapshot)
    }

    fun getMeasurements(
        beachId: UUID,
        type: MeasurementType?,
        from: java.time.Instant?,
        to: java.time.Instant?
    ): List<MeasurementDto> {
        val measurements = if (type != null && from != null && to != null) {
            measurementRepository.findBySiteAndTypeInRange(beachId, type, from, to)
        } else if (type != null) {
            measurementRepository.findByBathingSiteIdAndMeasurementTypeOrderByObservedAtDesc(beachId, type)
        } else {
            measurementRepository.findByBathingSiteIdOrderByObservedAtDesc(beachId)
        }
        return measurements.map { toMeasurementDto(it) }
    }

    fun listMunicipalities(): List<MunicipalityDto> {
        return municipalityRepository.findAll().map {
            MunicipalityDto(
                id = it.id!!.toString(),
                name = it.name,
                code = it.code,
                county = it.county
            )
        }
    }

    private fun toSummaryDto(
        site: BathingSite,
        snapshot: SiteStatusSnapshot?,
        municipality: Municipality?
    ): BeachSummaryDto {
        return BeachSummaryDto(
            id = site.id!!.toString(),
            name = site.name,
            slug = site.slug,
            municipalityName = municipality?.name,
            waterType = site.waterType.name,
            lat = site.lat,
            lon = site.lon,
            safeToSwim = snapshot?.safeToSwim ?: false,
            statusLabel = snapshot?.statusLabel?.name ?: StatusLabel.NO_DATA.name,
            waterQualityRating = snapshot?.waterQualityRating?.name ?: QualityRating.UNRATED.name,
            temperatureCelsius = snapshot?.temperatureCelsius?.toDouble(),
            trendDirection = snapshot?.trendDirection?.name ?: TrendDirection.UNKNOWN.name
        )
    }

    private fun toStatusDto(snapshot: SiteStatusSnapshot): BeachStatusDto {
        return BeachStatusDto(
            bathingSiteId = snapshot.bathingSiteId.toString(),
            computedAt = snapshot.computedAt.toString(),
            latestSampleAt = snapshot.latestSampleAt?.toString(),
            safeToSwim = snapshot.safeToSwim,
            statusLabel = snapshot.statusLabel.name,
            reasonCode = snapshot.reasonCode,
            waterQualityRating = snapshot.waterQualityRating.name,
            ratingBasis = snapshot.ratingBasis?.name,
            temperatureCelsius = snapshot.temperatureCelsius?.toDouble(),
            trendDirection = snapshot.trendDirection.name,
            trendChange7dCelsius = snapshot.trendChange7dCelsius?.toDouble()
        )
    }

    private fun toMeasurementDto(m: Measurement): MeasurementDto {
        return MeasurementDto(
            id = m.id!!.toString(),
            bathingSiteId = m.bathingSiteId.toString(),
            observedAt = m.observedAt.toString(),
            measurementType = m.measurementType.name,
            value = m.value.toDouble(),
            unit = m.unit.name,
            qualifier = m.qualifier.name,
            rawText = m.rawText
        )
    }
}
