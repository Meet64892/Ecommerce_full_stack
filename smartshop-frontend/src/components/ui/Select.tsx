/**
 * Select.tsx — Generic typed select (demonstrates generic components)
 */

export interface SelectOption<T extends string | number> {
  value: T;
  label: string;
}

export interface SelectProps<T extends string | number> {
  value: T;
  onChange: (value: T) => void;
  options: SelectOption<T>[];
  label?: string;
  className?: string;
}

/**
 * Select — Preserves option value type through generic T
 *
 * LEARNING NOTE: `T extends string | number` constrains generic to valid <option> values.
 */
export function Select<T extends string | number>({
  value,
  onChange,
  options,
  label,
  className,
}: SelectProps<T>) {
  return (
    <div className={className}>
      {label && <label className="mb-1 block text-sm font-medium text-slate-400">{label}</label>}
      <select
        value={String(value)}
        onChange={(e) => onChange(e.target.value as T)}
        className="input-glow w-full rounded-lg border border-slate-600/60 bg-surface-elevated px-3 py-2 text-sm text-slate-100 transition-all duration-200 hover:border-slate-500 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-500/25"
      >
        {options.map((opt) => (
          <option key={String(opt.value)} value={String(opt.value)}>
            {opt.label}
          </option>
        ))}
      </select>
    </div>
  );
}
