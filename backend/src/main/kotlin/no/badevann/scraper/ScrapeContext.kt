package no.badevann.scraper

import no.badevann.domain.ScrapeArtifact
import no.badevann.repository.ScrapeArtifactRepository
import java.security.MessageDigest
import java.time.Instant
import java.util.UUID

class ScrapeContext(
    val scrapeRunId: UUID,
    private val artifactRepository: ScrapeArtifactRepository
) {
    private val warnings = mutableListOf<String>()

    fun storeArtifact(url: String, body: String, httpStatus: Int = 200, contentType: String = "text/html") {
        val sha256 = sha256(body)
        artifactRepository.save(
            ScrapeArtifact(
                scrapeRunId = scrapeRunId,
                url = url,
                fetchedAt = Instant.now(),
                httpStatus = httpStatus,
                contentType = contentType,
                charset = "UTF-8",
                bodySha256 = sha256,
                body = body
            )
        )
    }

    fun addWarning(warning: String) {
        warnings.add(warning)
    }

    fun getWarnings(): List<String> = warnings.toList()

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }
}
