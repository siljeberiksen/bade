package no.badevann.domain

enum class WaterType {
    FRESH, SALT, BRACKISH, UNKNOWN
}

enum class MeasurementType {
    E_COLI, ENTEROCOCCI, TEMPERATURE
}

enum class MeasurementUnit {
    CFU_PER_100ML, CELSIUS
}

enum class MeasurementQualifier {
    EXACT, LESS_THAN, GREATER_THAN, ESTIMATED
}

enum class DataSourceKind {
    MUNICIPALITY_HTML, MUNICIPALITY_API, GOOGLE_DOC, MANUAL
}

enum class ScrapeRunStatus {
    RUNNING, SUCCESS, PARTIAL, FAILED
}

enum class ScrapeTrigger {
    SCHEDULED, MANUAL, BACKFILL
}

enum class StatusLabel {
    SAFE, CAUTION, UNSAFE, NO_DATA
}

enum class QualityRating {
    EXCELLENT, GOOD, SUFFICIENT, POOR, UNRATED
}

enum class RatingBasis {
    EU_2006_7_EC, NORWEGIAN_LEGACY, LATEST_SAMPLE_ONLY
}

enum class TrendDirection {
    UP, DOWN, FLAT, UNKNOWN
}
