import axiosClient from './axiosClient'

// Soumission publique (sans authentification).
export const submitDemande = (data) =>
  axiosClient.post('/demandes-adhesion', data).then((res) => res.data)

// Reserve directeur / admin.
export const getDemandes = (params = {}) =>
  axiosClient.get('/demandes-adhesion', { params }).then((res) => res.data)

export const accepterDemande = (id, data) =>
  axiosClient.post(`/demandes-adhesion/${id}/accepter`, data).then((res) => res.data)

export const refuserDemande = (id, data) =>
  axiosClient.post(`/demandes-adhesion/${id}/refuser`, data).then((res) => res.data)
