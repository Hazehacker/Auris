<template>
  <div class="player-container">
    <!-- 顶部 header（包含 logo、搜索、用户区域） -->
    <header class="app-header">
      <div class="header-left">
        <div class="logo" role="button" tabindex="0" @click="openFileDialog" @keydown.enter="openFileDialog">🎧 <span class="brand">Auris</span></div>
      </div>
      <div class="header-center">
        <input class="search" placeholder="搜索歌曲、歌手或歌单..." v-model="searchQuery" @input="performSearch" />
      </div>
      <div class="header-right">
        <div class="user">
          <template v-if="currentUser">
            <img v-if="currentUser.avatar" :src="currentUser.avatar" alt="avatar" class="avatar" />
            <span class="username">{{ currentUser.username }}</span>
            <button class="btn small" @click="logout">退出</button>
          </template>
          <template v-else>
            <button class="btn" @click="openAuth('login')">登录</button>
            <button class="btn green-outline" @click="openAuth('register')">注册</button>
          </template>
        </div>
        <button class="window-btn">— □ ✕</button>
      </div>
    </header>

    <div class="main-area">
      <!-- 左侧侧栏 -->
      <aside class="sidebar">
        <ul class="sidebar-list">
          <li class="side-item create" role="button" tabindex="0" @click="createPlaylist">＋  创建歌单</li>
          <li class="side-item import" role="button" tabindex="0" @click="openFileDialog" @keydown.enter="openFileDialog">⇪  导入本地音乐</li>
          <li class="side-item web">☁  网页音频提取</li>
          <li class="side-item profile" role="button" tabindex="0" @click="setView('profile')" @keydown.enter="setView('profile')" :class="{ active: viewMode === 'profile' }"><span class="icon">🏠</span> 个人主页</li>
          <li class="side-item collection" role="button" tabindex="0" @click="setView('all')" @keydown.enter="setView('all')" :class="{ active: viewMode === 'all' }"><span class="icon">🎵</span>▾ 单曲集合 <span class="count">({{ songList.length }})</span></li>
          <li class="side-item fav" role="button" tabindex="0" @click="setView('fav')" @keydown.enter="setView('fav')" :class="{ active: viewMode === 'fav' }">❤ 我喜欢的 <span class="count">({{ favCount }})</span></li>

          <!-- 歌单列表（可展开） -->
          <li class="side-item playlists" role="button" tabindex="0" @click="playlistsOpen = !playlistsOpen">▸ 歌单列表</li>
          <ul v-if="playlistsOpen" class="playlist-children">
            <li v-if="!playlists.length" class="side-item empty-note">（当前无歌单）</li>
            <li v-for="pl in playlists" :key="pl.id" class="side-item playlist-item" :class="{ active: selectedPlaylistId === pl.id }" role="button" tabindex="0" @click.stop="selectPlaylist(pl.id)">{{ pl.name }} <span class="count">({{ pl.songs ? pl.songs.length : 0 }})</span></li>
          </ul>
        </ul>
        <div class="sidebar-empty">(歌单操作)</div>
      </aside>

      <!-- 右侧主内容区 -->
      <main class="content">
        <!-- 个人主页区域（独立追加） -->
<section class="profile-page" v-if="viewMode === 'profile'">
  <div class="profile-header">
    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <div class="profile-avatar">
        <img 
          v-if="currentUser && currentUser.avatar" 
          :src="currentUser.avatar" 
          alt="用户头像" 
          class="avatar-lg"
        />
        <div v-else class="avatar-placeholder">
          {{ currentUser ? currentUser.username.charAt(0) : '👤' }}
        </div>
        <!-- 编辑头像按钮（登录后显示） -->
        <button 
          v-if="currentUser" 
          class="btn small edit-avatar-btn" 
          @click="openAvatarDialog"
        >
          更换头像
        </button>
      </div>
      
      <div class="profile-info">
        <h2 class="profile-username">
          <template v-if="editingProfile">
            <input v-model="editProfileForm.username" class="profile-name-input" />
          </template>
          <template v-else>
            {{ currentUser ? currentUser.username : '未登录' }}
          </template>
        </h2>
        <p class="profile-email">{{ currentUser ? currentUser.email : '请登录以查看个人信息' }}</p>
        <div class="profile-stats">
          <div class="stat-item">
            <span class="stat-value">{{ songList.length }}</span>
            <span class="stat-label">总歌曲</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ favCount }}</span>
            <span class="stat-label">收藏</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ playlists.length }}</span>
            <span class="stat-label">歌单</span>
          </div>
        </div>
        <!-- 编辑个人信息按钮（登录后显示） -->
        <button 
          v-if="currentUser" 
          class="btn green-outline profile-edit-btn" 
          @click="toggleEditProfile"
        >
          {{ editingProfile ? '保存' : '编辑信息' }}
        </button>
      </div>
    </div>
    
    <!-- 个人主页下的快捷入口 -->
    <div class="profile-actions">
      <button class="btn green" @click="setView('all')">查看所有歌曲</button>
      <button class="btn green" @click="setView('fav')">查看收藏</button>
      <button class="btn green" @click="createPlaylist">创建新歌单</button>
    </div>
  </div>
  
  <!-- 个人主页默认显示收藏的歌曲 -->
  <div class="profile-content">
    <h3 class="profile-content-title">我喜欢的歌曲</h3>
    <section class="playlist-table">
      <table>
        <thead>
          <tr>
            <th>歌曲名</th>
            <th>时长</th>
            <th>歌手/制作人</th>
            <th>喜爱程度</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="({ s, i }, idx) in favSongs" :key="i" :class="{ active: currentIndex === i }" @dblclick="playSong(i)">
            <td class="title-col">{{ s.name || '未知' }}</td>
            <td class="time-col">{{ s.duration ? formatTime(s.duration) : '—' }}</td>
            <td class="artist-col">{{ s.artist || '—' }}</td>
            <td class="fav-col"><button :class="['fav-btn', { filled: s.fav }]" @click.stop="toggleFav(i)">{{ s.fav ? '❤' : '♡' }}</button></td>
            <td class="action-col"><button class="icon-btn" @click.stop="openSongDeleteConfirm(i)" :title="'删除 ' + (s.name || '歌曲')">🗑</button></td>
          </tr>
          <tr v-if="favSongs.length === 0">
            <td colspan="5" class="empty">暂无收藏的歌曲。</td>
          </tr>
        </tbody>
      </table>
    </section>
  </div>
</section>

<!-- 头像上传输入（独立追加，放在原有 cover-ctrl 输入框下方） -->
<input id="avatar-ctrl" ref="avatarInput" class="sr-only" type="file" accept="image/*" @change="handleAvatarUpload" />
        <!-- 歌单详情区域 或 搜索结果区域 -->
        <section class="playlist-detail" v-if="viewMode !== 'search'">
          <div class="cover-and-title">
            <div class="cover" :style="coverStyle" role="button" tabindex="0" @click="viewMode==='playlist' && editing ? openCoverDialog() : null" @keydown.enter="viewMode==='playlist' && editing ? openCoverDialog() : null">
              <div class="cover-placeholder" v-if="!(viewMode === 'playlist' && selectedPlaylist && selectedPlaylist.cover)">
                <!-- 默认美观图标（SVG） -->
                <svg class="cover-default-icon" viewBox="0 0 64 64" role="img" aria-label="默认封面">
                  <defs>
                    <linearGradient id="coverGrad" x1="0" x2="1" y1="0" y2="1">
                      <stop offset="0" stop-color="#e9f7f0" />
                      <stop offset="1" stop-color="#dff7ef" />
                    </linearGradient>
                  </defs>
                  <rect x="6" y="6" width="52" height="52" rx="8" fill="url(#coverGrad)" />
                  <path d="M40 20v16a6 6 0 1 1-4-5.2V22l-10 3v12" fill="none" stroke="#2fb67d" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </div>
              <!-- 编辑时显示覆盖操作 -->
              <div class="cover-overlay" v-if="viewMode === 'playlist' && editing">
                <button class="btn small" @click.stop="openCoverDialog">上传封面</button>
                <button v-if="selectedPlaylist && selectedPlaylist.cover" class="btn small danger" @click.stop="removeCover">移除封面</button>
              </div>
            </div>
            <h2 class="main-title" :class="{ 'playlist-title': viewMode === 'playlist' }">
              <template v-if="editing && viewMode === 'playlist'">
                <input class="plist-name-input" v-model="editName" />
              </template>
              <template v-else>
                {{ currentTitle }}
              </template>
            </h2>
          </div>
          
          <!-- 区分不同视图的布局结构 -->
          <div class="meta" :class="{ 'collection-meta': viewMode === 'all' || viewMode === 'fav' }">
            <!-- 歌单模式：先显示简介，再显示创建人 -->
            <template v-if="viewMode === 'playlist'">
              <!-- 非编辑状态：白底静态 label -->
              <div v-if="!editing" class="desc-label">{{ selectedPlaylist ? selectedPlaylist.desc || '暂无简介' : '暂无简介' }}</div>
              <!-- 编辑状态：可输入 textarea -->
              <textarea v-else v-model="editDesc" class="desc" rows="4"></textarea>
              <div class="creator">创建人：<strong>Name</strong></div>
            </template>
            
            <!-- 单曲集合/我喜欢的：先显示创建人 -->
            <template v-else>
              <div class="creator" :class="{ 'collection-creator': viewMode === 'all' || viewMode === 'fav' }">创建人：<strong>Name</strong></div>
            </template>
            
            <div class="meta-actions" :class="{ 'collection-actions': viewMode === 'all' || viewMode === 'fav' }">
              <!-- 管理歌曲按钮（所有模式启用） -->
              <button class="btn green" @click="openManageSongs">管理歌曲</button>
              <!-- 编辑内容按钮（仅歌单模式） -->
              <button v-if="viewMode === 'playlist'" class="btn green-outline" :disabled="!selectedPlaylist" @click="toggleEditContent">{{ editing ? '保存' : '编辑内容' }}</button>
              <button v-if="editing && selectedPlaylist" class="btn danger" @click="deleteConfirmOpen = true">删除歌单</button>
            </div>
          </div>
        </section>

        <!-- 搜索结果区域 -->
        <section class="search-results" v-if="viewMode === 'search'">
          <h2 class="search-title">搜索结果</h2>
          <p class="search-query" v-if="searchQuery">关键词：“{{ searchQuery }}”</p>
        </section>

        <!-- 歌曲表格 -->
        <section class="playlist-table">
          <table>
            <thead>
              <tr>
                <th>歌曲名</th>
                <th>时长</th>
                <th>歌手/制作人</th>
                <th>喜爱程度</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="({ s, i }, idx) in displayed" :key="i" :class="{ active: currentIndex === i }" @dblclick="playSong(i)">
                <td class="title-col">{{ s.name || '未知' }}</td>
                <td class="time-col">{{ s.duration ? formatTime(s.duration) : '—' }}</td>
                <td class="artist-col">{{ s.artist || '—' }}</td>
                <td class="fav-col"><button :class="['fav-btn', { filled: s.fav }]" @click.stop="toggleFav(i)">{{ s.fav ? '❤' : '♡' }}</button></td>
                <td class="action-col"><button class="icon-btn" @click.stop="openSongDeleteConfirm(i)" :title="'删除 ' + (s.name || '歌曲')">🗑</button></td>
              </tr>
              <tr v-if="displayed.length === 0">
                <td colspan="5" class="empty">暂无歌曲可显示。</td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>
    </div>

    <!-- 底部播放器控制栏 -->
    <footer class="bottom-bar">
      <div class="player-controls">
        <button class="icon-btn prev-btn" @click="playPrev">◀◀</button>
        <button class="play-btn" :class="{ playing: isPlaying }" @click="togglePlay">{{ isPlaying ? '暂停' : '播放' }}</button>
        <!-- 优化爱心按钮的边界校验逻辑 -->
        <button 
          class="icon-btn fav-toggle" 
          :class="{ filled: songList[currentIndex]?.fav }" 
          @click="toggleCurrentFav" 
          :disabled="currentIndex === -1" 
          :title="songList[currentIndex]?.fav ? '取消喜欢' : '添加到我喜欢'"
        >
          {{ songList[currentIndex]?.fav ? '❤' : '♡' }}
        </button>
        <button class="icon-btn next-btn" @click="playNext">▶▶</button>
      </div>

      <div class="player-progress">
        <input class="range-progress" type="range" min="0" :max="audioDuration || 100" v-model="currentTime" @input="seekAudio" />
        <div class="time-row">
          <span class="current-time">{{ formatTime(currentTime) }}</span>
          <span class="sep">/</span>
          <span class="duration">{{ formatTime(audioDuration) }}</span>
        </div>
      </div>

      <div class="player-extra">
        <button class="icon-btn" @click="cyclePlayMode" :title="playModeTitle">{{ playModeIcon }}</button>
        <!-- 音量控制容器 - 修改触发逻辑 -->
        <div class="vol-container" 
             @mouseenter="handleVolMouseEnter" 
             @mouseleave="handleVolMouseLeave">
          <button class="icon-btn" @click="toggleMute" :title="isMuted ? '已静音' : '静音 / 音量'">{{ speakerIcon }}</button>
          <!-- 音量滑块 - 仅鼠标悬浮时显示 -->
          <div class="vol-popup" v-show="showVolSlider">
            <input class="range vol-vertical" type="range" min="0" max="1" step="0.01" v-model="audioVolume" @input="changeVolume" />
          </div>
        </div>
      </div>
    </footer>

    <!-- 管理歌曲模态 -->
    <div v-if="manageModalOpen" class="modal-overlay" @click.self="closeManageSongs">
      <div class="modal">
        <h3>从单曲集合选择歌曲</h3>
        <div class="modal-list">
          <div v-if="!songList.length" class="empty">当前没有可供选择的歌曲。</div>
          <div v-for="(s, i) in songList" :key="i" class="modal-row">
            <label>
              <input type="checkbox" :checked="manageSelection.has(i)" @change="toggleSongInManage(i)" />
              {{ s.name }} <span class="muted">{{ s.duration ? '(' + formatTime(s.duration) + ')' : '' }}</span>
            </label>
            <!-- 管理歌曲时可直接操作喜爱和删除 -->
            <button :class="['fav-btn', { filled: s.fav }]" @click.stop="toggleFav(i)" style="margin-left:8px;">{{ s.fav ? '❤' : '♡' }}</button>
            <button class="icon-btn" @click.stop="openSongDeleteConfirm(i)" title="删除" style="margin-left:6px;">🗑</button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn" @click="closeManageSongs">取消</button>
          <button class="btn btn-primary" @click="saveManageSongs">保存</button>
        </div>
      </div>
    </div>

    <!-- 单曲删除确认弹窗 -->
    <div v-if="songDeleteConfirmOpen" class="modal-overlay" @click.self="songDeleteConfirmOpen = false">
      <div class="modal">
        <h3>确认删除歌曲？</h3>
        <p class="muted">删除后将从所有歌单中移除，且无法恢复</p>
        <div class="modal-actions">
          <button class="btn green-outline" @click="songDeleteConfirmOpen = false">取消</button>
          <button class="btn danger" @click="confirmDeleteSong">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 歌单删除确认弹窗 -->
    <div v-if="deleteConfirmOpen" class="modal-overlay" @click.self="deleteConfirmOpen = false">
      <div class="modal">
        <h3>确认删除歌单？</h3>
        <p class="muted">删除后歌单内歌曲不会从单曲集合中移除</p>
        <div class="modal-actions">
          <button class="btn green-outline" @click="deleteConfirmOpen = false">取消</button>
          <button class="btn danger" @click="confirmDeletePlaylist">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 隐藏上传输入，保留可访问性 -->
    <input id="file-ctrl" ref="fileInput" class="sr-only" type="file" accept=".mp3,.wav" multiple @change="handleFileUpload" />
    <input id="cover-ctrl" ref="coverInput" class="sr-only" type="file" accept="image/*" @change="handleCoverUpload" />

    <!-- 登录 / 注册 模态 -->
    <div v-if="authModalOpen" class="modal-overlay" @click.self="closeAuth">
      <div class="modal auth-modal">
        <h3>{{ authMode === 'login' ? '登录' : '注册' }}</h3>
        <div class="auth-form">
          <template v-if="authMode === 'register'">
            <label class="auth-row">用户名 <input v-model="authForm.username" placeholder="用户名" /></label>
          </template>
          <label class="auth-row">邮箱 <input v-model="authForm.email" placeholder="邮箱" /></label>
          <label class="auth-row">密码 <input type="password" v-model="authForm.password" placeholder="密码" /></label>
          <div class="auth-error" v-if="authError">{{ authError }}</div>
          <div class="modal-actions">
            <button class="btn" @click="authMode === 'login' ? login() : register()">{{ authMode === 'login' ? '登录' : '注册' }}</button>
            <button class="btn green-outline" @click="authMode = authMode === 'login' ? 'register' : 'login'">{{ authMode === 'login' ? '去注册' : '去登录' }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed, onUnmounted } from 'vue'

// 基本播放数据
const songList = ref([])
const currentIndex = ref(-1)
const isPlaying = ref(false)
const audio = ref(new Audio())
const audioDuration = ref(0)
const currentTime = ref(0)
const audioVolume = ref(0.7)
const isMuted = ref(false)
const prevVolume = ref(audioVolume.value)
// 音量滑块显示控制
const showVolSlider = ref(false)
// 悬浮延迟定时器
let volHoverTimer = null

const speakerIcon = computed(() => {
  if (isMuted.value || Number(audioVolume.value) === 0) return '🔇'
  if (audioVolume.value < 0.5) return '🔈'
  return '🔊'
})

// 处理音量悬浮进入（延迟改为立即显示，离开时延迟2秒隐藏）
const handleVolMouseEnter = () => {
  // 清除之前的延迟定时器
  if (volHoverTimer) clearTimeout(volHoverTimer)
  showVolSlider.value = true
}

// 新增：处理音量悬浮离开（延迟2秒隐藏）
const handleVolMouseLeave = () => {
  volHoverTimer = setTimeout(() => {
    showVolSlider.value = false
  }, 2000) // 2秒延迟
}

// 单曲删除相关状态
const songDeleteConfirmOpen = ref(false)
const songDeleteIndex = ref(null)

onMounted(() => {
  audio.value.onloadedmetadata = () => {
    audioDuration.value = audio.value.duration
  }
  audio.value.ontimeupdate = () => {
    if (!audio.value.seeking) currentTime.value = audio.value.currentTime
  }
  // 播放结束时根据播放模式处理
  audio.value.onended = () => {
    if (playMode.value === 'repeat-one') {
      audio.value.currentTime = 0
      audio.value.play()
      return
    }
    if (playMode.value === 'shuffle') {
      // 随机一首
      if (!songList.value.length) return
      let rand = Math.floor(Math.random() * songList.value.length)
      // 保证不同于当前（若可能）
      if (songList.value.length > 1) {
        while (rand === currentIndex.value) {
          rand = Math.floor(Math.random() * songList.value.length)
        }
      }
      playSong(rand)
      return
    }
    // repeat-all（列表循环） 或默认行为
    if (songList.value.length === 0) return
    // 使用 playNext（已处理循环）
    playNext()
  }
  audio.value.volume = audioVolume.value
  isMuted.value = audio.value.muted || audioVolume.value === 0
})

// 组件卸载时清理定时器与释放临时封面 URL
onUnmounted(() => {
  if (volHoverTimer) clearTimeout(volHoverTimer)
  playlists.value.forEach(pl => {
    if (pl.cover && typeof pl.cover === 'string' && pl.cover.startsWith('blob:')) {
      try { URL.revokeObjectURL(pl.cover) } catch (e) {}
    }
  })
})

watch(audioVolume, (v) => {
  audio.value.volume = v
  if (Number(v) === 0) {
    isMuted.value = true
    audio.value.muted = true
  } else {
    isMuted.value = false
    audio.value.muted = false
  }
})

const fileInput = ref(null)
const coverInput = ref(null)

const openFileDialog = () => {
  if (fileInput.value) {
    // 修复：每次点击都重置文件输入，避免重复选择同一文件不触发change事件
    fileInput.value.value = ''
    fileInput.value.click()
  }
}

// 封面上传相关
const coverStyle = computed(() => {
  if (viewMode.value === 'playlist' && selectedPlaylist.value && selectedPlaylist.value.cover) {
    return { backgroundImage: `url(${selectedPlaylist.value.cover})`, backgroundSize: 'cover', backgroundPosition: 'center' }
  }
  return { backgroundImage: 'linear-gradient(90deg,#e9f7f0,#f7fff9)' }
})

const openCoverDialog = () => {
  if (coverInput.value) {
    coverInput.value.value = ''
    coverInput.value.click()
  }
}

const handleCoverUpload = (e) => {
  const f = e.target.files && e.target.files[0]
  if (!f) return
  if (!f.type || !f.type.startsWith('image/')) {
    console.warn(`文件 ${f.name} 不是图片格式`)
    return
  }
  const url = URL.createObjectURL(f)
  if (selectedPlaylist.value) {
    // 释放旧的 blob URL（若存在）
    if (selectedPlaylist.value.cover && typeof selectedPlaylist.value.cover === 'string' && selectedPlaylist.value.cover.startsWith('blob:')) {
      try { URL.revokeObjectURL(selectedPlaylist.value.cover) } catch (e) {}
    }
    selectedPlaylist.value.cover = url
    selectedPlaylist.value.coverFile = f
  }
}

const removeCover = () => {
  if (!selectedPlaylist.value || !selectedPlaylist.value.cover) return
  if (selectedPlaylist.value.cover.startsWith('blob:')) {
    try { URL.revokeObjectURL(selectedPlaylist.value.cover) } catch (e) {}
  }
  selectedPlaylist.value.cover = null
  selectedPlaylist.value.coverFile = null
}

// 身份认证相关（登录 / 注册）
const authModalOpen = ref(false)
const authMode = ref('login') // 'login' | 'register'
const authForm = ref({ username: '', email: '', password: '' })
const authError = ref('')
const currentUser = ref(null)
const token = ref(localStorage.getItem('token') || '')

const openAuth = (mode) => {
  authMode.value = mode
  authForm.value = { username: '', email: '', password: '' }
  authError.value = ''
  authModalOpen.value = true
}
const closeAuth = () => { authModalOpen.value = false }

const setToken = (t) => {
  token.value = t
  if (t) localStorage.setItem('token', t)
  else localStorage.removeItem('token')
}

const login = async () => {
  authError.value = ''
  if (!authForm.value.email || !authForm.value.password) { authError.value = '请填写邮箱与密码'; return }
  try {
    const res = await fetch('/user/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email: authForm.value.email, password: authForm.value.password }) })
    const data = await res.json()
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false
    } else {
      authError.value = data.msg || '登录失败'
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

const register = async () => {
  authError.value = ''
  if (!authForm.value.username || !authForm.value.email || !authForm.value.password) { authError.value = '请填写用户名、邮箱与密码'; return }
  try {
    const res = await fetch('/user/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ username: authForm.value.username, email: authForm.value.email, password: authForm.value.password }) })
    const data = await res.json()
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false
    } else {
      authError.value = data.msg || '注册失败'
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

const fetchUserInfo = async () => {
  if (!token.value) return
  try {
    const res = await fetch('/user/userinfo', { headers: { 'authorization': 'Bearer ' + token.value } })
    const data = await res.json()
    if (data && data.code === 200) {
      currentUser.value = data.data
    } else {
      setToken('')
      currentUser.value = null
    }
  } catch (e) {
    console.error(e)
    setToken('')
    currentUser.value = null
  }
}

const logout = async () => {
  if (token.value) {
    try { await fetch('/user/logout', { method: 'POST', headers: { 'authorization': 'Bearer ' + token.value } }) } catch (e) { console.error(e) }
  }
  setToken('')
  currentUser.value = null
  // 登出后返回主视图
  viewMode.value = 'all'
}

onMounted(() => { if (token.value) fetchUserInfo() })

// 视图：'all' 或 'fav' 或 'playlist' 或 'search'
const viewMode = ref('all')
const setView = (mode) => { 
  viewMode.value = mode 
  if (mode === 'all' || mode === 'fav') {
    selectedPlaylistId.value = null
  }
}

// 搜索相关
const searchQuery = ref('')
const searchResults = ref([])

const performSearch = () => {
  const query = searchQuery.value.trim().toLowerCase()
  if (!query) {
    viewMode.value = 'all'
    return
  }
  const results = []
  songList.value.forEach((song, index) => {
    if (song.name.toLowerCase().includes(query) || 
        song.artist.toLowerCase().includes(query)) {
      results.push({ s: song, i: index })
    }
  })
  // 检查歌单名
  playlists.value.forEach(pl => {
    if (pl.name.toLowerCase().includes(query)) {
      if (pl.songs && pl.songs.length) {
        pl.songs.forEach(idx => {
          if (songList.value[idx]) {
            results.push({ s: songList.value[idx], i: idx })
          }
        })
      }
    }
  })
  // 去重
  const uniqueResults = results.filter((item, pos, self) => 
    self.findIndex(i => i.i === item.i) === pos
  )
  searchResults.value = uniqueResults
  viewMode.value = 'search'
}

// 修复：单曲添加失败问题 - 优化文件处理逻辑，确保URL创建和时长获取稳定
const handleFileUpload = (e) => {
  const files = e.target.files
  if (!files || !files.length) return
  
  // 批量处理文件
  const newSongs = []
  Array.from(files).forEach((file) => {
    // 验证文件类型
    const fileType = file.type
    if (!['audio/mpeg', 'audio/wav', 'audio/mp3'].includes(fileType) && 
        !file.name.endsWith('.mp3') && !file.name.endsWith('.wav')) {
      // 移除不支持格式的提示弹窗
      // alert(`文件 ${file.name} 不是支持的音频格式（仅支持mp3/wav）`)
      console.warn(`文件 ${file.name} 不是支持的音频格式（仅支持mp3/wav）`)
      return
    }

    try {
      const url = URL.createObjectURL(file)
      // 提取歌曲名（移除扩展名）
      const name = file.name.replace(/\.(mp3|wav)$/i, '')
      const song = { 
        name, 
        url, 
        artist: '未知', 
        duration: 0, 
        fav: false,
        file: file // 保留文件引用，可选
      }
      newSongs.push(song)

      // 预加载获取时长
      const tmpAudio = new Audio()
      tmpAudio.src = url
      tmpAudio.onloadedmetadata = () => {
        song.duration = tmpAudio.duration || 0
        // 清理临时音频
        tmpAudio.remove()
      }
      tmpAudio.onerror = (err) => {
        console.error(`获取 ${name} 时长失败:`, err)
        song.duration = 0
      }
    } catch (err) {
      console.error(`处理文件 ${file.name} 失败:`, err)
      // 移除处理失败的提示弹窗
      // alert(`处理文件 ${file.name} 失败：${err.message}`)
    }
  })

  // 批量添加到歌曲列表
  if (newSongs.length > 0) {
    songList.value.push(...newSongs)
    
    // 自动播放第一首（如果当前没有播放）
    if (currentIndex.value === -1 && songList.value.length > 0) {
      currentIndex.value = 0
      audio.value.src = songList.value[0].url
      currentTime.value = 0
      audioDuration.value = 0
      // 可选：自动播放
      // audio.value.play().then(() => isPlaying.value = true).catch(() => isPlaying.value = false)
    }
    
    // 移除添加成功的提示弹窗
    // alert(`成功添加 ${newSongs.length} 首歌曲`)
  }
}

// Playlists (歌单数据)
const playlists = ref([])
const playlistsOpen = ref(false)
const selectedPlaylistId = ref(null)
const editing = ref(false)
const editName = ref('')
const editDesc = ref('')
const manageModalOpen = ref(false)
const manageSelection = ref(new Set())
const deleteConfirmOpen = ref(false)

const createPlaylist = () => {
  const base = '新建歌单'
  let name = base
  let i = 1
  while (playlists.value.some(p => p.name === name)) {
    name = `${base} (${i})`
    i++
  }
  const pl = { id: Date.now(), name, desc: '', songs: [], cover: null, coverFile: null }
  playlists.value.push(pl)
  // select it and switch to playlist view
  selectedPlaylistId.value = pl.id
  viewMode.value = 'playlist'
  // initialize edit fields and start editing so user can rename immediately
  editName.value = pl.name
  editDesc.value = pl.desc
  editing.value = true
  // open playlist list for visibility
  playlistsOpen.value = true
}

const selectPlaylist = (id) => {
  selectedPlaylistId.value = id
  viewMode.value = 'playlist'
  const pl = playlists.value.find(p => p.id === id)
  if (pl) {
    editName.value = pl.name
    editDesc.value = pl.desc
  }
}

const selectedPlaylist = computed(() => playlists.value.find(p => p.id === selectedPlaylistId.value) || null)

const favCount = computed(() => songList.value.filter(s => s.fav).length)

const currentTitle = computed(() => {
  if (viewMode.value === 'all') return '单曲集合'
  if (viewMode.value === 'fav') return '我喜欢的'
  if (viewMode.value === 'playlist') return selectedPlaylist.value ? selectedPlaylist.value.name : '示例歌单名'
  if (viewMode.value === 'search') return '搜索结果'
  return '示例歌单名'
})

const confirmDeletePlaylist = () => {
  if (!selectedPlaylist.value) { deleteConfirmOpen.value = false; return }
  const id = selectedPlaylist.value.id
  // 清理封面临时 URL（如果是 blob）
  const pl = selectedPlaylist.value
  if (pl.cover && typeof pl.cover === 'string' && pl.cover.startsWith('blob:')) {
    try { URL.revokeObjectURL(pl.cover) } catch (e) {}
  }
  const idx = playlists.value.findIndex(p => p.id === id)
  if (idx !== -1) playlists.value.splice(idx, 1)
  if (selectedPlaylistId.value === id) {
    selectedPlaylistId.value = null
    viewMode.value = 'all'
  }
  deleteConfirmOpen.value = false
  editing.value = false
}

// 单曲删除相关方法
const openSongDeleteConfirm = (idx) => {
  songDeleteIndex.value = idx
  songDeleteConfirmOpen.value = true
}
const confirmDeleteSong = () => {
  const idx = songDeleteIndex.value
  if (idx === null || idx === undefined) {
    songDeleteConfirmOpen.value = false
    return
  }
  // 先调整歌单内的索引：删除该索引并将大于该索引的索引向前移一位
  playlists.value.forEach(pl => {
    if (!pl.songs) return
    pl.songs = pl.songs.filter(j => j !== idx).map(j => j > idx ? j - 1 : j)
  })
  // 从主单曲列表中删除
  songList.value.splice(idx, 1)

  // 若处于搜索视图，同步移除搜索结果中的对应条目
  if (viewMode.value === 'search') {
    const results = searchResults.value.filter(item => item.i !== idx)
    // 重新映射索引
    searchResults.value = results.map(item => ({
      s: item.s,
      i: item.i > idx ? item.i - 1 : item.i
    }))
  }

  // 调整当前播放索引
  if (currentIndex.value === idx) {
    // 停止播放并清理
    audio.value.pause()
    currentIndex.value = -1
    audio.value.src = ''
    isPlaying.value = false
    currentTime.value = 0
    audioDuration.value = 0
  } else if (currentIndex.value > idx) {
    currentIndex.value = currentIndex.value - 1
  }

  // 关闭模态并重置
  songDeleteConfirmOpen.value = false
  songDeleteIndex.value = null
}

const toggleEditContent = () => {
  if (!selectedPlaylist.value) return
  if (editing.value) {
    // save
    selectedPlaylist.value.name = editName.value || selectedPlaylist.value.name
    selectedPlaylist.value.desc = editDesc.value || selectedPlaylist.value.desc
    editing.value = false
  } else {
    editing.value = true
    editName.value = selectedPlaylist.value.name
    editDesc.value = selectedPlaylist.value.desc
  }
}

const openManageSongs = () => {
  if (!selectedPlaylist.value && viewMode.value === 'playlist') return
  // 单曲集合/我喜欢的 也可打开管理歌曲
  manageSelection.value = new Set(
    viewMode.value === 'playlist' && selectedPlaylist.value
      ? selectedPlaylist.value.songs || []
      : []
  )
  manageModalOpen.value = true
}

const toggleSongInManage = (idx) => {
  const s = new Set(manageSelection.value)
  if (s.has(idx)) s.delete(idx)
  else s.add(idx)
  manageSelection.value = s
}

const saveManageSongs = () => {
  if (viewMode.value === 'playlist' && selectedPlaylist.value) {
    selectedPlaylist.value.songs = Array.from(manageSelection.value)
  }
  manageModalOpen.value = false
}

const closeManageSongs = () => { manageModalOpen.value = false }

// 修改 displayed 以支持 playlist 和 search 视图
const displayed = computed(() => {
  if (viewMode.value === 'search') return searchResults.value
  const arr = songList.value.map((s, i) => ({ s, i }))
  if (viewMode.value === 'fav') return arr.filter(x => x.s.fav)
  if (viewMode.value === 'playlist') {
    const pl = selectedPlaylist.value
    if (!pl || !pl.songs || !pl.songs.length) return []
    return pl.songs.map(i => ({ s: songList.value[i], i })).filter(x => x.s)
  }
  return arr
})

const playSong = (i) => {
  if (!songList.value.length || i < 0 || i >= songList.value.length) return
  currentIndex.value = i
  audio.value.src = songList.value[i].url
  currentTime.value = 0
  audioDuration.value = 0
  audio.value.play().then(() => isPlaying.value = true).catch((err) => {
    isPlaying.value = false
    console.error('播放失败:', err)
    // 移除播放失败的提示弹窗
    // alert(`播放 ${songList.value[i].name} 失败：${err.message}`)
  })
}

const togglePlay = () => {
  if (currentIndex.value === -1) return
  if (isPlaying.value) {
    audio.value.pause()
  } else {
    audio.value.play().catch((err) => {
      console.error('播放失败:', err)
      // 移除播放失败的提示弹窗
      // alert(`播放失败：${err.message}`)
    })
  }
  isPlaying.value = !isPlaying.value
}
const playPrev = () => {
  if (!songList.value.length) return
  if (playMode.value === 'shuffle') {
    let rand = Math.floor(Math.random() * songList.value.length)
    if (songList.value.length > 1) {
      while (rand === currentIndex.value) {
        rand = Math.floor(Math.random() * songList.value.length)
      }
    }
    playSong(rand)
    return
  }
  // 否则按顺序播放，向前循环
  let prev = (currentIndex.value - 1 + songList.value.length) % songList.value.length
  playSong(prev)
}
const playNext = () => {
  if (!songList.value.length) return
  if (playMode.value === 'shuffle') {
    let rand = Math.floor(Math.random() * songList.value.length)
    if (songList.value.length > 1) {
      while (rand === currentIndex.value) {
        rand = Math.floor(Math.random() * songList.value.length)
      }
    }
    playSong(rand)
    return
  }
  // 否则按顺序播放，循环到开头
  let next = (currentIndex.value + 1) % songList.value.length
  playSong(next)
}
const toggleMute = () => {
  if (!isMuted.value) {
    prevVolume.value = audioVolume.value
    isMuted.value = true
    audio.value.muted = true
  } else {
    isMuted.value = false
    audio.value.muted = false
    if (Number(audioVolume.value) === 0 && prevVolume.value > 0) audioVolume.value = prevVolume.value
  }
}
const changeVolume = () => {
  audio.value.volume = audioVolume.value
  if (Number(audioVolume.value) > 0 && isMuted.value) {
    isMuted.value = false
    audio.value.muted = false
  }
  if (Number(audioVolume.value) === 0) {
    isMuted.value = true
    audio.value.muted = true
  }
}
const seekAudio = () => audio.value.currentTime = currentTime.value
const toggleFav = (idx) => { if (songList.value[idx]) songList.value[idx].fav = !songList.value[idx].fav }

// 新增：底部快捷收藏当前播放歌曲的方法
const toggleCurrentFav = () => {
  // 校验当前是否有播放中的歌曲
  if (currentIndex.value === -1 || !songList.value[currentIndex.value]) return;
  // 切换收藏状态
  songList.value[currentIndex.value].fav = !songList.value[currentIndex.value].fav;
};

// 播放模式：'repeat-all'（列表循环） | 'repeat-one'（单曲循环） | 'shuffle'（列表随机）
const playMode = ref('repeat-all')
const playModeTitle = computed(() => {
  if (playMode.value === 'shuffle') return '列表随机播放'
  if (playMode.value === 'repeat-one') return '单曲循环'
  return '列表循环播放'
})
const playModeIcon = computed(() => {
  if (playMode.value === 'shuffle') return '🔀'
  if (playMode.value === 'repeat-one') return '🔂'
  return '🔁'
})
const cyclePlayMode = () => {
  const order = ['repeat-all', 'repeat-one', 'shuffle']
  const idx = order.indexOf(playMode.value)
  playMode.value = order[(idx + 1) % order.length]
}

const formatTime = (s) => {
  // 强制转为数字并校验
  const n = Number(s)
  if (!isFinite(n) || isNaN(n)) return '0:00'
  // 使用向下取整的秒数，避免浮点尾数（例如 123.3）导致显示异常
  const total = Math.floor(n)
  const m = Math.floor(total / 60)
  const sec = total % 60
  return `${m}:${sec.toString().padStart(2, '0')}`
}

// 个人主页相关状态
const editingProfile = ref(false)
const editProfileForm = ref({ username: '' })
const avatarInput = ref(null)

// 收藏歌曲列表（计算属性，复用原有数据）
const favSongs = computed(() => {
  return songList.value.map((s, i) => ({ s, i })).filter(x => x.s.fav)
})

// 切换编辑个人信息状态
const toggleEditProfile = () => {
  if (!currentUser.value) return
  if (editingProfile.value) {
    // 保存修改（仅前端展示，不修改后端）
    if (editProfileForm.username) {
      currentUser.value.username = editProfileForm.username
    }
    editingProfile.value = false
  } else {
    // 进入编辑状态，初始化表单
    editProfileForm.username = currentUser.value.username
    editingProfile.value = true
  }
}

// 打开头像上传对话框
const openAvatarDialog = () => {
  if (avatarInput.value) {
    avatarInput.value.value = ''
    avatarInput.value.click()
  }
}

// 处理头像上传（仅前端展示）
const handleAvatarUpload = (e) => {
  const f = e.target.files && e.target.files[0]
  if (!f) return
  if (!f.type || !f.type.startsWith('image/')) {
    console.warn(`文件 ${f.name} 不是图片格式`)
    return
  }
  const url = URL.createObjectURL(f)
  if (currentUser.value) {
    // 释放旧的头像 blob URL（若存在）
    if (currentUser.value.avatar && currentUser.value.avatar.startsWith('blob:')) {
      try { URL.revokeObjectURL(currentUser.value.avatar) } catch (e) {}
    }
    currentUser.value.avatar = url
    currentUser.value.avatarFile = f
  }
}

// 组件卸载时清理头像 blob URL（追加到原有清理逻辑后）
onUnmounted(() => {
  if (currentUser.value && currentUser.value.avatar && currentUser.value.avatar.startsWith('blob:')) {
    try { URL.revokeObjectURL(currentUser.value.avatar) } catch (e) {}
  }
})
</script>