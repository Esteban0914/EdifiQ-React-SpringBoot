import { useCallback, useEffect, useState } from 'react'
import { catalogApi, dashboardApi, reservasApi } from '../services/api'
import { useAuth } from '../context/useAuth'
import ModuleHeader from '../components/ModuleHeader'
import FilterBar from '../components/FilterBar'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'

const empty = { idReserva: null, fechaReserva: '', horaInicio: '', horaFin: '', cantidadInvitados: 0, idEstadoReserva: 1, idZona: '', idApartamento: '' }

export default function Reservas() {
  const { user } = useAuth()
  const admin = user.rol === 'Administrador'
  const resident = user.rol === 'Residente'
  const guard = user.rol === 'Vigilante'
  const canWrite = admin || resident
  const [rows, setRows] = useState([]), [cat, setCat] = useState({ estadosReserva: [], zonas: [], torres: [], apartamentos: [] }), [residentApartment, setResidentApartment] = useState(''), [filters, setFilters] = useState({ q: '', estado: '', zona: '', torre: '', fecha: '' }), [form, setForm] = useState(empty), [open, setOpen] = useState(false), [saving, setSaving] = useState(false), [error, setError] = useState('')

  const load = useCallback(async () => {
    try {
      const requests = [
        reservasApi.list({ q: filters.q, estado: filters.estado || undefined, zona: filters.zona || undefined, torre: filters.torre || undefined, fecha: filters.fecha || undefined }),
        catalogApi.all(),
      ]
      if (resident) requests.push(dashboardApi.resident())
      const [rowsData, catalogs, dashboard] = await Promise.all(requests)
      setRows(rowsData)
      setCat(catalogs)
      if (resident) setResidentApartment(dashboard.apartamento || '')
    } catch (e) {
      setError(e.message)
    }
  }, [filters.fecha, filters.q, filters.estado, filters.torre, filters.zona, resident])
  useEffect(() => {
    const timer = setTimeout(() => { void load() }, 160)
    return () => clearTimeout(timer)
  }, [load])

  const edit = (r) => {
    if (guard) return
    setForm({ idReserva: r.id, fechaReserva: r.fecha, horaInicio: String(r.horaInicio).slice(0, 5), horaFin: String(r.horaFin).slice(0, 5), cantidadInvitados: r.invitados, idEstadoReserva: r.idEstado, idZona: r.idZona, idApartamento: resident ? residentApartment : r.idApartamento })
    setOpen(true)
  }
  const create = () => { setForm({ ...empty, fechaReserva: new Date().toISOString().slice(0, 10), idApartamento: resident ? residentApartment : '' }); setOpen(true) }
  const save = async e => { e.preventDefault(); setSaving(true); try { form.idReserva ? await reservasApi.update(form.idReserva, form) : await reservasApi.create(form); setOpen(false); load() } catch (e) { setError(e.message) } finally { setSaving(false) } }

  return <><ModuleHeader title="Reservas" description={guard ? 'Consulta las reservas de las zonas comunes del conjunto.' : 'Gestiona reservas con control de horarios, aprobación administrativa y prevención de cruces.'} action={canWrite && <button className="button primary" onClick={create}>+ Nueva reserva</button>} />{error && <div className="alert error">{error}</div>}<div className="dashboard-card"><FilterBar search={filters.q} onSearch={v => setFilters({ ...filters, q: v })} filters={[{ name: 'estado', value: filters.estado, placeholder: 'Estado', onChange: v => setFilters({ ...filters, estado: v }), options: cat.estadosReserva.map(x => ({ value: x.idEstadoReserva, label: x.nombre })) }, { name: 'zona', value: filters.zona, placeholder: 'Zona', onChange: v => setFilters({ ...filters, zona: v }), options: cat.zonas.map(x => ({ value: x.idZona, label: x.nombre })) }, { name: 'torre', value: filters.torre, placeholder: 'Torre', onChange: v => setFilters({ ...filters, torre: v }), options: cat.torres.map(x => ({ value: x.idTorre, label: x.nombreTorre })) }]} onClear={() => setFilters({ q: '', estado: '', zona: '', torre: '', fecha: '' })} /><div className="inline-filter"><label>Fecha<input type="date" value={filters.fecha} onChange={e => setFilters({ ...filters, fecha: e.target.value })} /></label></div><div className="table-wrap">{rows.length === 0 ? <EmptyState /> : <table><thead><tr><th>Zona</th><th>Fecha</th><th>Horario</th><th>Apartamento</th><th>Invitados</th><th>Estado</th>{canWrite && <th></th>}</tr></thead><tbody>{rows.map(r => <tr key={r.id}><td><strong>{r.zona}</strong></td><td>{r.fecha}</td><td>{String(r.horaInicio).slice(0, 5)} - {String(r.horaFin).slice(0, 5)}</td><td>{r.apartamento}<small>{r.torre}</small></td><td>{r.invitados}</td><td><span className={`status ${r.idEstado === 2 ? 'success' : ''}`}>{r.estado}</span></td>{canWrite && <td className="actions">
  {admin && r.idEstado === 1 && <>
    <button className="success-action" onClick={() => window.confirm('¿Autorizar esta reserva?') && reservasApi.status(r.id, 2).then(load).catch(e => setError(e.message))}>Autorizar</button>
    <button className="danger-action" onClick={() => window.confirm('¿Rechazar esta reserva?') && reservasApi.status(r.id, 3).then(load).catch(e => setError(e.message))}>Rechazar</button>
  </>}
  <button onClick={() => edit(r)}>Editar</button>
  {r.idEstado !== 3 && <button onClick={() => reservasApi.cancel(r.id).then(load).catch(e => setError(e.message))}>Cancelar</button>}
  {admin && <button className="danger-text" onClick={() => window.confirm('¿Eliminar reserva?') && reservasApi.remove(r.id).then(load).catch(e => setError(e.message))}>Eliminar</button>}
</td>}</tr>)}</tbody></table>}</div></div>{canWrite && <Modal open={open} title={form.idReserva ? 'Editar reserva' : 'Nueva reserva'} onClose={() => setOpen(false)} onSubmit={save} loading={saving}><div className="form-grid"><label>Zona común<select value={form.idZona} onChange={e => setForm({ ...form, idZona: e.target.value })} required><option value="">Seleccione</option>{cat.zonas.map(x => <option key={x.idZona} value={x.idZona}>{x.nombre}</option>)}</select></label><label>Apartamento<select value={form.idApartamento} onChange={e => setForm({ ...form, idApartamento: e.target.value })} disabled={resident} required><option value="">Seleccione</option>{cat.apartamentos.filter(x => x.activo).map(x => <option key={x.idApartamento} value={x.idApartamento}>{x.numeroApartamento} · Torre #{x.idTorre}</option>)}</select></label><label>Fecha<input type="date" value={form.fechaReserva} onChange={e => setForm({ ...form, fechaReserva: e.target.value })} required /></label><label>Hora inicio<input type="time" value={form.horaInicio} onChange={e => setForm({ ...form, horaInicio: e.target.value })} required /></label><label>Hora fin<input type="time" value={form.horaFin} onChange={e => setForm({ ...form, horaFin: e.target.value })} required /></label><label>Cantidad invitados<input type="number" min="0" value={form.cantidadInvitados} onChange={e => setForm({ ...form, cantidadInvitados: e.target.value })} /></label></div></Modal>}</>
}
