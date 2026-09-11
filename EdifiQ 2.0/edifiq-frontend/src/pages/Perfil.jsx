import { useEffect, useState } from 'react'
import { perfilApi, authApi } from '../services/api'
import { useAuth } from '../context/useAuth'
import ModuleHeader from '../components/ModuleHeader'

export default function Perfil() {
  const { user } = useAuth()
  const [form, setForm] = useState({ username: '', nombres: '', apellidos: '', telefono: '', correo: '' })
  const [password, setPassword] = useState({ passwordActual: '', nuevaPassword: '' })
  const [loading, setLoading] = useState(true), [saving, setSaving] = useState(false), [savingPassword, setSavingPassword] = useState(false), [message, setMessage] = useState(''), [error, setError] = useState('')

  useEffect(() => { perfilApi.get().then(data => setForm({ username: data.username, nombres: data.nombres, apellidos: data.apellidos, telefono: data.telefono || '', correo: data.correo || '' })).catch(e => setError(e.message)).finally(() => setLoading(false)) }, [])

  const save = async e => { e.preventDefault(); setSaving(true); setError(''); setMessage(''); try { await perfilApi.update({ telefono: form.telefono, correo: form.correo }); const current = await authApi.me(); sessionStorage.setItem('edifiq_user', JSON.stringify(current)); window.location.reload() } catch (e) { setError(e.message) } finally { setSaving(false) } }
  const savePassword = async e => { e.preventDefault(); setSavingPassword(true); setError(''); setMessage(''); try { await perfilApi.changePassword(password); setPassword({ passwordActual: '', nuevaPassword: '' }); setMessage('Contraseña actualizada correctamente.'); } catch (e) { setError(e.message) } finally { setSavingPassword(false) } }

  return <><ModuleHeader title="Mi perfil" description={`Administra tus datos personales y las credenciales de ${user?.username || ''}.`} />{loading ? <div className="loading">Cargando perfil...</div> : <><div className="profile-grid"><div className="dashboard-card"><h2>Datos personales</h2><p>El usuario, nombres y apellidos no se pueden modificar.</p><form className="form-stack" onSubmit={save}><label>Usuario<input value={form.username} readOnly /></label><label>Nombres<input value={form.nombres} readOnly /></label><label>Apellidos<input value={form.apellidos} readOnly /></label><label>Teléfono<input value={form.telefono} onChange={e => setForm({ ...form, telefono: e.target.value })} /></label><label>Correo<input type="email" value={form.correo} onChange={e => setForm({ ...form, correo: e.target.value })} /></label><button type="submit" className="button primary" disabled={saving}>{saving ? 'Guardando...' : 'Guardar cambios'}</button></form></div><div className="dashboard-card"><h2>Seguridad</h2><p>Cambia tu contraseña usando la contraseña actual.</p><form className="form-stack" onSubmit={savePassword}><label>Contraseña actual<input type="password" value={password.passwordActual} onChange={e => setPassword({ ...password, passwordActual: e.target.value })} required /></label><label>Nueva contraseña<input type="password" minLength="6" value={password.nuevaPassword} onChange={e => setPassword({ ...password, nuevaPassword: e.target.value })} required /></label><button type="submit" className="button primary" disabled={savingPassword}>{savingPassword ? 'Actualizando...' : 'Cambiar contraseña'}</button></form></div></div>{(error || message) && <div className={`alert ${error ? 'error' : ''}`}>{error || message}</div>}</>}</>
}
