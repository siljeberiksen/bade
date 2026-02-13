package no.badevann.service

import jakarta.inject.Singleton
import no.badevann.domain.*
import no.badevann.repository.BathingSiteRepository
import no.badevann.repository.MeasurementRepository
import no.badevann.repository.SiteStatusSnapshotRepository
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Singleton
class StatusCalculationService(
    private val measurementRepository: MeasurementRepository,
    private val snapshotRepository: SiteStatusSnapshotRepository,
    private val siteRepository: BathingSiteRepository
) {
    private val log = LoggerFactory.getLogger(StatusCalculationService::class.java)

    companion object {
        private val STALE_THRESHOLD = Duration.ofDays(14)

        private val FRESH_ECOLI_SAFE = BigDecimal(1000)
        private val FRESH_ENTEROCOCCI_SAFE = BigDecimal(400)
        private val SALT_ECOLI_SAFE = BigDecimal(500)
        private val SALT_ENTEROCOCCI_SAFE = BigDecimal(200)
    }

    fun computeStatusForSite(siteId: UUID): SiteStatusSnapshot {
        val site = siteRepository.findById(siteId).orElse(null)
            ?: throw IllegalArgumentException("Site not found: $siteId")

        val latestEcoli = measurementRepository.findLatestBySiteAndType(siteId, MeasurementType.E_COLI)
        val latestEnterococci = measurementRepository.findLatestBySiteAndType(siteId, MeasurementType.ENTEROCOCCI)
        val latestTemp = measurementRepository.findLatestBySiteAndType(siteId, MeasurementType.TEMPERATURE)

        val latestSampleAt = listOfNotNull(
            latestEcoli?.observedAt,
            latestEnterococci?.observedAt,
            latestTemp?.observedAt
        ).maxOrNull()

        val isStale = latestSampleAt == null ||
            Duration.between(latestSampleAt, Instant.now()) > STALE_THRESHOLD

        val (safeToSwim, statusLabel, reasonCode) = evaluateSafety(
            site.waterType, latestEcoli, latestEnterococci, isStale
        )

        val trendResult = calculateTemperatureTrend(siteId)

        val snapshot = SiteStatusSnapshot(
            bathingSiteId = siteId,
            computedAt = Instant.now(),
            latestSampleAt = latestSampleAt,
            safeToSwim = safeToSwim,
            statusLabel = statusLabel,
            reasonCode = reasonCode,
            waterQualityRating = QualityRating.UNRATED,
            ratingBasis = RatingBasis.LATEST_SAMPLE_ONLY,
            temperatureCelsius = latestTemp?.value,
            trendDirection = trendResult.first,
            trendChange7dCelsius = trendResult.second
        )

        val existing = snapshotRepository.findByBathingSiteId(siteId)
        return if (existing != null) {
            snapshotRepository.update(snapshot.copy(id = existing.id))
        } else {
            snapshotRepository.save(snapshot)
        }
    }

    private fun evaluateSafety(
        waterType: WaterType,
        ecoli: Measurement?,
        enterococci: Measurement?,
        isStale: Boolean
    ): Triple<Boolean, StatusLabel, String?> {
        if (ecoli == null && enterococci == null) {
            return Triple(false, StatusLabel.NO_DATA, "NO_RECENT_SAMPLE")
        }

        if (isStale) {
            return Triple(false, StatusLabel.CAUTION, "SAMPLE_STALE")
        }

        val (ecoliThreshold, entThreshold) = when (waterType) {
            WaterType.SALT, WaterType.BRACKISH -> SALT_ECOLI_SAFE to SALT_ENTEROCOCCI_SAFE
            else -> FRESH_ECOLI_SAFE to FRESH_ENTEROCOCCI_SAFE
        }

        val ecoliSafe = ecoli == null || ecoli.value <= ecoliThreshold
        val entSafe = enterococci == null || enterococci.value <= entThreshold

        return when {
            ecoliSafe && entSafe -> Triple(true, StatusLabel.SAFE, null)
            !ecoliSafe -> Triple(false, StatusLabel.UNSAFE, "E_COLI_HIGH")
            else -> Triple(false, StatusLabel.UNSAFE, "ENTEROCOCCI_HIGH")
        }
    }

    private fun calculateTemperatureTrend(siteId: UUID): Pair<TrendDirection, BigDecimal?> {
        val sevenDaysAgo = Instant.now().minus(Duration.ofDays(7))
        val temps = measurementRepository.findBySiteAndTypeInRange(
            siteId, MeasurementType.TEMPERATURE, sevenDaysAgo, Instant.now()
        )

        if (temps.size < 2) return TrendDirection.UNKNOWN to null

        val oldest = temps.last().value
        val newest = temps.first().value
        val change = newest - oldest

        val direction = when {
            change > BigDecimal("0.5") -> TrendDirection.UP
            change < BigDecimal("-0.5") -> TrendDirection.DOWN
            else -> TrendDirection.FLAT
        }

        return direction to change
    }

    fun recomputeAll() {
        val sites = siteRepository.findByIsActiveTrue()
        log.info("Recomputing status for ${sites.size} active sites")
        for (site in sites) {
            try {
                computeStatusForSite(site.id!!)
            } catch (e: Exception) {
                log.error("Failed to compute status for site ${site.name}", e)
            }
        }
    }
}
