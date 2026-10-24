import axiosClient from './axiosClient'

export const getMembres = (query) =>
  axiosClient.get('/membres', { params: query ? { q: query } : {} }).then((res) => res.data)

export const getMembre = (id) =>
  axiosClient.get(`/membres/${id}`).then((res) => res.data)

export const createMembre = (data) =>
  axiosClient.post('/membres', data).then((res) => res.data)

export const updateMembre = (id, data) =>
  axiosClient.put(`/membres/${id}`, data).then((res) => res.data)

export const deleteMembre = (id) =>
  axiosClient.delete(`/membres/${id}`)

export const desactiverMembre = (id) =>
  axiosClient.patch(`/membres/${id}/desactiver`).then(r => r.data)

export const mettreEnRetraiteMembre = (id) =>
  axiosClient.patch(`/membres/${id}/retraite`).then(r => r.data)

export const reactiverMembre = (id) =>
  axiosClient.patch(`/membres/${id}/reactiver`).then(r => r.data)
