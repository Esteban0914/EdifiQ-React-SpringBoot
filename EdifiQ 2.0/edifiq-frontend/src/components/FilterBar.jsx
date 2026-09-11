export default function FilterBar({ search, onSearch, filters = [], onClear }) {
  return <div className="filter-bar"><input className="search-input" value={search} onChange={(e) => onSearch(e.target.value)} placeholder="Buscar..." />{filters.map((f) => <select key={f.name} value={f.value} onChange={(e) => f.onChange(e.target.value)}><option value="">{f.placeholder}</option>{f.options.map((o) => <option key={o.value} value={o.value}>{o.label}</option>)}</select>)}{onClear && <button className="button ghost" onClick={onClear}>Limpiar</button>}</div>
}
