import { Outlet, NavLink } from 'react-router-dom';
import { Map, List, Waves } from 'lucide-react';
import { clsx } from 'clsx';

export function Layout() {
  return (
    <div className="flex flex-col h-screen bg-slate-50">
      <header className="bg-white border-b border-slate-200 shadow-sm z-50 relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <div className="flex items-center gap-3">
              <div className="bg-sky-500 p-2 rounded-lg text-white shadow-sm ring-1 ring-sky-600/10">
                <Waves className="h-5 w-5" />
              </div>
              <span className="text-xl font-bold text-slate-900 tracking-tight">BadeVann</span>
            </div>
            
            <nav className="flex space-x-1 bg-slate-100 p-1 rounded-lg">
              <NavLink 
                to="/" 
                end
                className={({ isActive }) => clsx(
                  "flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-all duration-200",
                  isActive 
                    ? "bg-white text-sky-700 shadow-sm ring-1 ring-black/5" 
                    : "text-slate-600 hover:text-slate-900 hover:bg-slate-200/50"
                )}
              >
                <Map className="w-4 h-4" />
                <span className="hidden sm:inline">Kart</span>
              </NavLink>
              <NavLink 
                to="/badesteder" 
                className={({ isActive }) => clsx(
                  "flex items-center gap-2 px-4 py-2 rounded-md text-sm font-medium transition-all duration-200",
                  isActive 
                    ? "bg-white text-sky-700 shadow-sm ring-1 ring-black/5" 
                    : "text-slate-600 hover:text-slate-900 hover:bg-slate-200/50"
                )}
              >
                <List className="w-4 h-4" />
                <span className="hidden sm:inline">Badesteder</span>
              </NavLink>
            </nav>
          </div>
        </div>
      </header>

      <main className="flex-1 overflow-hidden relative">
        <Outlet />
      </main>
    </div>
  );
}
