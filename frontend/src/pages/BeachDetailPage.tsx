import { useQuery } from '@tanstack/react-query';
import { useParams, Link } from 'react-router-dom';
import { beachDetailQuery } from '../queries/beaches';
import { StatusBadge } from '../components/StatusBadge';
import { TemperatureChart } from '../components/TemperatureChart';
import { BeachMap } from '../components/BeachMap';
import { MapPin, Calendar, Activity, Droplets, ArrowLeft } from 'lucide-react';
import type { BeachSummary } from '../types';

export function BeachDetailPage() {
  const { beachId } = useParams<{ beachId: string }>();
  if (!beachId) return null;

  const { data: beach, isLoading } = useQuery(beachDetailQuery(beachId));

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse space-y-8">
          <div className="h-12 bg-slate-200 rounded-lg w-1/2"></div>
          <div className="h-64 bg-slate-200 rounded-xl"></div>
        </div>
      </div>
    );
  }

  if (!beach) return <div className="p-8 text-center">Badestrand ikke funnet</div>;

  const status = beach.status;
  const measurements = beach.latestMeasurements;

  const beachSummary: BeachSummary = {
    ...beach,
    municipalityName: beach.municipalityName,
    waterType: beach.waterType,
    lat: beach.lat,
    lon: beach.lon,
    safeToSwim: status?.safeToSwim ?? false,
    statusLabel: status?.statusLabel ?? 'NO_DATA',
    waterQualityRating: status?.waterQualityRating ?? 'UNRATED',
    temperatureCelsius: status?.temperatureCelsius ?? null,
    trendDirection: status?.trendDirection ?? 'UNKNOWN',
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <Link to="/badesteder" className="inline-flex items-center text-sm font-medium text-slate-500 hover:text-sky-600 transition-colors">
        <ArrowLeft className="w-4 h-4 mr-1" />
        Tilbake til badesteder
      </Link>

      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 text-slate-500 text-sm mb-1">
            <MapPin className="w-4 h-4" />
            <span>{beach.municipalityName || 'Ukjent kommune'}</span>
            <span className={`px-2 py-0.5 rounded-full text-xs font-medium ml-2 ${
                beach.waterType === 'SALT' ? 'bg-blue-50 text-blue-700' : 'bg-teal-50 text-teal-700'
              }`}>
                {beach.waterType === 'SALT' ? 'Saltvann' : 'Ferskvann'}
              </span>
          </div>
          <h1 className="text-4xl font-bold text-slate-900 tracking-tight">{beach.name}</h1>
        </div>
        {status && <div className="flex items-center gap-3">
            <span className="text-slate-500 font-medium">Status:</span>
            <StatusBadge status={status.statusLabel} className="text-base px-4 py-1.5" />
          </div>}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-8">
          
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100 flex flex-col sm:flex-row gap-8">
            <div className="flex-1">
              <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Vannkvalitet</h3>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-bold text-slate-900">
                  {status?.waterQualityRating === 'EXCELLENT' ? 'Utmerket' :
                   status?.waterQualityRating === 'GOOD' ? 'God' :
                   status?.waterQualityRating === 'SUFFICIENT' ? 'Tilfredsstillende' :
                   status?.waterQualityRating === 'POOR' ? 'Dårlig' : 'Ikke vurdert'}
                </span>
              </div>
              <p className="text-slate-600 mt-2">
                {status?.safeToSwim 
                  ? 'Det er trygt å bade her basert på siste målinger.' 
                  : 'Bading frarådes på grunn av vannkvalitet.'}
              </p>
            </div>
            
            <div className="w-px bg-slate-100 hidden sm:block"></div>
            
            <div className="flex-1">
              <h3 className="text-sm font-semibold text-slate-500 uppercase tracking-wider mb-4">Vanntemperatur</h3>
              <div className="flex items-baseline gap-2">
                <span className="text-4xl font-bold text-sky-600">
                  {status?.temperatureCelsius ? status.temperatureCelsius.toFixed(1) : '--'}
                </span>
                <span className="text-xl text-slate-400">°C</span>
              </div>
              <p className="text-slate-500 text-sm mt-2 flex items-center gap-1">
                <Calendar className="w-4 h-4" />
                Sist målt: {status?.latestSampleAt ? new Date(status.latestSampleAt).toLocaleDateString() : 'Ukjent'}
              </p>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-100">
            <h3 className="text-lg font-bold text-slate-900 mb-6 flex items-center gap-2">
              <Activity className="w-5 h-5 text-sky-500" />
              Temperaturhistorikk
            </h3>
            <TemperatureChart data={measurements || []} />
          </div>

          <div className="bg-white rounded-2xl border border-slate-100 overflow-hidden shadow-sm">
             <div className="px-6 py-4 border-b border-slate-100">
                <h3 className="text-lg font-bold text-slate-900 flex items-center gap-2">
                  <Droplets className="w-5 h-5 text-sky-500" />
                  Siste målinger
                </h3>
             </div>
             <div className="overflow-x-auto">
               <table className="w-full text-sm text-left">
                 <thead className="text-xs text-slate-500 uppercase bg-slate-50 border-b border-slate-100">
                   <tr>
                     <th className="px-6 py-3 font-semibold">Dato</th>
                     <th className="px-6 py-3 font-semibold">Type</th>
                     <th className="px-6 py-3 font-semibold">Verdi</th>
                   </tr>
                 </thead>
                 <tbody className="divide-y divide-slate-100">
                   {measurements?.slice(0, 10).map((m) => (
                     <tr key={m.id} className="hover:bg-slate-50/50">
                       <td className="px-6 py-3 text-slate-600">
                         {new Date(m.observedAt).toLocaleString('no-NO')}
                       </td>
                       <td className="px-6 py-3 text-slate-900 font-medium">
                         {m.measurementType === 'TEMPERATURE' ? 'Temperatur' : 
                          m.measurementType === 'E_COLI' ? 'E. Coli' : 
                          m.measurementType === 'ENTEROCOCCI' ? 'Enterokokker' : m.measurementType}
                       </td>
                       <td className="px-6 py-3 text-slate-600">
                         {m.value} {m.unit}
                       </td>
                     </tr>
                   ))}
                   {!measurements?.length && (
                     <tr>
                       <td colSpan={3} className="px-6 py-8 text-center text-slate-500">
                         Ingen målinger tilgjengelig.
                       </td>
                     </tr>
                   )}
                 </tbody>
               </table>
             </div>
          </div>

        </div>

        <div className="space-y-6">
           <div className="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden h-80 sticky top-6">
             <BeachMap 
               beaches={[beachSummary]} 
               center={[beach.lat, beach.lon]} 
               zoom={13} 
               className="h-full w-full"
             />
             <div className="absolute bottom-2 left-2 bg-white/90 backdrop-blur px-2 py-1 rounded text-xs text-slate-600 z-[500] border border-slate-200">
               {beach.lat.toFixed(4)}, {beach.lon.toFixed(4)}
             </div>
           </div>
        </div>
      </div>
    </div>
  );
}
