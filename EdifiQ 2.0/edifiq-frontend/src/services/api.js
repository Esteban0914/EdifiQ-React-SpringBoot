import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status
    const data = error.response?.data
    const message = data?.message || (status === 403 ? 'No tienes permisos para realizar esta operación.' : status === 401 ? 'Tu sesión ha expirado. Inicia sesión nuevamente.' : status === 500 ? 'Error interno del servidor. Revisa la consola de Spring Boot para ver el detalle.' : error.code === 'ECONNABORTED' ? 'El servidor tardó demasiado en responder.' : 'No se pudo completar la solicitud.')
    return Promise.reject(new Error(message))
  },
)

export const authApi = {
  login: async (username, password) => (await api.post('/auth/login', { username, password })).data.user,
  me: async () => (await api.get('/auth/me')).data,
  logout: async () => { await api.post('/auth/logout') },
}

export const catalogApi = { all: async () => (await api.get('/catalogos')).data }
export const dashboardApi = {
  admin: async () => (await api.get('/dashboard/admin')).data,
  guard: async () => (await api.get('/dashboard/guard')).data,
  resident: async () => (await api.get('/dashboard/resident')).data,
}

export const personasApi = {
  list: async (params = {}) => {
    const response = await api.get('/personas', { params })
    return response.data
  },
  stats: async () => {
    const response = await api.get('/personas/stats')
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/personas', body)
    return response.data
  },
  update: async (id, body) => {
    const response = await api.put(`/personas/${id}`, body)
    return response.data
  },
  toggle: async (id) => {
    await api.patch(`/personas/${id}/estado`)
  },
  remove: async (id) => {
    await api.delete(`/personas/${id}`)
  },
}

export const vigilantesApi = {
  list: async (params = {}) => {
    const response = await api.get('/vigilantes', { params })
    return response.data
  },
  save: async (body) => {
    const response = await api.post('/vigilantes', body)
    return response.data
  },
  toggle: async (id) => {
    await api.patch(`/vigilantes/${id}/estado`)
  },
  remove: async (id) => {
    await api.delete(`/vigilantes/${id}`)
  },
}

export const usuariosApi = {
  list: async (params = {}) => {
    const response = await api.get('/usuarios', { params })
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/usuarios', body)
    return response.data
  },
  update: async (id, body) => {
    const response = await api.put(`/usuarios/${id}`, body)
    return response.data
  },
  toggle: async (id) => {
    await api.patch(`/usuarios/${id}/estado`)
  },
  remove: async (id) => {
    await api.delete(`/usuarios/${id}`)
  },
}

export const estructuraApi = {
  torres: async () => (await api.get('/torres')).data,
  createTorre: async (body) => (await api.post('/torres', body)).data,
  updateTorre: async (id, body) => (await api.put(`/torres/${id}`, body)).data,
  removeTorre: async (id) => { await api.delete(`/torres/${id}`) },
  apartamentos: async () => (await api.get('/apartamentos')).data,
  createApartamento: async (body) => (await api.post('/apartamentos', body)).data,
  updateApartamento: async (id, body) => (await api.put(`/apartamentos/${id}`, body)).data,
  toggleApartamento: async (id) => { await api.patch(`/apartamentos/${id}/estado`) },
  removeApartamento: async (id) => { await api.delete(`/apartamentos/${id}`) },
  zonas: async () => (await api.get('/zonas')).data,
  createZona: async (body) => (await api.post('/zonas', body)).data,
  updateZona: async (id, body) => (await api.put(`/zonas/${id}`, body)).data,
  removeZona: async (id) => { await api.delete(`/zonas/${id}`) },
}

export const perfilApi = {
  get: async () => (await api.get('/perfil')).data,
  update: async (body) => (await api.put('/perfil', body)).data,
  changePassword: async (body) => (await api.patch('/perfil/password', body)).data,
}

export const asociadosApi = {
  mine: async () => (await api.get("/asociados/mios")).data,
  add: async (body) => (await api.post("/asociados/mios", body)).data,
  remove: async (id) => { await api.delete(`/asociados/mios/${id}`) },
  byApartment: async (id) => (await api.get(`/asociados/apartamento/${id}`)).data,
  byPerson: async (id) => (await api.get(`/asociados/persona/${id}`)).data,
}

export const visitasApi = {
  list: async (params = {}) => {
    const response = await api.get('/visitas', { params })
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/visitas', body)
    return response.data
  },
  update: async (id, body) => {
    const response = await api.put(`/visitas/${id}`, body)
    return response.data
  },
  status: async (id, status) => { await api.patch(`/visitas/${id}/estado/${status}`) },
  authorize: async (id, autorizada) => { await api.patch(`/visitas/${id}/autorizacion`, null, { params: { autorizada } }) },
  remove: async (id) => {
    await api.delete(`/visitas/${id}`)
  },
}

export const paquetesApi = {
  list: async (params = {}) => {
    const response = await api.get('/paquetes', { params })
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/paquetes', body)
    return response.data
  },
  update: async (id, body) => {
    const response = await api.put(`/paquetes/${id}`, body)
    return response.data
  },
  people: async (id) => (await api.get(`/paquetes/${id}/personas`)).data,
  deliver: async (id, idPersona) => {
    await api.patch(`/paquetes/${id}/entregar`, null, idPersona ? { params: { idPersona } } : undefined)
  },
  remove: async (id) => {
    await api.delete(`/paquetes/${id}`)
  },
}

export const recibosApi = {
  list: async (params = {}) => {
    const response = await api.get('/recibos', { params })
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/recibos', body)
    return response.data
  },
  createBulk: async (body) => (await api.post('/recibos/torre', body)).data,
  update: async (id, body) => {
    const response = await api.put(`/recibos/${id}`, body)
    return response.data
  },
  sendBulk: async (ids) => (await api.post('/recibos/enviar-masivo', ids)).data,
  sendBulkByTower: async (tower) => (await api.post(`/recibos/enviar-masivo/torre/${tower}`)).data,
  pay: async (id) => {
    await api.patch(`/recibos/${id}/pagar`)
  },
  remove: async (id) => {
    await api.delete(`/recibos/${id}`)
  },
}

export const reservasApi = {
  list: async (params = {}) => {
    const response = await api.get('/reservas', { params })
    return response.data
  },
  create: async (body) => {
    const response = await api.post('/reservas', body)
    return response.data
  },
  update: async (id, body) => {
    const response = await api.put(`/reservas/${id}`, body)
    return response.data
  },
  cancel: async (id) => {
    await api.patch(`/reservas/${id}/cancelar`)
  },
  status: async (id, status) => {
    await api.patch(`/reservas/${id}/estado/${status}`)
  },
  remove: async (id) => {
    await api.delete(`/reservas/${id}`)
  },
}

export default api
