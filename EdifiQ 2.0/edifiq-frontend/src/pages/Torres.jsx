import { useEffect, useState } from 'react'
import { estructuraApi } from '../services/api'
import ModuleHeader from '../components/ModuleHeader'
import FilterBar from '../components/FilterBar'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'

const empty = {
  idTorre: null, nombreTorre: '', cantidadTorres: 1, pisos: 5, apartamentosPorPiso: 4
}

export default function Torres() {
  const [rows, setRows] = useState([]), [apartments, setApartments] = useState([])
  const [q, setQ] = useState('')
  const [form, setForm] = useState(empty)
  const [open, setOpen] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const load = async () => { try { const [torres, aptos] = await Promise.all([estructuraApi.torres(), estructuraApi.apartamentos()]); setRows(torres); setApartments(aptos) } catch (e) { setError(e.message) } }
  useEffect(() => {
    let cancelled = false
    const loadInitialData = async () => {
      try {
        const [torres, apartamentos] = await Promise.all([estructuraApi.torres(), estructuraApi.apartamentos()])
        if (!cancelled) {
          setRows(torres)
          setApartments(apartamentos)
        }
      } catch (e) {
        if (!cancelled) setError(e.message)
      }
    }
    void loadInitialData()
    return () => { cancelled = true }
  }, [])

  const filtered = rows.filter(x => !q || x.nombreTorre.toLowerCase().includes(q.toLowerCase()))

  const save = async e => {
    e.preventDefault()
    setSaving(true); setError('')
    try {
      form.idTorre ? await estructuraApi.updateTorre(form.idTorre, form) : await estructuraApi.createTorre(form)
      setOpen(false); setForm(empty); await load()
    } catch (err) { setError(err.message) } finally { setSaving(false) }
  }

  const edit = r => {
    setForm({ ...empty, idTorre: r.idTorre, nombreTorre: r.nombreTorre, cantidadTorres: 1 })
    setOpen(true)
  }

  return <>
    <ModuleHeader
      title="Torres y apartamentos"
      description="Crea torres de forma individual o masiva. Cada torre genera automáticamente sus apartamentos."
      action={<button className="button primary" onClick={() => { setForm(empty); setError(''); setOpen(true) }}>+ Nueva torre</button>}
    />
    {error && <div className="alert error">{error}</div>}

    <div className="structure-summary">
      <div><strong>{rows.length}</strong><span>Torres registradas</span></div>
      <div><strong>{apartments.length}</strong><span>Apartamentos registrados</span></div>
      <div><strong>5 × 4</strong><span>Configuración rápida predeterminada</span></div>
    </div>

    <div className="dashboard-card">
      <FilterBar search={q} onSearch={setQ} onClear={() => setQ('')} />
      <div className="table-wrap">
        {filtered.length ? <table><thead><tr><th>Nombre</th><th>Apartamentos</th><th>Creación</th><th></th></tr></thead>
        <tbody>{filtered.map(r => <tr key={r.idTorre}>
          <td><strong>{r.nombreTorre}</strong></td>
          <td><span className="status success">Generados automáticamente</span></td>
          <td>{String(r.fechaCreacion || '').replace('T', ' ').slice(0, 16)}</td>
          <td className="actions">
            <button onClick={() => edit(r)}>Editar</button>
            <button className="danger-text" onClick={() => window.confirm('¿Eliminar esta torre?') && estructuraApi.removeTorre(r.idTorre).then(load).catch(e => setError(e.message))}>Eliminar</button>
          </td>
        </tr>)}</tbody></table> : <EmptyState />}
      </div>
    </div>

    <Modal open={open} title={form.idTorre ? 'Editar torre' : 'Crear torres y apartamentos'} onClose={() => setOpen(false)} onSubmit={save} loading={saving}>
      <div className="form-grid">
        <label>Nombre / prefijo
          <input maxLength="20" value={form.nombreTorre} onChange={e => setForm({ ...form, nombreTorre: e.target.value })} placeholder="Ej. Torre" required />
        </label>
        {!form.idTorre && <label>Cantidad de torres
          <input type="number" min="1" max="50" value={form.cantidadTorres} onChange={e => setForm({ ...form, cantidadTorres: Math.max(1, Math.min(50, Number(e.target.value) || 1)) })} />
        </label>}
        {!form.idTorre && <label>Pisos
          <input type="number" min="1" max="26" value={form.pisos} onChange={e => setForm({ ...form, pisos: Math.max(1, Math.min(26, Number(e.target.value) || 1)) })} />
        </label>}
        {!form.idTorre && <label>Apartamentos por piso
          <input type="number" min="1" max="20" value={form.apartamentosPorPiso} onChange={e => setForm({ ...form, apartamentosPorPiso: Math.max(1, Math.min(20, Number(e.target.value) || 1)) })} />
        </label>}
      </div>
      {!form.idTorre && <div className="generation-preview">
        <strong>Vista previa</strong>
        <p>{form.cantidadTorres} torre(s) × {form.pisos} piso(s) × {form.apartamentosPorPiso} apartamento(s) = <b>{form.cantidadTorres * form.pisos * form.apartamentosPorPiso}</b> apartamentos.</p>
        <small>Con 5 pisos y 4 apartamentos: piso 1 → 101–104, piso 2 → 201–204, piso 3 → 301–304, piso 4 → 401–404 y piso 5 → 501–504.</small>
      </div>}
    </Modal>
  </>
}
