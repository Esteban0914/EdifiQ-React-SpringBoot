import { useEffect, useMemo, useState } from 'react'
import { authApi } from '../services/api'
import { AuthContext } from './AuthContext'

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => { try { return JSON.parse(sessionStorage.getItem('edifiq_user')) || null } catch { return null } })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    let mounted = true
    authApi.me().then((current) => {
      if (mounted) { setUser(current); sessionStorage.setItem('edifiq_user', JSON.stringify(current)) }
    }).catch(() => {
      if (mounted) { setUser(null); sessionStorage.removeItem('edifiq_user') }
    }).finally(() => mounted && setLoading(false))
    return () => { mounted = false }
  }, [])

  const login = async (username, password) => { const current = await authApi.login(username, password); setUser(current); sessionStorage.setItem('edifiq_user', JSON.stringify(current)); return current }
  const logout = async () => { try { await authApi.logout() } finally { setUser(null); sessionStorage.removeItem('edifiq_user') } }
  const value = useMemo(() => ({ user, loading, login, logout }), [user, loading])
  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
