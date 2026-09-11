import { useEffect, useState } from 'react'
import { asociadosApi, catalogApi } from '../services/api'
import ModuleHeader from '../components/ModuleHeader'
import Modal from '../components/Modal'
import EmptyState from '../components/EmptyState'

const empty = { idTipoDocumento: '', numeroDocumento: '', nombres: '', apellidos: '', telefono: '', correo: '' }
export default function Familiares() {
  const [rows,setRows]=useState([]), [docs,setDocs]=useState([]), [form,setForm]=useState(empty), [open,setOpen]=useState(false), [saving,setSaving]=useState(false), [error,setError]=useState('')
  const load=async()=>{try{const [r,c]=await Promise.all([asociadosApi.mine(),catalogApi.all()]);setRows(r.filter(x=>x.idTipoResidente===3));setDocs(c.tiposDocumento||[])}catch(e){setError(e.message)}}
  useEffect(() => {
    let cancelled = false
    const loadInitialData = async () => {
      try {
        const [familiares, catalogos] = await Promise.all([asociadosApi.mine(), catalogApi.all()])
        if (!cancelled) {
          setRows(familiares.filter((item) => item.idTipoResidente === 3))
          setDocs(catalogos.tiposDocumento || [])
        }
      } catch (e) {
        if (!cancelled) setError(e.message)
      }
    }
    void loadInitialData()
    return () => { cancelled = true }
  }, [])
  const save=async e=>{e.preventDefault();setSaving(true);setError('');try{await asociadosApi.add(form);setOpen(false);setForm(empty);await load()}catch(e){setError(e.message)}finally{setSaving(false)}}
  const remove = async (id) => {
    if (!window.confirm('¿Retirar este familiar del apartamento?')) return
    try {
      await asociadosApi.remove(id)
      await load()
    } catch (e) {
      setError(e.message)
    }
  }
  return <><ModuleHeader title="Mis familiares" description="Agrega y administra las personas asociadas a tu apartamento." action={<button className="button primary" onClick={()=>{setForm(empty);setOpen(true)}}>+ Agregar familiar</button>}/>{error&&<div className="alert error">{error}</div>}<div className="dashboard-card"><div className="table-wrap">{rows.length===0?<EmptyState title="No tienes familiares asociados" description="Agrega un familiar para que pueda ser identificado y reclamar paquetes en tu apartamento."/>:<table><thead><tr><th>Familiar</th><th>Documento</th><th>Teléfono</th><th></th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td><strong>{r.nombres} {r.apellidos}</strong></td><td>{r.numeroDocumento}</td><td>{r.telefono||'—'}</td><td className="actions"><button className="danger-text" onClick={()=>remove(r.id)}>Retirar</button></td></tr>)}</tbody></table>}</div></div><Modal open={open} title="Agregar familiar" onClose={()=>setOpen(false)} onSubmit={save} loading={saving}><div className="form-grid"><label>Tipo documento<select value={form.idTipoDocumento} onChange={e=>setForm({...form,idTipoDocumento:e.target.value})} required><option value="">Seleccione</option>{docs.map(x=><option key={x.idTipoDocumento} value={x.idTipoDocumento}>{x.nombre}</option>)}</select></label><label>Número documento<input value={form.numeroDocumento} onChange={e=>setForm({...form,numeroDocumento:e.target.value.replace(/\D/g,'').slice(0,16)})} required/></label><label>Nombres<input value={form.nombres} onChange={e=>setForm({...form,nombres:e.target.value})} required/></label><label>Apellidos<input value={form.apellidos} onChange={e=>setForm({...form,apellidos:e.target.value})} required/></label><label>Teléfono<input inputMode="numeric" maxLength="10" value={form.telefono} onChange={e=>setForm({...form,telefono:e.target.value.replace(/\D/g,'').slice(0,10)})}/></label><label>Correo<input type="email" value={form.correo} onChange={e=>setForm({...form,correo:e.target.value})}/></label></div></Modal></>
}
