import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { dashboardApi } from '../services/api'
import StatsCards from '../components/StatsCards'

export default function Dashboard() {
  const { user } = useAuth()
  const [stats, setStats] = useState({})
  const [error, setError] = useState('')

  useEffect(() => {
    const load = user.rol === 'Administrador' ? dashboardApi.admin : user.rol === 'Vigilante' ? dashboardApi.guard : dashboardApi.resident
    load().then(setStats).catch(e => setError(e.message))
  }, [user.rol])

  const admin = user.rol === 'Administrador'
  const guard = user.rol === 'Vigilante'

  const items = admin
    ? [['Personas activas', stats.personas, '♙'], ['Visitas hoy', stats.visitasHoy, '◷'], ['Paquetes hoy', stats.paquetesHoy, '▣'], ['Recibos pendientes', stats.recibosPendientes, '$']]
    : guard
      ? [['Visitas hoy', stats.visitasHoy, '◷'], ['Visitas pendientes', stats.visitasPendientes, '!'], ['Paquetes hoy', stats.paquetesHoy, '▣'], ['Paquetes pendientes', stats.paquetesPendientes, '!']]
      : [['Paquetes pendientes', stats.paquetes, '▣'], ['Visitas hoy', stats.visitasHoy, '◷'], ['Reservas', stats.reservas, '▤'], ['Recibos pendientes', stats.recibosPendientes, '$']]

  const quick = admin
    ? [['Personas','/personas','Directorio y residentes'],['Usuarios','/usuarios','Cuentas y roles'],['Torres','/torres','Estructura masiva'],['Visitas','/visitas','Ingresos y salidas'],['Reservas','/reservas','Aprobaciones y horarios'],['Zonas comunes','/zonas','Espacios reservables']]
    : guard
      ? [['Visitas','/visitas','Control de acceso'],['Paquetes','/paquetes','Recepción y entrega'],['Personas','/personas','Directorio'],['Reservas','/reservas','Consulta']]
      : [['Paquetes','/paquetes','Tus entregas'],['Visitas','/visitas','Tus visitantes'],['Reservas','/reservas','Zonas comunes'],['Recibos','/recibos','Pagos']]

  return <>
    <section className="dashboard-hero">
      <div>
        <span className="hero-kicker">Panel de administración</span>
        <h1>Hola, {user.nombreCompleto?.split(' ')[0] || 'usuario'} 👋</h1>
        <p>Control centralizado del conjunto. Revisa lo importante y entra directamente a cada módulo.</p>
      </div>
      {admin && <Link className="button primary hero-action" to="/torres">+ Registrar estructura</Link>}
    </section>

    {error && <div className="alert error">{error}</div>}
    <StatsCards items={items.map(([label, value, icon]) => ({ label, value, icon }))}/>

    <div className="dashboard-card quick-card">
      <div className="section-heading">
        <div><h2>Accesos rápidos</h2><p>Las acciones disponibles cambian según tu rol.</p></div>
      </div>
      <div className="quick-grid">
        {quick.map(([name, to, desc]) => <Link to={to} className="quick-item" key={name}>
          <div><span className="quick-icon">{name.slice(0,1)}</span><div><strong>{name}</strong><small>{desc}</small></div></div>
          <b>→</b>
        </Link>)}
      </div>
    </div>

    {admin && <div className="admin-note">
      <div><strong>Flujo recomendado</strong><span>1. Registra personas → 2. Genera torres/apartamentos → 3. Crea usuarios → 4. Administra visitas y reservas.</span></div>
      <Link to="/personas">Empezar →</Link>
    </div>}
  </>
}
