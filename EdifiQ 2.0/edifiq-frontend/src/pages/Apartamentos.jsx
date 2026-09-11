import { useEffect, useState } from 'react'
import { estructuraApi } from '../services/api'
import ModuleHeader from '../components/ModuleHeader'
import FilterBar from '../components/FilterBar'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'
const empty = { idApartamento: null, numeroApartamento: '', piso: 1, idTorre: '', activo: true }
export default function Apartamentos() {
  const [rows, setRows] = useState([]), [torres, setTorres] = useState([]), [filters, setFilters] = useState({ q: '', torre: '', estado: '' }), [form, setForm] = useState(empty), [open, setOpen] = useState(false), [saving, setSaving] = useState(false), [error, setError] = useState('')
  const load = async () => { try { const [a, t] = await Promise.all([estructuraApi.apartamentos(), estructuraApi.torres()]); setRows(a); setTorres(t) } catch (e) { setError(e.message) } }
  useEffect(() => {
    let cancelled = false
    const loadInitialData = async () => {
      try {
        const [apartamentos, torresData] = await Promise.all([estructuraApi.apartamentos(), estructuraApi.torres()])
        if (!cancelled) {
          setRows(apartamentos)
          setTorres(torresData)
        }
      } catch (e) {
        if (!cancelled) setError(e.message)
      }
    }
    void loadInitialData()
    return () => { cancelled = true }
  }, [])
  const filtered = rows.filter(x => (!filters.q || x.numeroApartamento.toLowerCase().includes(filters.q.toLowerCase())) && (!filters.torre || String(x.idTorre) === String(filters.torre)) && (filters.estado === '' || String(x.activo ? 1 : 0) === filters.estado))
  const save = async e => { e.preventDefault(); setSaving(true); try { form.idApartamento ? await estructuraApi.updateApartamento(form.idApartamento, form) : await estructuraApi.createApartamento(form); setOpen(false); setForm(empty); load() } catch (err) { setError(err.message) } finally { setSaving(false) } }
  return <><ModuleHeader title="Apartamentos" description="Administra apartamentos, pisos, torres y disponibilidad." action={<button className="button primary" onClick={() => { setForm(empty); setOpen(true) }}>+ Nuevo apartamento</button>} />{error && <div className="alert error">{error}</div>}<div className="dashboard-card"><FilterBar search={filters.q} onSearch={v => setFilters({ ...filters, q: v })} filters={[{ name: 'torre', value: filters.torre, placeholder: 'Todas las torres', onChange: v => setFilters({ ...filters, torre: v }), options: torres.map(x => ({ value: x.idTorre, label: x.nombreTorre })) }, { name: 'estado', value: filters.estado, placeholder: 'Todos los estados', onChange: v => setFilters({ ...filters, estado: v }), options: [{ value: '1', label: 'Activo' }, { value: '0', label: 'Inactivo' }] }]} onClear={() => setFilters({ q: '', torre: '', estado: '' })} /><div className="table-wrap">{filtered.length ? <table><thead><tr><th>Apartamento</th><th>Piso</th><th>Torre</th><th>Estado</th><th></th></tr></thead><tbody>{filtered.map(r => <tr key={r.idApartamento}><td><strong>{r.numeroApartamento}</strong></td><td>{r.piso}</td><td>{torres.find(t => t.idTorre === r.idTorre)?.nombreTorre || `Torre #${r.idTorre}`}</td><td><span className={`status ${r.activo ? 'success' : 'muted'}`}>{r.activo ? 'Activo' : 'Inactivo'}</span></td><td className="actions"><button onClick={() => { setForm({ ...r }); setOpen(true) }}>Editar</button><button onClick={() => estructuraApi.toggleApartamento(r.idApartamento).then(load).catch(e => setError(e.message))}>{r.activo ? 'Desactivar' : 'Activar'}</button><button className="danger-text" onClick={() => window.confirm('¿Eliminar este apartamento?') && estructuraApi.removeApartamento(r.idApartamento).then(load).catch(e => setError(e.message))}>Eliminar</button></td></tr>)}</tbody></table> : <EmptyState />}</div></div><Modal open={open} title={form.idApartamento ? 'Editar apartamento' : 'Nuevo apartamento'} onClose={() => setOpen(false)} onSubmit={save} loading={saving}><div className="form-grid"><label>Número<input maxLength="10" inputMode="numeric" pattern="[0-9]+" value={form.numeroApartamento} onChange={e => setForm({ ...form, numeroApartamento: e.target.value })} required /></label><label>Piso<input type="number" min="0" value={form.piso} onChange={e => setForm({ ...form, piso: e.target.value })} required /></label><label>Torre<select value={form.idTorre} onChange={e => setForm({ ...form, idTorre: e.target.value })} required><option value="">Seleccione</option>{torres.map(x => <option key={x.idTorre} value={x.idTorre}>{x.nombreTorre}</option>)}</select></label></div></Modal></>
}
