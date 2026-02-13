export interface BeachSummary {
  id: string;
  name: string;
  slug: string;
  municipalityName: string | null;
  waterType: string;
  lat: number;
  lon: number;
  safeToSwim: boolean;
  statusLabel: StatusLabel;
  waterQualityRating: string;
  temperatureCelsius: number | null;
  trendDirection: string;
}

export interface BeachDetail {
  id: string;
  name: string;
  slug: string;
  municipalityId: string;
  municipalityName: string | null;
  waterType: string;
  lat: number;
  lon: number;
  isActive: boolean;
  publicNotes: string | null;
  status: BeachStatus | null;
  latestMeasurements: Measurement[];
}

export interface BeachStatus {
  bathingSiteId: string;
  computedAt: string;
  latestSampleAt: string | null;
  safeToSwim: boolean;
  statusLabel: StatusLabel;
  reasonCode: string | null;
  waterQualityRating: string;
  ratingBasis: string | null;
  temperatureCelsius: number | null;
  trendDirection: string;
  trendChange7dCelsius: number | null;
}

export type StatusLabel = 'SAFE' | 'CAUTION' | 'UNSAFE' | 'NO_DATA';

export interface Measurement {
  id: string;
  bathingSiteId: string;
  observedAt: string;
  measurementType: string;
  value: number;
  unit: string;
  qualifier: string;
  rawText: string | null;
}

export interface Municipality {
  id: string;
  name: string;
  code: string;
  county: string | null;
}
