import { twMerge } from 'tailwind-merge';

interface StatusBadgeProps {
  status: 'SAFE' | 'CAUTION' | 'UNSAFE' | 'NO_DATA';
  className?: string;
}

export function StatusBadge({ status, className }: StatusBadgeProps) {
  const styles = {
    SAFE: 'bg-emerald-50 text-emerald-700 ring-emerald-600/20',
    CAUTION: 'bg-amber-50 text-amber-700 ring-amber-600/20',
    UNSAFE: 'bg-rose-50 text-rose-700 ring-rose-600/20',
    NO_DATA: 'bg-slate-50 text-slate-700 ring-slate-600/20',
  };

  const labels = {
    SAFE: 'God badekvalitet',
    CAUTION: 'Obs',
    UNSAFE: 'Frarådes',
    NO_DATA: 'Ingen data',
  };

  return (
    <span
      className={twMerge(
        'inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ring-1 ring-inset',
        styles[status],
        className
      )}
    >
      {labels[status]}
    </span>
  );
}
