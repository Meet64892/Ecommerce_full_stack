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
      {label && <label className="mb-1 block text-sm font-medium text-neutral-400">{label}</label>}
      <select
        value={String(value)}
        onChange={(e) => onChange(e.target.value as T)}
        className="w-full rounded-lg border border-neutral-700 bg-neutral-900 px-3 py-2 text-sm text-white focus:border-white focus:outline-none focus:ring-2 focus:ring-white/20"
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
