import api from './axiosClient'

export const getNotifications = () => api.get('/notifications').then(r => r.data)
export const countNotifications = () => api.get('/notifications/count').then(r => r.data)
export const markNotifRead = (id) => api.patch(`/notifications/${id}/lire`).then(r => r.data)
export const markAllNotifRead = () => api.patch('/notifications/lire-tout')
