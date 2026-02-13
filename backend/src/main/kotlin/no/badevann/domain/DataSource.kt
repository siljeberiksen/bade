package no.badevann.domain

import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.serde.annotation.Serdeable
import java.time.Instant
import java.util.UUID

@Serdeable
@MappedEntity("data_source")
data class DataSource(
    @field:Id
    @field:GeneratedValue(GeneratedValue.Type.UUID)
    val id: UUID? = null,
    val kind: DataSourceKind,
    val owner: String,
    val baseUrl: String? = null,
    val authRequired: Boolean = false,
    val notes: String? = null,
    val createdAt: Instant = Instant.now()
)
