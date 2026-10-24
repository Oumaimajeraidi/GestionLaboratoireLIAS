import api from './axiosClient'

export const getRapportAnnuel = (annee) => api.get(`/rapport-annuel?annee=${annee}`).then(r => r.data)
