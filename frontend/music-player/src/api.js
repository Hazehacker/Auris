// src/api.js
const BASE_URL = '' // 后端地址，开发时可配 proxy

export const api = {
  async request(url, options = {}) {
    const token = localStorage.getItem('token')
    const headers = {
      ...(options.headers || {}),
      ...(token ? { 'Authorization': `Bearer ${token}` } : {})
    }
    const res = await fetch(BASE_URL + url, { ...options, headers })
    return res.json()
  },

  login(email, password) {
    return this.request('/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
  },

  register(username, email, password) {
    return this.request('/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    })
  },

  uploadTrack(file, name, artist) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('name', name)
    formData.append('artist', artist)
    return fetch(BASE_URL + '/track/upload', {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${localStorage.getItem('token')}` },
      body: formData
    }).then(r => r.json())
  },

  // ... 其他方法
}