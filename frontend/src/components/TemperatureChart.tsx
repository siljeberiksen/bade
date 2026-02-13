import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid } from 'recharts';
import type { Measurement } from '../types';

interface TemperatureChartProps {
  data: Measurement[];
}

export function TemperatureChart({ data }: TemperatureChartProps) {
  const tempData = data
    .filter((m) => m.measurementType === 'TEMPERATURE')
    .map((m) => ({
      ...m,
      date: new Date(m.observedAt),
    }))
    .sort((a, b) => a.date.getTime() - b.date.getTime());

  if (tempData.length === 0) return <div className="text-slate-500 text-sm">Ingen temperaturdata tilgjengelig</div>;

  return (
    <div className="h-64 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={tempData} margin={{ top: 5, right: 20, bottom: 5, left: 0 }}>
          <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e2e8f0" />
          <XAxis
            dataKey="date"
            tickFormatter={(date: any) => new Date(date).toLocaleDateString('no-NO', { day: '2-digit', month: '2-digit' })}
            stroke="#94a3b8"
            tick={{ fontSize: 12 }}
            tickLine={false}
            axisLine={false}
          />
          <YAxis
            stroke="#94a3b8"
            tick={{ fontSize: 12 }}
            tickLine={false}
            axisLine={false}
            unit="°C"
            width={40}
          />
          <Tooltip
            contentStyle={{ backgroundColor: '#fff', borderRadius: '8px', border: 'none', boxShadow: '0 4px 6px -1px rgb(0 0 0 / 0.1)' }}
            labelFormatter={(label: any) => new Date(label).toLocaleDateString('no-NO', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' })}
          />
          <Line
            type="monotone"
            dataKey="value"
            stroke="#0ea5e9"
            strokeWidth={3}
            dot={false}
            activeDot={{ r: 6, fill: '#0ea5e9', stroke: '#fff', strokeWidth: 2 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
