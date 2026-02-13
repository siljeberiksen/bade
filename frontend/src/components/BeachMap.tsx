import { MapContainer, TileLayer, CircleMarker, Popup } from 'react-leaflet';
import type { BeachSummary } from '../types';
import { Link } from 'react-router-dom';
import { StatusBadge } from './StatusBadge';

interface BeachMapProps {
  beaches: BeachSummary[];
  center?: [number, number];
  zoom?: number;
  className?: string;
}

const statusColors: Record<string, string> = {
  SAFE: '#10b981',
  CAUTION: '#f59e0b',
  UNSAFE: '#ef4444',
  NO_DATA: '#94a3b8',
};

export function BeachMap({ beaches, center = [59.91, 10.75], zoom = 6, className }: BeachMapProps) {
  return (
    <MapContainer
      center={center}
      zoom={zoom}
      className={className}
      scrollWheelZoom={true}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      {beaches.map((beach) => (
        <CircleMarker
          key={beach.id}
          center={[beach.lat, beach.lon]}
          radius={8}
          pathOptions={{
            color: '#fff',
            weight: 2,
            fillColor: statusColors[beach.statusLabel] || statusColors.NO_DATA,
            fillOpacity: 1,
          }}
        >
          <Popup>
            <div className="min-w-[200px] p-2">
              <h3 className="text-lg font-bold text-slate-900 mb-1">{beach.name}</h3>
              <p className="text-sm text-slate-600 mb-2">{beach.municipalityName}</p>
              
              <div className="flex items-center gap-2 mb-3">
                <StatusBadge status={beach.statusLabel} />
                {beach.temperatureCelsius !== null && (
                  <span className="text-sm font-medium text-slate-700">
                    {beach.temperatureCelsius.toFixed(1)}°C
                  </span>
                )}
              </div>

              <Link
                to={`/badesteder/${beach.id}`}
                className="block w-full text-center rounded-md bg-sky-600 px-3 py-2 text-sm font-semibold text-white shadow-sm hover:bg-sky-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-sky-600"
              >
                Se detaljer
              </Link>
            </div>
          </Popup>
        </CircleMarker>
      ))}
    </MapContainer>
  );
}
