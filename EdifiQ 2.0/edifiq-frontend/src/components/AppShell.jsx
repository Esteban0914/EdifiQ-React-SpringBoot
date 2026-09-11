import React from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

const menus = {
  Administrador: [
    ['/', 'Dashboard', '⌂'], ['/personas', 'Personas', '♙'], ['/usuarios', 'Usuarios', '◎'], ['/vigilantes', 'Vigilantes', '◉'],
    ['/torres', 'Torres', '▥'], ['/apartamentos', 'Apartamentos', '▦'], ['/zonas', 'Zonas comunes', '◇'],
    ['/visitas', 'Visitas', '◷'], ['/paquetes', 'Paquetes', '▣'], ['/recibos', 'Recibos', '$'], ['/reservas', 'Reservas', '▤'],
  ],
  Vigilante: [['/', 'Dashboard', '⌂'], ['/visitas', 'Visitas', '◷'], ['/paquetes', 'Paquetes', '▣'], ['/personas', 'Personas', '♙'], ['/reservas', 'Reservas', '▤']],
  Residente: [['/', 'Dashboard', '⌂'], ['/paquetes', 'Paquetes', '▣'], ['/visitas', 'Visitas', '◷'], ['/reservas', 'Reservas', '▤'], ['/recibos', 'Recibos', '$'], ['/familiares', 'Mis familiares', '♧']],
}

export default function AppShell() {
  const { user, logout } = useAuth()
  const [open, setOpen] = React.useState(false)
  const location = useLocation()
  const items = menus[user?.rol] || []
  const navClass = (to, isActive) => {
    const active = to === '/' ? location.pathname === '/' : isActive
    return `nav-item ${active ? 'active' : ''}`
  }

  return <div className="app-shell">
    <aside className={`sidebar ${open ? 'sidebar-open' : ''}`}>
      <div className="sidebar-brand-row"><div className="brand"><span className="brand-mark">E</span><div><strong>Edifiq</strong><small>Gestión residencial</small></div></div><span className="sidebar-live" title="Sistema activo" /></div>
      <nav className="main-nav" aria-label="Navegación principal">
        {items.map(([to, label, icon]) => <NavLink key={to} to={to} className={({ isActive }) => navClass(to, isActive)} onClick={() => setOpen(false)}><span>{icon}</span>{label}</NavLink>)}
      </nav>
      <div className="sidebar-bottom"><NavLink to="/perfil" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} onClick={() => setOpen(false)}><span>◯</span>Mi perfil</NavLink><button className="nav-item logout-link" onClick={logout}><span>↪</span>Cerrar sesión</button></div>
    </aside>
    {open && <button className="sidebar-overlay" aria-label="Cerrar menú" onClick={() => setOpen(false)} />}
    <main className="main-content"><header className="topbar"><button className="mobile-menu" onClick={() => setOpen(true)} aria-label="Abrir menú">☰</button><div className="topbar-context"><span className="eyebrow">Espacio residencial</span><strong>{user?.nombreCompleto}</strong></div><NavLink to="/perfil" className="user-pill"><span className="avatar">{user?.nombreCompleto?.slice(0, 1).toUpperCase()}</span><span><b>{user?.rol}</b><small>Ver perfil</small></span><i>⌄</i></NavLink></header><div className="content-wrap"><Outlet /></div></main>
  </div>
}
