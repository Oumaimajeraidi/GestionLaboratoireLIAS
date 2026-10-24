import api from './axiosClient'

export const getAffiliations = () => api.get('/gouvernance/affiliations').then(r => r.data)
export const getAffiliationsMembre = (membreId) => api.get(`/gouvernance/affiliations/membre/${membreId}`).then(r => r.data)
export const createAffiliation = (data) => api.post('/gouvernance/affiliations', data).then(r => r.data)

export const getRoles = () => api.get('/gouvernance/roles').then(r => r.data)
export const getRolesMembre = (membreId) => api.get(`/gouvernance/roles/membre/${membreId}`).then(r => r.data)
export const createRole = (data) => api.post('/gouvernance/roles', data).then(r => r.data)
