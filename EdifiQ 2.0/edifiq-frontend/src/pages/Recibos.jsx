import { useEffect, useState } from 'react'
import { catalogApi, recibosApi } from '../services/api'
import { useAuth } from '../context/useAuth'
import ModuleHeader from '../components/ModuleHeader'
import FilterBar from '../components/FilterBar'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'

const empty = {
  idRecibo: null, idTipoServicio: '', periodo: '', valor: 0,
  fechaEmision: new Date().toISOString().slice(0, 10), fechaVencimiento: '',
  idEstadoRecibo: 1, idApartamento: '', idTorre: '', destino: 'apartamento',
}

export default function Recibos() {
  const { user } = useAuth()
  const admin = user.rol === 'Administrador'
  const [rows, setRows] = useState([])
  const [cat, setCat] = useState({ tiposServicio: [], estadosRecibo: [], torres: [], apartamentos: [] })
  const [filters, setFilters] = useState({ q: '', estado: '', servicio: '', torre: '' })
  const [form, setForm] = useState(empty)
  const [selected, setSelected] = useState([])
  const [open, setOpen] = useState(false)
  const [saving, setSaving] = useState(false)
  const [sending, setSending] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const load = async () => {
    try {
      const [receipts, catalogs] = await Promise.all([
        recibosApi.list({ q: filters.q, estado: filters.estado || undefined, servicio: filters.servicio || undefined, torre: filters.torre || undefined }),
        catalogApi.all(),
      ])
      setRows(receipts)
      setCat(catalogs)
      setSelected([])
    } catch (e) { setError(e.message) }
  }

  useEffect(() => {
    const timer = setTimeout(load, 180)
    return () => clearTimeout(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filters.q, filters.estado, filters.servicio, filters.torre])

  const save = async (event) => {
    event.preventDefault(); setSaving(true); setError('')
    try {
      if (form.idRecibo) await recibosApi.update(form.idRecibo, form)
      else if (form.destino === 'torre') {
        const bulkBody = { ...form }
        delete bulkBody.destino
        delete bulkBody.idApartamento
        delete bulkBody.idRecibo
        await recibosApi.createBulk(bulkBody)
      }
      else {
        const body = { ...form }
        delete body.idTorre
        delete body.destino
        await recibosApi.create(body)
      }
      setOpen(false); await load()
    } catch (e) { setError(e.message) } finally { setSaving(false) }
  }

  const sendBulk = async () => {
    setSending(true); setError(''); setMessage('')
    try {
      const result = await recibosApi.sendBulk(selected)
      setMessage(`Se enviaron ${result.enviados} notificaciones.`); setSelected([])
    } catch (e) { setError(e.message) } finally { setSending(false) }
  }

  const toggleSelected = (id) => setSelected((current) => current.includes(id) ? current.filter((item) => item !== id) : [...current, id])
  const toggleAll = () => setSelected(selected.length === rows.length ? [] : rows.map((row) => row.id))
  let bulkLabel = 'Enviar seleccionados'
  if (sending) bulkLabel = 'Enviando...'
  else if (selected.length) bulkLabel += ` (${selected.length})`
  const edit = (row) => {
    setForm({ ...empty, idRecibo: row.id, idTipoServicio: row.idTipoServicio, periodo: row.periodo, valor: row.valor, fechaEmision: row.fechaEmision, fechaVencimiento: row.fechaVencimiento, idEstadoRecibo: row.idEstado, idApartamento: row.idApartamento })
    setOpen(true)
  }

  return <>
    <ModuleHeader title="Recibos" description="Control de cobros, vencimientos y pagos por servicio y apartamento." action={admin && <div className="actions">
      <button className="button" disabled={!selected.length || sending} onClick={sendBulk}>{bulkLabel}</button>
      <button className="button primary" onClick={() => { setForm(empty); setError(''); setOpen(true) }}>+ Nuevo recibo</button>
    </div>} />
    {error && <div className="alert error">{error}</div>}
    {message && <div className="alert success">{message}</div>}
    <div className="dashboard-card">
      <FilterBar search={filters.q} onSearch={(value) => setFilters({ ...filters, q: value })} filters={[
        { name: 'estado', value: filters.estado, placeholder: 'Estado', onChange: (value) => setFilters({ ...filters, estado: value }), options: cat.estadosRecibo.map((item) => ({ value: item.idEstadoRecibo, label: item.nombre })) },
        { name: 'servicio', value: filters.servicio, placeholder: 'Servicio', onChange: (value) => setFilters({ ...filters, servicio: value }), options: cat.tiposServicio.map((item) => ({ value: item.idTipoServicio, label: item.nombre })) },
        { name: 'torre', value: filters.torre, placeholder: 'Torre', onChange: (value) => setFilters({ ...filters, torre: value }), options: cat.torres.map((item) => ({ value: item.idTorre, label: item.nombreTorre })) },
      ]} onClear={() => setFilters({ q: '', estado: '', servicio: '', torre: '' })} />
      <div className="table-wrap">
        {rows.length === 0 ? <EmptyState /> : <table>
          <thead><tr>{admin && <th><input type="checkbox" aria-label="Seleccionar todos" checked={rows.length > 0 && selected.length === rows.length} onChange={toggleAll} /></th>}<th>Periodo</th><th>Servicio</th><th>Vencimiento</th><th>Apartamento</th><th>Estado</th><th></th></tr></thead>
          <tbody>{rows.map((row) => <tr key={row.id}>
            {admin && <td><input type="checkbox" aria-label={`Seleccionar recibo ${row.periodo}`} checked={selected.includes(row.id)} onChange={() => toggleSelected(row.id)} /></td>}
            <td><strong>{row.periodo}</strong><small>Emitido {row.fechaEmision}</small></td><td>{row.servicio}</td><td>{row.fechaVencimiento}</td><td>{row.apartamento}<small>{row.torre}</small></td><td><span className={`status ${row.idEstado === 2 ? 'success' : ''}`}>{row.estado}</span></td>
            <td className="actions">{admin && <><button onClick={() => edit(row)}>Editar</button><button className="danger-text" onClick={() => window.confirm('¿Eliminar este recibo?') && recibosApi.remove(row.id).then(load).catch((e) => setError(e.message))}>Eliminar</button></>}{!admin && row.idEstado !== 2 && <button className="button primary" onClick={() => recibosApi.pay(row.id).then(load).catch((e) => setError(e.message))}>Marcar pagado</button>}</td>
          </tr>)}</tbody>
        </table>}
      </div>
    </div>
    {admin && <Modal open={open} title={form.idRecibo ? 'Editar recibo' : 'Nuevo recibo'} onClose={() => setOpen(false)} onSubmit={save} loading={saving}><div className="form-grid">
      <label>Servicio<select value={form.idTipoServicio} onChange={(e) => setForm({ ...form, idTipoServicio: e.target.value })} required><option value="">Seleccione</option>{cat.tiposServicio.map((item) => <option key={item.idTipoServicio} value={item.idTipoServicio}>{item.nombre}</option>)}</select></label>
      <label>Periodo<input value={form.periodo} onChange={(e) => setForm({ ...form, periodo: e.target.value })} placeholder="Ej. Septiembre 2026" required /></label>
      <label>Fecha de emisión<input type="date" value={form.fechaEmision} onChange={(e) => setForm({ ...form, fechaEmision: e.target.value })} required /></label>
      <label>Fecha de vencimiento<input type="date" value={form.fechaVencimiento} onChange={(e) => setForm({ ...form, fechaVencimiento: e.target.value })} required /></label>
      {!form.idRecibo && <label>Destino<select value={form.destino} onChange={(e) => setForm({ ...form, destino: e.target.value, idTorre: '', idApartamento: '' })}><option value="apartamento">Un apartamento</option><option value="torre">Torre completa</option></select></label>}
      {form.destino === 'torre' ? <label>Torre<select value={form.idTorre} onChange={(e) => setForm({ ...form, idTorre: e.target.value })} required><option value="">Seleccione una torre</option>{cat.torres.map((item) => <option key={item.idTorre} value={item.idTorre}>{item.nombreTorre}</option>)}</select></label> : <label>Apartamento<select value={form.idApartamento} onChange={(e) => setForm({ ...form, idApartamento: e.target.value })} required><option value="">Seleccione</option>{cat.apartamentos.map((item) => <option key={item.idApartamento} value={item.idApartamento}>{item.numeroApartamento} - {item.nombreTorre}</option>)}</select></label>}
    </div></Modal>}
  </>
}