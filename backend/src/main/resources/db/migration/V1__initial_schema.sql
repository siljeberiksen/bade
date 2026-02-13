-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- ============================================================
-- Municipality
-- ============================================================
CREATE TABLE municipality (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    code        VARCHAR(10)  NOT NULL UNIQUE,  -- e.g. "0301" for Oslo
    county      VARCHAR(255),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================
-- Bathing Site
-- ============================================================
CREATE TYPE water_type AS ENUM ('FRESH', 'SALT', 'BRACKISH', 'UNKNOWN');

CREATE TABLE bathing_site (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    municipality_id UUID         NOT NULL REFERENCES municipality(id),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(255) NOT NULL,
    water_type      water_type   NOT NULL DEFAULT 'UNKNOWN',
    lat             DOUBLE PRECISION NOT NULL,
    lon             DOUBLE PRECISION NOT NULL,
    geog            GEOGRAPHY(POINT, 4326) GENERATED ALWAYS AS (
                        ST_SetSRID(ST_MakePoint(lon, lat), 4326)::geography
                    ) STORED,
    is_active       BOOLEAN      NOT NULL DEFAULT true,
    public_notes    TEXT,
    source_display_name VARCHAR(255),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_site_name_municipality UNIQUE(name, municipality_id)
);

CREATE INDEX idx_bathing_site_geog ON bathing_site USING GIST(geog);
CREATE INDEX idx_bathing_site_municipality ON bathing_site(municipality_id);
CREATE INDEX idx_bathing_site_slug ON bathing_site(slug);

-- ============================================================
-- Data Source
-- ============================================================
CREATE TYPE data_source_kind AS ENUM ('MUNICIPALITY_HTML', 'MUNICIPALITY_API', 'GOOGLE_DOC', 'MANUAL');

CREATE TABLE data_source (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    kind           data_source_kind NOT NULL,
    owner          VARCHAR(255)     NOT NULL,  -- e.g. "Oslo kommune"
    base_url       TEXT,
    auth_required  BOOLEAN NOT NULL DEFAULT false,
    notes          TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ============================================================
-- Scraper
-- ============================================================
CREATE TABLE scraper (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL UNIQUE,
    source_id   UUID         NOT NULL REFERENCES data_source(id),
    version     VARCHAR(50)  NOT NULL DEFAULT '1.0.0',
    enabled     BOOLEAN      NOT NULL DEFAULT true,
    schedule    VARCHAR(100),  -- cron expression, nullable for on-demand only
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ============================================================
-- Scrape Run (provenance)
-- ============================================================
CREATE TYPE scrape_run_status AS ENUM ('RUNNING', 'SUCCESS', 'PARTIAL', 'FAILED');
CREATE TYPE scrape_trigger    AS ENUM ('SCHEDULED', 'MANUAL', 'BACKFILL');

CREATE TABLE scrape_run (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scraper_id   UUID             NOT NULL REFERENCES scraper(id),
    started_at   TIMESTAMPTZ      NOT NULL DEFAULT now(),
    finished_at  TIMESTAMPTZ,
    status       scrape_run_status NOT NULL DEFAULT 'RUNNING',
    trigger      scrape_trigger   NOT NULL DEFAULT 'MANUAL',
    message      TEXT,
    sites_found  INT DEFAULT 0,
    measurements_saved INT DEFAULT 0,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_scrape_run_scraper ON scrape_run(scraper_id);
CREATE INDEX idx_scrape_run_status ON scrape_run(status);

-- ============================================================
-- Scrape Artifact (raw HTML/response storage for debugging)
-- ============================================================
CREATE TABLE scrape_artifact (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    scrape_run_id  UUID         NOT NULL REFERENCES scrape_run(id) ON DELETE CASCADE,
    url            TEXT         NOT NULL,
    fetched_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    http_status    INT,
    content_type   VARCHAR(255),
    charset        VARCHAR(50),
    body_sha256    VARCHAR(64),
    body           TEXT,  -- raw HTML/JSON response
    parse_warnings JSONB
);

CREATE INDEX idx_scrape_artifact_run ON scrape_artifact(scrape_run_id);

-- ============================================================
-- Measurement (source of truth)
-- ============================================================
CREATE TYPE measurement_type AS ENUM ('E_COLI', 'ENTEROCOCCI', 'TEMPERATURE');
CREATE TYPE measurement_unit AS ENUM ('CFU_PER_100ML', 'CELSIUS');
CREATE TYPE measurement_qualifier AS ENUM ('EXACT', 'LESS_THAN', 'GREATER_THAN', 'ESTIMATED');

CREATE TABLE measurement (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bathing_site_id  UUID              NOT NULL REFERENCES bathing_site(id),
    source_id        UUID              NOT NULL REFERENCES data_source(id),
    scrape_run_id    UUID              REFERENCES scrape_run(id),
    observed_at      TIMESTAMPTZ       NOT NULL,
    measurement_type measurement_type  NOT NULL,
    value            DECIMAL(12,4)     NOT NULL,
    unit             measurement_unit  NOT NULL,
    qualifier        measurement_qualifier NOT NULL DEFAULT 'EXACT',
    raw_text         VARCHAR(255),
    sample_depth_cm  INT,
    source_external_id VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),

    CONSTRAINT uq_measurement UNIQUE(bathing_site_id, measurement_type, observed_at, source_id)
);

CREATE INDEX idx_measurement_site ON measurement(bathing_site_id);
CREATE INDEX idx_measurement_observed ON measurement(observed_at DESC);
CREATE INDEX idx_measurement_type ON measurement(measurement_type);
CREATE INDEX idx_measurement_site_type_observed ON measurement(bathing_site_id, measurement_type, observed_at DESC);

-- ============================================================
-- Site Status Snapshot (derived, materialized for fast reads)
-- ============================================================
CREATE TYPE status_label AS ENUM ('SAFE', 'CAUTION', 'UNSAFE', 'NO_DATA');
CREATE TYPE quality_rating AS ENUM ('EXCELLENT', 'GOOD', 'SUFFICIENT', 'POOR', 'UNRATED');
CREATE TYPE rating_basis AS ENUM ('EU_2006_7_EC', 'NORWEGIAN_LEGACY', 'LATEST_SAMPLE_ONLY');
CREATE TYPE trend_direction AS ENUM ('UP', 'DOWN', 'FLAT', 'UNKNOWN');

CREATE TABLE site_status_snapshot (
    id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bathing_site_id          UUID UNIQUE NOT NULL REFERENCES bathing_site(id),
    computed_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    latest_sample_at         TIMESTAMPTZ,
    safe_to_swim             BOOLEAN NOT NULL DEFAULT false,
    status_label             status_label NOT NULL DEFAULT 'NO_DATA',
    reason_code              VARCHAR(100),
    water_quality_rating     quality_rating NOT NULL DEFAULT 'UNRATED',
    rating_basis             rating_basis,
    temperature_celsius      DECIMAL(5,2),
    trend_direction          trend_direction NOT NULL DEFAULT 'UNKNOWN',
    trend_change_7d_celsius  DECIMAL(5,2)
);

CREATE INDEX idx_status_snapshot_site ON site_status_snapshot(bathing_site_id);

-- ============================================================
-- EU Classification Result (4-year rolling, versioned)
-- ============================================================
CREATE TABLE eu_classification_result (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bathing_site_id     UUID         NOT NULL REFERENCES bathing_site(id),
    season_year         INT          NOT NULL,
    window_start        DATE         NOT NULL,
    window_end          DATE         NOT NULL,
    n_samples           INT          NOT NULL,
    water_type          water_type   NOT NULL,
    rating              quality_rating NOT NULL DEFAULT 'UNRATED',
    ecoli_p95           DECIMAL(12,4),
    enterococci_p95     DECIMAL(12,4),
    ecoli_p90           DECIMAL(12,4),
    enterococci_p90     DECIMAL(12,4),
    algorithm_version   VARCHAR(20)  NOT NULL DEFAULT '1.0.0',
    computed_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_eu_classification UNIQUE(bathing_site_id, season_year, algorithm_version)
);

CREATE INDEX idx_eu_classification_site ON eu_classification_result(bathing_site_id);
