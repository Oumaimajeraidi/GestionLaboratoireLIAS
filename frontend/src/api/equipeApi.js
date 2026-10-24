import axiosClient from './axiosClient'

export const getEquipes = () => axiosClient.get('/equipes').then((res) => res.data)
