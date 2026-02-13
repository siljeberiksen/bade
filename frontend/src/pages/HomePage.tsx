import { useQuery } from '@tanstack/react-query';
import { beachesQuery } from '../queries/beaches';
import { BeachMap } from '../components/BeachMap';

export function HomePage() {
  const { data: beaches = [] } = useQuery(beachesQuery());

  return (
    <div className="h-[calc(100vh-64px)] w-full relative">
      <BeachMap 
        beaches={beaches} 
        className="h-full w-full z-0"
        center={[59.9, 10.75]}
        zoom={6}
      />
    </div>
  );
}
