import axiosClient from './axiosClient'

export const getDocuments = (params = {}) =>
  axiosClient.get('/documents', { params }).then((res) => res.data)

export const uploadDocument = (formData) =>
  axiosClient
    .post('/documents', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    .then((res) => res.data)

export const deleteDocument = (id) => axiosClient.delete(`/documents/${id}`)

export const downloadDocument = async (id, filename) => {
  const res = await axiosClient.get(`/documents/${id}/download`, { responseType: 'blob' })
  const url = window.URL.createObjectURL(new Blob([res.data]))
  const a = document.createElement('a')
  a.href = url
  a.download = filename || 'document'
  document.body.appendChild(a)
  a.click()
  a.remove()
  window.URL.revokeObjectURL(url)
}
