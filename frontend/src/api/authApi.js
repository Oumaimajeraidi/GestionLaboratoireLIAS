import axiosClient from './axiosClient'

export const login = (email, motDePasse) =>
  axiosClient.post('/auth/login', { email, motDePasse }).then((res) => res.data)

export const fetchCurrentUser = () =>
  axiosClient.get('/auth/me').then((res) => res.data)
