import api from './axiosClient'

export const getPartenaires = () => api.get('/conventions/partenaires').then(r => r.data)
export const createPartenaire = (data) => api.post('/conventions/partenaires', data).then(r => r.data)
export const updatePartenaire = (id, data) => api.put(`/conventions/partenaires/${id}`, data).then(r => r.data)
export const deletePartenaire = (id) => api.delete(`/conventions/partenaires/${id}`)

export const getConventions = () => api.get('/conventions').then(r => r.data)
export const createConvention = (data) => api.post('/conventions', data).then(r => r.data)
export const updateConvention = (id, data) => api.put(`/conventions/${id}`, data).then(r => r.data)
export const deleteConvention = (id) => api.delete(`/conventions/${id}`)
