import api from './axiosClient'

export const getRecus = () => api.get('/messages/recus').then(r => r.data)
export const getEnvoyes = () => api.get('/messages/envoyes').then(r => r.data)
export const getNonLus = () => api.get('/messages/non-lus').then(r => r.data)
export const sendMessage = (data) => api.post('/messages', data).then(r => r.data)
export const markAsRead = (id) => api.patch(`/messages/${id}/lire`).then(r => r.data)
