import api from './axiosClient'

export const getMandats = () => api.get('/mandats').then(r => r.data)
export const createMandat = (data) => api.post('/mandats', data).then(r => r.data)
export const updateMandat = (id, data) => api.put(`/mandats/${id}`, data).then(r => r.data)
export const activerMandat = (id) => api.patch(`/mandats/${id}/activer`).then(r => r.data)
export const deleteMandat = (id) => api.delete(`/mandats/${id}`)
