import api from './axiosClient'

export const search = (q) => api.get(`/recherche?q=${encodeURIComponent(q)}`).then(r => r.data)
