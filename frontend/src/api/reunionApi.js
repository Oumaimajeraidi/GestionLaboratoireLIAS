import api from './axiosClient'

export const getReunions = () => api.get('/reunions').then(r => r.data)
export const getReunion = (id) => api.get(`/reunions/${id}`).then(r => r.data)
export const createReunion = (data) => api.post('/reunions', data).then(r => r.data)
export const updateReunion = (id, data) => api.put(`/reunions/${id}`, data).then(r => r.data)
export const deleteReunion = (id) => api.delete(`/reunions/${id}`)
export const uploadPv = (id, file) => {
  const fd = new FormData()
  fd.append('file', file)
  return api.post(`/reunions/${id}/pv`, fd, { headers: { 'Content-Type': 'multipart/form-data' } }).then(r => r.data)
}
