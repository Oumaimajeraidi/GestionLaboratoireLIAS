import axiosClient from './axiosClient'

export const getEvenements = (params = {}) =>
  axiosClient.get('/evenements', { params }).then((res) => res.data)

export const getEvenement = (id) =>
  axiosClient.get(`/evenements/${id}`).then((res) => res.data)

export const createEvenement = (data) =>
  axiosClient.post('/evenements', data).then((res) => res.data)

export const updateEvenement = (id, data) =>
  axiosClient.put(`/evenements/${id}`, data).then((res) => res.data)

export const deleteEvenement = (id) => axiosClient.delete(`/evenements/${id}`)
