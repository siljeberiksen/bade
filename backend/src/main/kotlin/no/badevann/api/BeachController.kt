package no.badevann.api

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.serde.annotation.Serdeable
import no.badevann.api.dto.*
import no.badevann.domain.MeasurementType
import no.badevann.service.BeachService
import java.time.Instant
import java.util.UUID

@Controller("/v1")
class BeachController(
    private val beachService: BeachService
) {
    @Get("/beaches")
    fun listBeaches(
        @QueryValue municipalityId: String?,
        @QueryValue waterType: String?,
        @QueryValue status: String?,
        @QueryValue q: String?
    ): List<BeachSummaryDto> {
        return beachService.listBeaches(
            BeachFilters(
                municipalityId = municipalityId,
                waterType = waterType,
                status = status,
                q = q
            )
        )
    }

    @Get("/beaches/nearest")
    fun findNearest(
        @QueryValue lat: Double,
        @QueryValue lon: Double,
        @QueryValue(defaultValue = "50.0") radiusKm: Double,
        @QueryValue(defaultValue = "20") limit: Int
    ): List<BeachSummaryDto> {
        return beachService.findNearby(lat, lon, radiusKm, limit)
    }

    @Get("/beaches/{beachId}")
    fun getBeach(@PathVariable beachId: UUID): HttpResponse<BeachDetailDto> {
        val detail = beachService.getBeachDetail(beachId)
            ?: return HttpResponse.notFound()
        return HttpResponse.ok(detail)
    }

    @Get("/beaches/{beachId}/status")
    fun getBeachStatus(@PathVariable beachId: UUID): HttpResponse<BeachStatusDto> {
        val status = beachService.getBeachStatus(beachId)
            ?: return HttpResponse.notFound()
        return HttpResponse.ok(status)
    }

    @Get("/beaches/{beachId}/measurements")
    fun getMeasurements(
        @PathVariable beachId: UUID,
        @QueryValue type: String?,
        @QueryValue from: String?,
        @QueryValue to: String?
    ): List<MeasurementDto> {
        val measurementType = type?.let { MeasurementType.valueOf(it) }
        val fromInstant = from?.let { Instant.parse(it) }
        val toInstant = to?.let { Instant.parse(it) }
        return beachService.getMeasurements(beachId, measurementType, fromInstant, toInstant)
    }

    @Get("/municipalities")
    fun listMunicipalities(): List<MunicipalityDto> {
        return beachService.listMunicipalities()
    }

    @Get("/meta")
    fun getMeta(): MetaDto {
        return MetaDto(
            statusLabels = no.badevann.domain.StatusLabel.entries.map { it.name },
            qualityRatings = no.badevann.domain.QualityRating.entries.map { it.name },
            waterTypes = no.badevann.domain.WaterType.entries.map { it.name },
            measurementTypes = no.badevann.domain.MeasurementType.entries.map { it.name },
            trendDirections = no.badevann.domain.TrendDirection.entries.map { it.name },
            availableScrapers = emptyList()
        )
    }
}
