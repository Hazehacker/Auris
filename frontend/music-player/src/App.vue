import { api } from './api.js'
<template>
  <!-- 首屏登录引导层 (带渐入渐出动画 ) -->
  <div 
    class="landing-overlay" 
    v-show="landingOpen"
    :class="{ fadeout: landingFadeout }"
  >
    <div class="landing-container">
      <div class="landing-brand">
        <div class="logo big">🎧 <span class="brand">Auris</span></div>
        <p class="landing-slogan">沉浸式本地音乐播放器 · 收藏你的专属旋律</p>
      </div>
      <div class="landing-actions">
        <button class="btn primary big" @click="openAuth('login')">
          登录 / 注册
        </button>
        <button class="btn outline big" @click="enterGuest">
          游客模式进入
        </button>
      </div>
      <div class="landing-footer">
        <p>游客模式支持本地音乐导入，登录后可同步歌单与收藏</p>
      </div>
    </div>
  </div>

  <div :class="['player-container', { 'no-footer': landingOpen }]">
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
    <footer v-if="!landingOpen" class="bottom-bar">
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

    <!-- 登录 / 注册 模态（全局唯一，开屏/内部共用 ✅ 核心统一） -->
    <div v-if="authModalOpen" class="modal-overlay" @click.self="closeAuth">
      <div class="modal auth-modal">
        <h3>{{ authMode === 'login' ? '登录' : '注册' }}</h3>
        <div class="auth-form">
          <template v-if="authMode === 'register'">
            <label class="auth-row">用户名 <input v-model="authForm.username" placeholder="用户名" /></label>
          </template>
          <label class="auth-row">邮箱 <input ref="authEmailInput" v-model="authForm.email" placeholder="邮箱" /></label>
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
import { ref, watch, onMounted, computed, onUnmounted, nextTick } from 'vue'

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
  if (volHoverTimer) clearTimeout(volHoverTimer)
  showVolSlider.value = true
}

// 处理音量悬浮离开（延迟2秒隐藏）
const handleVolMouseLeave = () => {
  volHoverTimer = setTimeout(() => {
    showVolSlider.value = false
  }, 2000)
}

// 单曲删除相关状态
const songDeleteConfirmOpen = ref(false)
const songDeleteIndex = ref(null)


const fetchPlaylists = async () => {
  const res = await fetch('/playlist/list', {
    headers: { 'Authorization': `Bearer ${token.value}` }
  })
  const data = await res.json()
  if (data.code === 200) {
    playlists.value = data.data
  }
}

//初始化加载用户数据
const fetchFavTracks = async () => {
  const res = await fetch('/track/favList', {
    headers: { 'Authorization': `Bearer ${token.value}` }
  })
  const data = await res.json()
  if (data.code === 200) {
    // 将 favList 合并到 songList（去重）
    const favMap = new Map()
    data.data.forEach(track => {
      favMap.set(track.id, { ...track, fav: true })
    })
    // 更新 songList 中的 fav 状态
    songList.value = songList.value.map(s => 
      favMap.has(s.id) ? { ...s, fav: true } : s
    )
  }
}

onMounted(() => {
  audio.value.onloadedmetadata = () => {
    audioDuration.value = audio.value.duration
  }
  audio.value.ontimeupdate = () => {
    if (!audio.value.seeking) currentTime.value = audio.value.currentTime
  }
  audio.value.onended = () => {
    if (playMode.value === 'repeat-one') {
      audio.value.currentTime = 0
      audio.value.play()
      return
    }
    if (playMode.value === 'shuffle') {
      if (!songList.value.length) return
      let rand = Math.floor(Math.random() * songList.value.length)
      if (songList.value.length > 1) {
        while (rand === currentIndex.value) {
          rand = Math.floor(Math.random() * songList.value.length)
        }
      }
      playSong(rand)
      return
    }
    if (songList.value.length === 0) return
    playNext()
  }
  audio.value.volume = audioVolume.value
  isMuted.value = audio.value.muted || audioVolume.value === 0

  // 首屏初始化：读取本地存储，控制是否展示
  const isDismissed = localStorage.getItem('landing-dismissed') === '1'
  if (isDismissed) landingOpen.value = false
  
  // 初始化用户信息
  if (token.value) fetchUserInfo()
})

// 组件卸载时清理定时器与释放临时封面 URL
onUnmounted(() => {
  if (volHoverTimer) clearTimeout(volHoverTimer)
  playlists.value.forEach(pl => {
    if (pl.cover && typeof pl.cover === 'string' && pl.cover.startsWith('blob:')) {
      try { URL.revokeObjectURL(pl.cover) } catch (e) {}
    }
  })
  if (currentUser.value && currentUser.value.avatar && currentUser.value.avatar.startsWith('blob:')) {
    try { URL.revokeObjectURL(currentUser.value.avatar) } catch (e) {}
  }
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

// 身份认证核心（全局唯一，开屏/内部共用，登录/注册成功才关开屏）
const authModalOpen = ref(false)
const authMode = ref('login')
const authForm = ref({ username: '', email: '', password: '' })
const authError = ref('')
// 聚焦引用：打开登录时自动聚焦到邮箱输入框
const authEmailInput = ref(null)
// 标记登录/注册是否来自开屏（用于失败时恢复开屏）
const loginFromLanding = ref(false)
const currentUser = ref(null)
const token = ref(localStorage.getItem('token') || '')

// 统一登录弹窗打开方法（开屏/内部调用完全一致，弹窗样式、逻辑统一）
const openAuth = (mode) => {
  authMode.value = mode
  authForm.value = { username: '', email: '', password: '' }
  authError.value = ''
  // 如果当前在开屏上打开登录，则先淡出并关闭开屏（用户要求：点击开屏的登录/注册应当关闭开屏）
  if (landingOpen.value) {
    loginFromLanding.value = true
    landingFadeout.value = true
    // 与过渡时长保持一致（0.5s），过渡后移除开屏
    setTimeout(() => {
      landingOpen.value = false
      landingFadeout.value = false
    }, 500)
  }
  authModalOpen.value = true
  // 打开后自动聚焦到邮箱输入
  nextTick(() => {
    try { authEmailInput.value && authEmailInput.value.focus() } catch (e) {}
  })
}
const closeAuth = () => {
  authModalOpen.value = false
  // 如果打开来源于开屏且还未完成登录，则恢复开屏（比如用户主动关闭模态）
  if (loginFromLanding.value) {
    landingOpen.value = true
    loginFromLanding.value = false
  }
}

// 首屏核心配置（核心：仅登录/注册成功、游客点击 才关闭）
const landingOpen = ref(true)
const landingFadeout = ref(false)
// 不再持久化首屏关闭状态（每次访问都显示）
const setLandingDismissed = (v = true) => { /* noop */ }

// 游客模式：点击后关闭开屏
const enterGuest = () => {
  landingFadeout.value = true
  setTimeout(() => {
    landingOpen.value = false
    setLandingDismissed(true)
    viewMode.value = 'profile'
  }, 500)
}  

const setToken = (t) => {
  token.value = t
  if (t) localStorage.setItem('token', t)
  else localStorage.removeItem('token')
}

// 登录成功 → 关闭登录弹窗 + 关闭开屏动画 + 进入个人主页
const login = async () => {
  authError.value = ''
  if (!authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写邮箱与密码'; return 
  }
  try {
    const res = await fetch('/user/login', { 
      method: 'POST', 
      headers: { 'Content-Type': 'application/json' }, 
      body: JSON.stringify({ email: authForm.value.email, password: authForm.value.password }) 
    })
    const data = await res.json()
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false // 关闭登录弹窗
      // 核心：登录成功后 才关闭开屏动画
      landingFadeout.value = true
      setTimeout(() => {
        landingOpen.value = false
        setLandingDismissed(true)
        viewMode.value = 'profile'
      }, 500)
    } else {
      // 登录失败：若登录是从开屏发起，则关闭模态并恢复开屏
      authError.value = data.msg || '登录失败'
      if (loginFromLanding.value) {
        authModalOpen.value = false
        // 等待模态闭合再显示开屏，避免视觉跳跃
        setTimeout(() => { landingOpen.value = true }, 160)
        loginFromLanding.value = false
      }
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

// 注册成功 → 关闭登录弹窗 + 关闭开屏动画 + 进入个人主页
const register = async () => {
  authError.value = ''
  if (!authForm.value.username || !authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写用户名、邮箱与密码'; return 
  }
  try {
    const res = await fetch('/user/register', { 
      method: 'POST', 
      headers: { 'Content-Type': 'application/json' }, 
      body: JSON.stringify({ username: authForm.value.username, email: authForm.value.email, password: authForm.value.password }) 
    })
    const data = await res.json()
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false // 关闭注册弹窗
      // 核心：注册成功后 才关闭开屏动画
      landingFadeout.value = true
      setTimeout(() => {
        landingOpen.value = false
        setLandingDismissed(true)
        viewMode.value = 'profile'
      }, 500)
    } else {
      authError.value = data.msg || '注册失败'
      if (loginFromLanding.value) {
        authModalOpen.value = false
        setTimeout(() => { landingOpen.value = true }, 160)
        loginFromLanding.value = false
      }
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
  viewMode.value = 'all'
}

// 视图控制
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
  const uniqueResults = results.filter((item, pos, self) => 
    self.findIndex(i => i.i === item.i) === pos
  )
  searchResults.value = uniqueResults
  viewMode.value = 'search'
}

// 文件上传处理
const handleFileUpload = async (e) => {
  const files = e.target.files
  if (!files || !files.length) return

  for (const file of files) {
    if (!['audio/mpeg', 'audio/wav'].includes(file.type)) continue

    const formData = new FormData()
    formData.append('file', file)
    formData.append('name', file.name.replace(/\.(mp3|wav)$/i, ''))
    formData.append('artist', '未知')

    try {
      const res = await fetch('/track/upload', {
        method: 'POST',
        headers: {
          'Authorization': token.value ? `Bearer ${token.value}` : ''
        },
        body: formData
      })
      const data = await res.json()
      if (data.code === 200) {
        // 假设后端返回 { id, name, artist, url, duration }
        songList.value.push({
          id: data.data.id,
          name: data.data.name,
          artist: data.data.artist,
          url: data.data.url,
          duration: data.data.duration || 0,
          fav: false
        })
      }
    } catch (err) {
      console.error('上传失败:', err)
    }
  }
}

// 歌单相关
const playlists = ref([])
const playlistsOpen = ref(false)
const selectedPlaylistId = ref(null)
const editing = ref(false)
const editName = ref('')
const editDesc = ref('')
const manageModalOpen = ref(false)
const manageSelection = ref(new Set())
const deleteConfirmOpen = ref(false)

const createPlaylist = async () => {
  const base = '新建歌单'
  let name = base
  let i = 1
  while (playlists.value.some(p => p.name === name)) {
    name = `${base} (${i})`
    i++
  }

  try {
    const res = await fetch('/playlist/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify({ name, desc: '' })
    })
    const data = await res.json()
    if (data.code === 200) {
      playlists.value.push(data.data) // 假设返回完整 playlist 对象
      selectPlaylist(data.data.id)
    }
  } catch (err) {
    console.error('创建歌单失败', err)
  }
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

const confirmDeletePlaylist = async () => {
  if (!selectedPlaylist.value) return
  try {
    const res = await fetch(`/playlist/delete/${selectedPlaylist.value.id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${token.value}` }
    })
    const data = await res.json()
    if (data.code === 200) {
      // 从列表移除
      playlists.value = playlists.value.filter(p => p.id !== selectedPlaylist.value.id)
      viewMode.value = 'all'
      selectedPlaylistId.value = null
    }
  } catch (err) {
    console.error('删除失败', err)
  }
  deleteConfirmOpen.value = false
}

// 单曲删除相关
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
  playlists.value.forEach(pl => {
    if (!pl.songs) return
    pl.songs = pl.songs.filter(j => j !== idx).map(j => j > idx ? j - 1 : j)
  })
  songList.value.splice(idx, 1)
  if (viewMode.value === 'search') {
    const results = searchResults.value.filter(item => item.i !== idx)
    searchResults.value = results.map(item => ({
      s: item.s,
      i: item.i > idx ? item.i - 1 : item.i
    }))
  }
  if (currentIndex.value === idx) {
    audio.value.pause()
    currentIndex.value = -1
    audio.value.src = ''
    isPlaying.value = false
    currentTime.value = 0
    audioDuration.value = 0
  } else if (currentIndex.value > idx) {
    currentIndex.value = currentIndex.value - 1
  }
  songDeleteConfirmOpen.value = false
  songDeleteIndex.value = null
}

const toggleEditContent = () => {
  if (!selectedPlaylist.value) return
  if (editing.value) {
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

const saveManageSongs = async () => {
  if (viewMode.value === 'playlist' && selectedPlaylist.value) {
    const trackIds = Array.from(manageSelection.value).map(i => songList.value[i].id)
    
    try {
      const res = await fetch('/playlist/updateTracks', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token.value}`
        },
        body: JSON.stringify({
          playlistId: selectedPlaylist.value.id,
          trackIds: trackIds
        })
      })
      const data = await res.json()
      if (data.code === 200) {
        // 后端更新成功，前端同步（可选）
        selectedPlaylist.value.songs = Array.from(manageSelection.value)
      }
    } catch (err) {
      console.error('更新歌单失败', err)
    }
  }
  manageModalOpen.value = false
}

const closeManageSongs = () => { manageModalOpen.value = false }

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

// 播放控制相关
const playSong = (i) => {
  if (!songList.value.length || i < 0 || i >= songList.value.length) return
  currentIndex.value = i
  audio.value.src = songList.value[i].url
  currentTime.value = 0
  audioDuration.value = 0
  audio.value.play().then(() => isPlaying.value = true).catch((err) => {
    isPlaying.value = false
    console.error('播放失败:', err)
  })
}

const togglePlay = () => {
  if (currentIndex.value === -1) return
  if (isPlaying.value) {
    audio.value.pause()
  } else {
    audio.value.play().catch((err) => {
      console.error('播放失败:', err)
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
const toggleFav = async (idx) => {
  const song = songList.value[idx]
  if (!song.id) return

  try {
    if (song.fav) {
      await fetch(`/track/unfav/${song.id}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token.value}` }
      })
    } else {
      await fetch(`/track/fav/${song.id}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token.value}` }
      })
    }
    song.fav = !song.fav
  } catch (err) {
    console.error('收藏操作失败', err)
  }
}
const toggleCurrentFav = () => {
  if (currentIndex.value === -1 || !songList.value[currentIndex.value]) return;
  songList.value[currentIndex.value].fav = !songList.value[currentIndex.value].fav;
};

// 播放模式
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
  const n = Number(s)
  if (!isFinite(n) || isNaN(n)) return '0:00'
  const total = Math.floor(n)
  const m = Math.floor(total / 60)
  const sec = total % 60
  return `${m}:${sec.toString().padStart(2, '0')}`
}

// 个人主页相关
const editingProfile = ref(false)
const editProfileForm = ref({ username: '' })
const avatarInput = ref(null)
const favSongs = computed(() => {
  return songList.value.map((s, i) => ({ s, i })).filter(x => x.s.fav)
})

const toggleEditProfile = () => {
  if (!currentUser.value) return
  if (editingProfile.value) {
    // 保存到后端
    saveProfile()
  } else {
    // 进入编辑模式
    editProfileForm.value = {
      username: currentUser.value.username,
      email: currentUser.value.email,
      gender: currentUser.value.gender ?? 1 // 默认男
    }
    editingProfile.value = true
  }
}

// 新增：保存用户资料
const saveProfile = async () => {
  const { username, email, gender } = editProfileForm.value

  // 基础校验（可选）
  if (!username.trim() || username.length > 30) {
    alert('用户名不能为空且不超过30字符')
    return
  }

  try {
    const res = await fetch('/user/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify({ username, email, gender })
    })
    const data = await res.json()
    if (data.code === 200) {
      // 更新前端 currentUser
      currentUser.value = { ...currentUser.value, ...data.data }
      editingProfile.value = false
    } else {
      alert(data.msg || '保存失败')
    }
  } catch (err) {
    console.error(err)
    alert('网络错误')
  }
}

const openAvatarDialog = () => {
  if (avatarInput.value) {
    avatarInput.value.value = ''
    avatarInput.value.click()
  }
}

// 新增：上传文件到后端
const uploadFile = async (file) => {
  const formData = new FormData()
  formData.append('file', file)
  const res = await fetch('/file/upload', {
    method: 'POST',
    headers: { 'Authorization': `Bearer ${token.value}` },
    body: formData
  })
  const data = await res.json()
  if (data.code === 200) {
    return data.data.url // 假设返回 { url }
  } else {
    throw new Error(data.msg || '上传失败')
  }
}

// 修改 handleAvatarUpload
const handleAvatarUpload = async (e) => {
  const f = e.target.files?.[0]
  if (!f || !f.type.startsWith('image/')) return

  try {
    // 1. 上传头像
    const avatarUrl = await uploadFile(f)
    
    // 2. 更新用户信息
    const res = await fetch('/user/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token.value}`
      },
      body: JSON.stringify({ avatar: avatarUrl })
    })
    const data = await res.json()
    if (data.code === 200) {
      currentUser.value.avatar = avatarUrl
      // 清理旧 blob URL（如果之前是本地预览）
      if (currentUser.value.avatar?.startsWith('blob:')) {
        URL.revokeObjectURL(currentUser.value.avatar)
      }
    } else {
      alert(data.msg || '头像更新失败')
    }
  } catch (err) {
    console.error(err)
    alert('头像上传失败')
  }
}


</script>