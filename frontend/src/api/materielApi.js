import api from './axiosClient'

export const getMateriels = () => api.get('/materiels').then(r => r.data)
export const createMateriel = (data) => api.post('/materiels', data).then(r => r.data)
export const updateMateriel = (id, data) => api.put(`/materiels/${id}`, data).then(r => r.data)
export const attribuerMateriel = (id, membreId) => api.patch(`/materiels/${id}/attribuer?membreId=${membreId}`).then(r => r.data)
export const retournerMateriel = (id) => api.patch(`/materiels/${id}/retourner`).then(r => r.data)
export const deleteMateriel = (id) => api.delete(`/materiels/${id}`)

export const getBesoins = () => api.get('/materiels/besoins').then(r => r.data)
export const createBesoin = (data) => api.post('/materiels/besoins', data).then(r => r.data)
export const validerBesoin = (id, valideurId, commentaire) =>
  api.patch(`/materiels/besoins/${id}/valider?valideurId=${valideurId}${commentaire ? '&commentaire=' + encodeURIComponent(commentaire) : ''}`).then(r => r.data)
export const refuserBesoin = (id, valideurId, commentaire) =>
  api.patch(`/materiels/besoins/${id}/refuser?valideurId=${valideurId}${commentaire ? '&commentaire=' + encodeURIComponent(commentaire) : ''}`).then(r => r.data)
