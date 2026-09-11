import { Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './context/AuthProvider'
import { useAuth } from './context/useAuth'
import AppShell from './components/AppShell'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Personas from './pages/Personas'
import Vigilantes from './pages/Vigilantes'
import Usuarios from './pages/Usuarios'
import Torres from './pages/Torres'
import Apartamentos from './pages/Apartamentos'
import Zonas from './pages/Zonas'
import Perfil from './pages/Perfil'
import Visitas from './pages/Visitas'
import Paquetes from './pages/Paquetes'
import Recibos from './pages/Recibos'
import Familiares from './pages/Familiares'
import Reservas from './pages/Reservas'
import './App.css'

function RoleRoute({ roles, children }) {
  const { user } = useAuth()
  return roles.includes(user?.rol) ? children : <Navigate to="/" replace />
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <div className="loading-page">Cargando Edifiq...</div>
  return user ? <Navigate to="/" replace /> : children
}

function PrivateApp() {
  const { user, loading } = useAuth()
  if (loading) return <div className="loading-page">Cargando Edifiq...</div>
  if (!user) return <Navigate to="/login" replace />

  return <Routes><Route element={<AppShell />}>
      <Route path="/" element={<Dashboard />} />
      <Route path="/perfil" element={<Perfil />} />
      <Route path="/personas" element={<RoleRoute roles={['Administrador', 'Vigilante']}><Personas /></RoleRoute>} />
      <Route path="/vigilantes" element={<RoleRoute roles={['Administrador']}><Vigilantes /></RoleRoute>} />
      <Route path="/usuarios" element={<RoleRoute roles={['Administrador']}><Usuarios /></RoleRoute>} />
      <Route path="/torres" element={<RoleRoute roles={['Administrador']}><Torres /></RoleRoute>} />
      <Route path="/apartamentos" element={<RoleRoute roles={['Administrador']}><Apartamentos /></RoleRoute>} />
      <Route path="/zonas" element={<RoleRoute roles={['Administrador']}><Zonas /></RoleRoute>} />
      <Route path="/visitas" element={<RoleRoute roles={['Administrador', 'Vigilante', 'Residente']}><Visitas /></RoleRoute>} />
      <Route path="/paquetes" element={<RoleRoute roles={['Administrador', 'Vigilante', 'Residente']}><Paquetes /></RoleRoute>} />
      <Route path="/recibos" element={<RoleRoute roles={['Administrador', 'Residente']}><Recibos /></RoleRoute>} />
      <Route path="/familiares" element={<RoleRoute roles={['Residente']}><Familiares /></RoleRoute>} />
      <Route path="/reservas" element={<RoleRoute roles={['Administrador', 'Vigilante', 'Residente']}><Reservas /></RoleRoute>} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Route>
  </Routes>
}

export default function App() {
  return <AuthProvider><Routes><Route path="/login" element={<PublicRoute><Login /></PublicRoute>} /><Route path="*" element={<PrivateApp />} /></Routes></AuthProvider>
}
