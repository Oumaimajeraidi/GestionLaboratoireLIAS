import axiosClient from './axiosClient'

export const getPublications = (params = {}) =>
  axiosClient.get('/publications', { params }).then((res) => res.data)

export const getPublication = (id) =>
  axiosClient.get(`/publications/${id}`).then((res) => res.data)

export const createPublication = (data) =>
  axiosClient.post('/publications', data).then((res) => res.data)

export const updatePublication = (id, data) =>
  axiosClient.put(`/publications/${id}`, data).then((res) => res.data)

export const deletePublication = (id) => axiosClient.delete(`/publications/${id}`)
