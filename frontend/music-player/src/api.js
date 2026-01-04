// src/api.js
// 根据环境变量确定API基础地址
const BASE_URL = import.meta.env.PROD 
  ? 'https://auris.hazenix.top/api' 
  : 'http://localhost:9090'

// 确保路径拼接正确，避免双斜杠或缺少斜杠
function buildUrl(baseUrl, path) {
  // 移除 baseUrl 末尾的斜杠
  const base = baseUrl.replace(/\/+$/, '')
  // 确保 path 以斜杠开头
  const urlPath = path.startsWith('/') ? path : '/' + path
  return base + urlPath
}

export const api = {
  async request(url, options = {}) {
    const token = localStorage.getItem('token')
    const headers = {
      ...(options.headers || {}),
      ...(token ? { 'authentication': token } : {})
    }
    
    // 如果是 FormData，不要设置 Content-Type，让浏览器自动设置
    if (options.body instanceof FormData) {
      delete headers['Content-Type']
    }
    
    // 使用 buildUrl 确保路径拼接正确
    const fullUrl = buildUrl(BASE_URL, url)
    const res = await fetch(fullUrl, { ...options, headers })
    
    // 检查响应状态
    if (!res.ok) {
      // 尝试解析错误响应
      const contentType = res.headers.get('content-type')
      if (contentType && contentType.includes('application/json')) {
        const errorData = await res.json().catch(() => ({}))
        throw new Error(errorData.msg || errorData.message || `请求失败: ${res.status}`)
      } else {
        throw new Error(`请求失败: ${res.status} ${res.statusText}`)
      }
    }
    
    // 检查响应体是否为空
    const text = await res.text()
    if (!text) {
      return null
    }
    
    // 安全地解析JSON
    try {
      return JSON.parse(text)
    } catch (e) {
      throw new Error(`响应解析失败: ${e.message}`)
    }
  },

  // ========== 用户相关接口 ==========
  
  // 接口1：用户登录
  login(email, password) {
    return this.request('/user/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
  },

  // 接口2：用户注册
  register(username, email, password) {
    return this.request('/user/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    })
  },

  // 接口3：获取当前用户信息
  getUserInfo() {
    return this.request('/user/user/userinfo', {
      method: 'GET'
    })
  },

  // 接口4：退出登录
  logout() {
    return this.request('/user/user/logout', {
      method: 'POST'
    })
  },

  // 接口5：修改用户信息
  updateProfile(data) {
    return this.request('/user/user/profile', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },

  // ========== 歌单相关接口 ==========

  // 接口6：获取所有歌单列表
  getPlaylists() {
    return this.request('/user/playlist', {
      method: 'GET'
    })
  },

  // 接口7：创建新歌单
  createPlaylist(data) {
    return this.request('/user/playlist', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },

  // 接口8：修改歌单信息
  updatePlaylist(data) {
    return this.request('/user/playlist', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },

  // 接口9：删除歌单
  deletePlaylist(id) {
    return this.request(`/user/playlist/${id}`, {
      method: 'DELETE'
    })
  },

  // ========== 音乐相关接口 ==========

  // 接口10：根据歌单id获取歌曲列表
  getTracksByPlaylist(playlistId) {
    return this.request(`/user/track/playlist/${playlistId}`, {
      method: 'GET'
    })
  },

  // 接口11：向歌单添加歌曲
  addTrackToPlaylist(data) {
    const formData = new FormData()
    
    // 必填字段
    if (data.playlistId) formData.append('playlistId', data.playlistId)
    if (data.title) formData.append('title', data.title)
    if (data.artist) formData.append('artist', data.artist)
    
    // 可选字段
    if (data.orderIndex !== undefined) formData.append('orderIndex', data.orderIndex)
    if (data.album) formData.append('album', data.album)
    if (data.coverUrl) formData.append('coverUrl', data.coverUrl)
    if (data.file) formData.append('file', data.file)
    
    return this.request('/user/track/playlist', {
      method: 'POST',
      body: formData
    })
  },

  // 接口12：从歌单中移除歌曲
  removeTrackFromPlaylist(playlistId, trackId) {
    return this.request(`/user/track/playlist/${playlistId}/${trackId}`, {
      method: 'DELETE'
    })
  },

  // 接口13：重新排序歌单内歌曲
  reorderTracks(playlistId, ids) {
    return this.request(`/user/track/playlist/${playlistId}/reorder`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ids })
    })
  },

  // 接口14：上传歌曲封面
  uploadTrackCover(trackId, file) {
    const formData = new FormData()
    formData.append('file', file)
    return this.request(`/user/track/${trackId}/cover`, {
      method: 'POST',
      body: formData
    })
  },

  // 接口15：上传歌曲音频
  uploadTrackAudio(trackId, file) {
    const formData = new FormData()
    formData.append('file', file)
    return this.request(`/user/track/${trackId}/audio`, {
      method: 'POST',
      body: formData
    })
  },

  // 接口18：获取STS临时凭证
  getTempCredentials() {
    return this.request('/user/track/upload/credentials', {
      method: 'GET'
    })
  },

  // 接口19：获取上传封面的临时凭证（v2版本）
  getCoverUploadCredentials(trackId) {
    return this.request(`/user/track/${trackId}/cover/v2`, {
      method: 'GET'
    })
  },

  // 接口20：获取上传音频的临时凭证（v2版本）
  getAudioUploadCredentials(trackId) {
    return this.request(`/user/track/${trackId}/audio/v2`, {
      method: 'GET'
    })
  },

  // 修改歌曲信息
  updateTrack(trackId, data) {
    return this.request(`/user/track/${trackId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },

  // ========== 歌词相关接口 ==========

  // 接口16：根据歌曲id获取歌词
  getLyrics(trackId) {
    return this.request(`/user/lyrics/${trackId}`, {
      method: 'GET'
    })
  },

  // 接口17：根据歌曲id添加歌词
  addLyrics(trackId, file) {
    const formData = new FormData()
    formData.append('file', file)
    return this.request(`/user/lyrics/${trackId}`, {
      method: 'POST',
      body: formData
    })
  }
}
