import { useEffect, useState } from 'react'
import { estructuraApi } from '../services/api'
import ModuleHeader from '../components/ModuleHeader'
import FilterBar from '../components/FilterBar'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'
const empty = { idZona: null, nombre: '', descripcion: '' }
export default function Zonas() {
  const [rows, setRows] = useState([]), [q, setQ] = useState(''), [form, setForm] = useState(empty), [open, setOpen] = useState(false), [saving, setSaving] = useState(false), [error, setError] = useState('')
  const load = () => estructuraApi.zonas().then(setRows).catch(e => setError(e.message))
  useEffect(() => { load() }, [])
  const filtered = rows.filter(x => !q || `${x.nombre} ${x.descripcion}`.toLowerCase().includes(q.toLowerCase()))
  const save = async e => { e.preventDefault(); setSaving(true); try { form.idZona ? await estructuraApi.updateZona(form.idZona, form) : await estructuraApi.createZona(form); setOpen(false); setForm(empty); load() } catch (err) { setError(err.message) } finally { setSaving(false) } }
  return <><ModuleHeader title="Zonas comunes" description="Gestiona los espacios disponibles para reservas del conjunto." action={<button className="button primary" onClick={() => { setForm(empty); setOpen(true) }}>+ Nueva zona</button>} />{error && <div className="alert error">{error}</div>}<div className="dashboard-card"><FilterBar search={q} onSearch={setQ} onClear={() => setQ('')} /><div className="table-wrap">{filtered.length ? <table><thead><tr><th>Zona</th><th>Descripción</th><th></th></tr></thead><tbody>{filtered.map(r => <tr key={r.idZona}><td><strong>{r.nombre}</strong></td><td>{r.descripcion}</td><td className="actions"><button onClick={() => { setForm({ ...r }); setOpen(true) }}>Editar</button><button className="danger-text" onClick={() => window.confirm('¿Eliminar esta zona?') && estructuraApi.removeZona(r.idZona).then(load).catch(e => setError(e.message))}>Eliminar</button></td></tr>)}</tbody></table> : <EmptyState />}</div></div><Modal open={open} title={form.idZona ? 'Editar zona' : 'Nueva zona'} onClose={() => setOpen(false)} onSubmit={save} loading={saving}><div className="form-grid"><label>Nombre<input maxLength="50" value={form.nombre} onChange={e => setForm({ ...form, nombre: e.target.value })} required /></label><label>Descripción<input maxLength="200" value={form.descripcion} onChange={e => setForm({ ...form, descripcion: e.target.value })} required /></label></div></Modal></>
}
