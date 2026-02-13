import { useQuery } from '@tanstack/react-query';
import { useSearchParams, Link } from 'react-router-dom';
import { beachesQuery, municipalitiesQuery } from '../queries/beaches';
import { StatusBadge } from '../components/StatusBadge';
import { MapPin, Thermometer, Waves, Search } from 'lucide-react';
import { useState, useEffect } from 'react';

export function BeachListPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const { data: beaches = [], isLoading } = useQuery(beachesQuery());
  const { data: municipalities = [] } = useQuery(municipalitiesQuery());

  const q = searchParams.get('q') || '';
  const municipality = searchParams.get('municipality') || 'all';
  const waterType = searchParams.get('waterType') || 'all';
  const status = searchParams.get('status') || 'all';

  const [searchInput, setSearchInput] = useState(q);

  useEffect(() => {
    const timer = setTimeout(() => {
      setSearchParams((prev) => {
        if (searchInput) prev.set('q', searchInput);
        else prev.delete('q');
        return prev;
      });
    }, 300);
    return () => clearTimeout(timer);
  }, [searchInput, setSearchParams]);

  const filteredBeaches = beaches.filter((beach) => {
    const matchesSearch = beach.name.toLowerCase().includes(q.toLowerCase());
    
    const muniName = municipalities.find(m => m.id === municipality)?.name;
    const matchesMuni = municipality === 'all' || (muniName && beach.municipalityName === muniName);

    const matchesWaterType = waterType === 'all' || beach.waterType === waterType;
    const matchesStatus = status === 'all' || beach.statusLabel === status;

    return matchesSearch && matchesMuni && matchesWaterType && matchesStatus;
  });

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse space-y-8">
          <div className="h-12 bg-slate-200 rounded-lg w-full max-w-md"></div>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {[...Array(6)].map((_, i) => (
              <div key={i} className="h-48 bg-slate-200 rounded-xl"></div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      <div className="flex flex-col gap-6">
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
          <h1 className="text-3xl font-bold text-slate-900 tracking-tight">
            Badesteder <span className="text-slate-400 text-lg font-normal ml-2">({filteredBeaches.length})</span>
          </h1>
        </div>

        <div className="bg-white p-4 rounded-xl border border-slate-200 shadow-sm space-y-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
            <input
              type="text"
              placeholder="Søk etter badestrand..."
              className="w-full pl-10 pr-4 py-2 rounded-lg border border-slate-300 focus:ring-2 focus:ring-sky-500 focus:border-transparent outline-none transition-all"
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <select
              className="w-full rounded-lg border border-slate-300 py-2 px-3 focus:ring-2 focus:ring-sky-500 outline-none bg-white"
              value={municipality}
              onChange={(e) => setSearchParams(prev => { prev.set('municipality', e.target.value); return prev; })}
            >
              <option value="all">Alle kommuner</option>
              {municipalities.map(m => (
                <option key={m.id} value={m.id}>{m.name}</option>
              ))}
            </select>

            <select
              className="w-full rounded-lg border border-slate-300 py-2 px-3 focus:ring-2 focus:ring-sky-500 outline-none bg-white"
              value={waterType}
              onChange={(e) => setSearchParams(prev => { prev.set('waterType', e.target.value); return prev; })}
            >
              <option value="all">Alle vanntyper</option>
              <option value="SALT">Saltvann</option>
              <option value="FRESH">Ferskvann</option>
            </select>

            <select
              className="w-full rounded-lg border border-slate-300 py-2 px-3 focus:ring-2 focus:ring-sky-500 outline-none bg-white"
              value={status}
              onChange={(e) => setSearchParams(prev => { prev.set('status', e.target.value); return prev; })}
            >
              <option value="all">Alle statuser</option>
              <option value="SAFE">Trygt å bade</option>
              <option value="CAUTION">Obs</option>
              <option value="UNSAFE">Frarådes</option>
            </select>
          </div>
        </div>
      </div>

      {filteredBeaches.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredBeaches.map((beach) => (
            <Link
              key={beach.id}
              to={`/badesteder/${beach.id}`}
              className="group bg-white rounded-xl border border-slate-200 shadow-sm hover:shadow-md hover:border-sky-300 transition-all duration-200 overflow-hidden flex flex-col h-full"
            >
              <div className="p-5 flex-1 flex flex-col gap-4">
                <div className="flex justify-between items-start gap-2">
                  <div>
                    <h3 className="font-bold text-lg text-slate-900 group-hover:text-sky-700 transition-colors line-clamp-1">
                      {beach.name}
                    </h3>
                    <p className="text-sm text-slate-500 flex items-center mt-1">
                      <MapPin className="w-3.5 h-3.5 mr-1" />
                      {beach.municipalityName || 'Ukjent'}
                    </p>
                  </div>
                  <StatusBadge status={beach.statusLabel} />
                </div>

                <div className="mt-auto flex items-center justify-between pt-4 border-t border-slate-50">
                  <div className="flex items-center gap-3 text-sm">
                    <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${
                      beach.waterType === 'SALT' ? 'bg-blue-50 text-blue-700' : 'bg-teal-50 text-teal-700'
                    }`}>
                      {beach.waterType === 'SALT' ? 'Saltvann' : 'Ferskvann'}
                    </span>
                    {beach.temperatureCelsius != null && (
                      <div className="flex items-center font-medium text-slate-700">
                        <Thermometer className="w-4 h-4 mr-1 text-slate-400" />
                        {beach.temperatureCelsius.toFixed(1)}°C
                      </div>
                    )}
                  </div>
                </div>
              </div>
            </Link>
          ))}
        </div>
      ) : (
        <div className="text-center py-12 bg-white rounded-xl border border-slate-200 border-dashed">
          <Waves className="mx-auto h-12 w-12 text-slate-300 mb-4" />
          <h3 className="text-lg font-medium text-slate-900">Ingen badesteder funnet</h3>
          <p className="text-slate-500">Prøv å endre filtrene eller søkeordet.</p>
        </div>
      )}
    </div>
  );
}
