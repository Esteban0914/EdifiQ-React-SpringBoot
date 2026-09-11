import { useState } from 'react'
import { useAuth } from '../context/useAuth'

export default function Login() {
  const { login } = useAuth()
  const [username, setUsername] = useState(localStorage.getItem('edifiq_last_user') || '')
  const [password, setPassword] = useState('')
  const [remember, setRemember] = useState(true)
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const submit = async (event) => {
    event.preventDefault()
    if (!username.trim() || !password) {
      setError('Completa usuario y contraseña.')
      return
    }
    setLoading(true)
    setError('')
    try {
      await login(username.trim(), password)
      if (remember) localStorage.setItem('edifiq_last_user', username.trim())
      else localStorage.removeItem('edifiq_last_user')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return <main className="login-page">
    <div className="login-orbit login-orbit-one" />
    <div className="login-orbit login-orbit-two" />
    <section className="login-layout">
      <aside className="login-aside">
        <div className="login-aside-top">
          <div className="brand login-brand"><span className="brand-mark">E</span><div><strong>Edifiq</strong><small>Administracion residencial</small></div></div>
          <span className="login-status"><i /> Plataforma activa</span>
        </div>
        <div className="login-aside-copy">
          <span className="login-kicker">Tu comunidad, en orden</span>
          <h1>Todo lo que pasa en tu conjunto, <em>en un solo lugar.</em></h1>
          <p>Gestiona residentes, visitas, paquetes y recibos con una experiencia clara para cada rol.</p>
        </div>
        <div className="login-aside-footer"><span>01</span><div><b>Control simple</b><small>Informacion precisa para decidir mejor.</small></div></div>
      </aside>
      <section className="login-card">
        <div className="login-copy"><span>Acceso seguro</span><h2>Bienvenido de nuevo</h2><p>Ingresa tus datos para continuar al panel.</p></div>
        <form onSubmit={submit} className="form-stack">
          <label>Usuario<input className="login-input" value={username} onChange={(event) => setUsername(event.target.value)} autoComplete="username" autoFocus placeholder="Tu usuario" aria-label="Usuario" required /></label>
          <label>Contraseña<span className="password-field"><input className="login-input" type={showPassword ? 'text' : 'password'} value={password} onChange={(event) => setPassword(event.target.value)} autoComplete="current-password" placeholder="Tu contraseña" aria-label="Contraseña" required /><button type="button" className="password-toggle" onClick={() => setShowPassword(!showPassword)} aria-label={showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'}>{showPassword ? 'Ocultar' : 'Mostrar'}</button></span></label>
          <label className="check-row login-remember"><input type="checkbox" checked={remember} onChange={(event) => setRemember(event.target.checked)} /> Recordarme <span>en este equipo</span></label>
          {error && <div className="alert error login-error" role="alert">{error}</div>}
          <button type="submit" className="button primary full login-submit" disabled={loading}>{loading ? <><span className="login-spinner" /> Verificando...</> : <>Iniciar sesión <span aria-hidden="true">→</span></>}</button>
        </form>
        <p className="login-help">Si no puedes ingresar, contacta al administrador de tu conjunto.</p>
      </section>
    </section>
  </main>
}
