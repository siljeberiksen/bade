import { queryOptions } from '@tanstack/react-query';
import { api } from '../lib/api';
import type { BeachSummary, BeachDetail, BeachStatus, Measurement, Municipality } from '../types';

export const beachesQuery = (filters?: Record<string, string>) => queryOptions({
  queryKey: ['beaches', filters],
  queryFn: () => {
    const params = new URLSearchParams(filters);
    return api.get<BeachSummary[]>(`/v1/beaches?${params.toString()}`);
  },
});

export const beachDetailQuery = (id: string) => queryOptions({
  queryKey: ['beach', id],
  queryFn: () => api.get<BeachDetail>(`/v1/beaches/${id}`),
});

export const beachStatusQuery = (id: string) => queryOptions({
  queryKey: ['beach', id, 'status'],
  queryFn: () => api.get<BeachStatus>(`/v1/beaches/${id}/status`),
});

export const beachMeasurementsQuery = (id: string, type?: string) => queryOptions({
  queryKey: ['beach', id, 'measurements', type],
  queryFn: () => {
    const params = type ? `?type=${type}` : '';
    return api.get<Measurement[]>(`/v1/beaches/${id}/measurements${params}`);
  },
});

export const nearbyBeachesQuery = (lat: number, lon: number, radius: number = 10) => queryOptions({
  queryKey: ['beaches', 'nearby', lat, lon, radius],
  queryFn: () => api.get<BeachSummary[]>(`/v1/beaches/nearest?lat=${lat}&lon=${lon}&radius=${radius}`),
});

export const municipalitiesQuery = () => queryOptions({
  queryKey: ['municipalities'],
  queryFn: () => api.get<Municipality[]>('/v1/municipalities'),
});
