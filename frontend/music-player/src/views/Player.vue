import { api } from '../api.js'
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
                        <div class="avatar-section" role="button" tabindex="0" @click="showProfileModal = true" @keydown.enter="showProfileModal = true">
                            <img v-if="currentUser.avatar" :src="currentUser.avatar" alt="avatar" class="avatar" />
                            <span class="username">{{ currentUser.username }}</span>
                        </div>
                        <button class="btn small" @click="logout">退出</button>
                    </template>
                    <template v-else>
                        <div class="avatar-section" role="button" tabindex="0" @click="showProfileModal = true" @keydown.enter="showProfileModal = true">
                            <div class="avatar-placeholder"></div>
                        </div>
                        <button class="btn" @click="openAuth('login')">登录</button>
                        <button class="btn green-outline" @click="openAuth('register')">注册</button>
                    </template>
                </div>
                <button class="theme-toggle-btn" @click="toggleTheme" :title="isDarkMode ? '切换到日间模式' : '切换到夜间模式'">
                    {{ isDarkMode ? '☀️' : '🌙' }}
                </button>
            </div>
        </header>

        <div class="main-area">
            <!-- 左侧侧栏 -->
            <aside class="sidebar">
                <ul class="sidebar-list">
                    <li class="side-item import" role="button" tabindex="0" @click="openFileDialog" @keydown.enter="openFileDialog">⇪  导入本地音乐</li>
                    <li class="side-item web">☁  网页音频提取</li>
                    <li class="side-item collection" role="button" tabindex="0" @click="setView('all')" @keydown.enter="setView('all')" :class="{ active: viewMode === 'all' }">▾ 单曲集合 <span class="count">({{ songList.length }})</span></li>
                    <li class="side-item fav" role="button" tabindex="0" @click="setView('fav')" @keydown.enter="setView('fav')" :class="{ active: viewMode === 'fav' }">❤ 我喜欢的 <span class="count">({{ favCount }})</span></li>

                    <!-- 歌单列表（可展开） -->
                    <li class="side-item playlists" role="button" tabindex="0" @click="playlistsOpen = !playlistsOpen">
                        <span class="expand-icon">{{ playlistsOpen ? '▾' : '▸' }}</span><span class="playlists-title">歌单列表</span>
                    </li>
                    <ul v-if="playlistsOpen" class="playlist-children">
                        <li class="side-item create" role="button" tabindex="0" @click="createPlaylist">＋  创建歌单</li>
                        <li v-if="!playlists.length" class="side-item empty-note">（当前无歌单）</li>
                        <li v-for="pl in playlists" :key="pl.id" class="side-item playlist-item" :class="{ active: selectedPlaylistId === pl.id }" role="button" tabindex="0">
                            <span @click.stop="selectPlaylist(pl.id)" class="playlist-name">{{ pl.name }} <span class="count">({{ pl.songs ? pl.songs.length : 0 }})</span></span>
                            <div class="playlist-actions">
                                <button class="playlist-edit-btn"
                                        @click.stop="openEditPlaylistNameModal(pl.id)"
                                        :title="'修改歌单名称'">
                                    ✏️
                                </button>
                                <button class="playlist-delete-btn"
                                        @click.stop="openDeletePlaylistConfirm(pl.id)"
                                        :title="'删除歌单'">
                                    🗑️
                                </button>
                            </div>
                        </li>
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
                                <img v-if="currentUser && currentUser.avatar"
                                     :src="currentUser.avatar"
                                     alt="用户头像"
                                     class="avatar-lg" />
                                <div v-else class="avatar-placeholder">
                                    {{ currentUser ? currentUser.username.charAt(0) : '👤' }}
                                </div>
                                <!-- 编辑头像按钮（登录后显示） -->
                                <button v-if="currentUser"
                                        class="btn small edit-avatar-btn"
                                        @click="openAvatarDialog">
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
                                <button v-if="currentUser"
                                        class="btn green-outline profile-edit-btn"
                                        @click="toggleEditProfile">
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
                                        <td class="title-col">
                                            <div class="title-with-play">
                                                <button class="play-icon-btn"
                                                        @click.stop="handlePlayButtonClick(i)"
                                                        :title="currentIndex === i && isPlaying ? '暂停' : '播放 ' + (s.name || '歌曲')"
                                                        :disabled="!s.url || s.url === ''">
                                                    {{ currentIndex === i && isPlaying ? '⏸' : '▶' }}
                                                </button>
                                                <span class="song-title-text">{{ s.name || '未知' }}</span>
                                            </div>
                                        </td>
                                        <td class="time-col">{{ s.duration ? formatTime(s.duration) : '—' }}</td>
                                        <td class="artist-col">{{ s.artist || '—' }}</td>
                                        <td class="fav-col"><button :class="['fav-btn', { filled: s.fav }]" @click.stop="toggleFav(i)">{{ s.fav ? '❤' : '♡' }}</button></td>
                                        <td class="action-col">
                                            <div class="action-buttons">
                                                <button v-if="!s.url || s.url === ''"
                                                        class="icon-btn action-btn"
                                                        @click.stop="openUploadAudioModal(i)"
                                                        :title="'上传音频 ' + (s.name || '歌曲')">
                                                    📤
                                                </button>
                                                <button class="icon-btn action-btn"
                                                        @click.stop="openUploadCoverModal(i)"
                                                        :title="'上传封面 ' + (s.name || '歌曲')">
                                                    🖼️
                                                </button>
                                                <button class="icon-btn action-btn danger"
                                                        @click.stop="openSongDeleteConfirm(i)"
                                                        :title="'删除 ' + (s.name || '歌曲')">
                                                    🗑
                                                </button>
                                            </div>
                                        </td>
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
                                            <stop offset="0" stop-color="var(--bg-secondary)" />
                                            <stop offset="1" stop-color="var(--bg-tertiary)" />
                                        </linearGradient>
                                    </defs>
                                    <rect x="6" y="6" width="52" height="52" rx="8" fill="url(#coverGrad)" />
                                    <path d="M40 20v16a6 6 0 1 1-4-5.2V22l-10 3v12" fill="none" stroke="var(--primary)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
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
                            <!-- 添加歌曲按钮（仅歌单模式） -->
                            <button v-if="viewMode === 'playlist'" class="btn green" :disabled="!selectedPlaylist" @click="openAddTrackModal">
                                ＋ 添加歌曲
                            </button>
                            <!-- 管理歌曲按钮（所有模式启用） -->
                            <button class="btn green-outline" @click="openManageSongs">管理歌曲</button>
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
                                <td class="title-col">
                                    <div class="title-with-play">
                                        <button class="play-icon-btn"
                                                @click.stop="handlePlayButtonClick(i)"
                                                :title="currentIndex === i && isPlaying ? '暂停' : '播放 ' + (s.name || '歌曲')"
                                                :disabled="!s.url || s.url === ''">
                                            {{ currentIndex === i && isPlaying ? '⏸' : '▶' }}
                                        </button>
                                        <span class="song-title-text">{{ s.name || '未知' }}</span>
                                    </div>
                                </td>
                                <td class="time-col">{{ s.duration ? formatTime(s.duration) : '—' }}</td>
                                <td class="artist-col">{{ s.artist || '—' }}</td>
                                <td class="fav-col"><button :class="['fav-btn', { filled: s.fav }]" @click.stop="toggleFav(i)">{{ s.fav ? '❤' : '♡' }}</button></td>
                                <td class="action-col">
                                    <div class="action-buttons">
                                        <button v-if="!s.url || s.url === ''"
                                                class="icon-btn action-btn"
                                                @click.stop="openUploadAudioModal(i)"
                                                :title="'上传音频 ' + (s.name || '歌曲')">
                                            📤
                                        </button>
                                        <button class="icon-btn action-btn"
                                                @click.stop="openUploadCoverModal(i)"
                                                :title="'上传封面 ' + (s.name || '歌曲')">
                                            🖼️
                                        </button>
                                        <button class="icon-btn action-btn danger"
                                                @click.stop="openSongDeleteConfirm(i)"
                                                :title="'删除 ' + (s.name || '歌曲')">
                                            🗑
                                        </button>
                                    </div>
                                </td>
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
        <footer class="bottom-bar"@click.self="toggleDetail">
            <div class="player-controls">
                <button class="icon-btn prev-btn" @click="playPrev">◀◀</button>
                <button class="play-btn" :class="{ playing: isPlaying }" @click="togglePlay">{{ isPlaying ? '暂停' : '播放' }}</button>
                <!-- 优化爱心按钮的边界校验逻辑 -->
                <button class="icon-btn fav-toggle"
                        :class="{ filled: songList[currentIndex]?.fav }"
                        @click="toggleCurrentFav"
                        :disabled="currentIndex === -1"
                        :title="songList[currentIndex]?.fav ? '取消喜欢' : '添加到我喜欢'">
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

        <!-- 个人主页模态框 -->
        <transition name="fade">
            <section v-if="showProfileModal" class="profile-modal">
                <!-- 遮罩 -->
                <div class="modal-mask" @click="showProfileModal = false"></div>
                
                <div class="modal-content">
                    <!-- 关闭按钮 -->
                    <button class="modal-close" @click="showProfileModal = false">×</button>
                    
                    <!-- 个人主页内容 -->
                    <div class="profile-header">
                        <!-- 个人信息卡片 -->
                        <div class="profile-card">
                            <div class="profile-avatar">
                                <img v-if="currentUser && currentUser.avatar"
                                     :src="currentUser.avatar"
                                     alt="用户头像"
                                     class="avatar-lg" />
                                <div v-else class="avatar-placeholder">
                                    {{ currentUser ? currentUser.username.charAt(0) : '👤' }}
                                </div>
                                <!-- 编辑头像按钮（登录后显示） -->
                                <button v-if="currentUser"
                                        class="btn small edit-avatar-btn"
                                        @click="openAvatarDialog">
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
                                <button v-if="currentUser"
                                        class="btn green-outline profile-edit-btn"
                                        @click="toggleEditProfile">
                                    {{ editingProfile ? '保存' : '编辑信息' }}
                                </button>
                            </div>
                        </div>

                        <!-- 个人主页下的快捷入口 -->
                        <div class="profile-actions">
                            <button class="btn green" @click="setView('all'); showProfileModal = false">查看所有歌曲</button>
                            <button class="btn green" @click="setView('fav'); showProfileModal = false">查看收藏</button>
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
                                        <th style="width: 50px;">序号</th>
                                        <th style="width: 50px;">播放</th>
                                        <th style="width: 50%;">歌曲标题</th>
                                        <th style="width: 20%;">艺术家</th>
                                        <th style="width: 10%;">时长</th>
                                        <th style="width: 50px;">操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="(song, index) in songList.filter(s => s.fav)" 
                                        :key="song.id" 
                                        class="song-row"
                                        @click="playSong(song)" 
                                        :class="{ active: currentSong && currentSong.id === song.id }">
                                        <td>{{ index + 1 }}</td>
                                        <td>
                                            <button class="btn-icon small" @click.stop="togglePlayPause(song)">
                                                {{ currentSong && currentSong.id === song.id && isPlaying ? '⏸️' : '▶️' }}
                                            </button>
                                        </td>
                                        <td>{{ song.title }}</td>
                                        <td>{{ song.artist }}</td>
                                        <td>{{ formatTime(song.duration) }}</td>
                                        <td>
                                            <button class="btn-icon" @click.stop="toggleFavorite(song)">
                                                {{ song.fav ? '❤️' : '🤍' }}
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </section>
                    </div>
                </div>
            </section>
        </transition>
        
        <!-- 单曲详情 / 歌词面板 -->
        <transition name="slide-up">
            <section v-show="showDetail"
                     class="song-detail"
                     @click.self="showDetail = false">
                <!-- 遮罩 -->
                <div class="detail-mask" @click="showDetail = false"></div>

                <div class="detail-content">
                    <!-- 顶部：封面 + 信息 + 歌词 -->
                    <div class="detail-top">
                        <!-- 左侧：封面 + 信息 -->
                        <aside class="detail-left">
                            <img class="cover"
                                 :src="currentSong?.coverUrl || defaultCover"
                                 alt="cover" />
                            <h2 class="title">{{ currentSong?.name || '未知歌曲' }}</h2>
                            <p class="artist">{{ currentSong?.artist || '未知歌手' }}</p>
                            <p class="album">{{ currentSong?.album || '未知专辑' }}</p>
                        </aside>

                        <!-- 右侧：滚动歌词 -->
                        <main class="detail-right">
                            <ul ref="lrcList" class="lrc-list">
                                <li v-for="(line, idx) in parsedLrc"
                                    :key="idx"
                                    :class="{ active: idx === activeLrcIndex }">
                                    {{ line.text }}
                                </li>
                            </ul>
                        </main>
        </div>

            <!-- 内置播放器控制栏 -->
            <footer class="bottom-bar built-in" @click.self="showDetail = false">
                <div class="player-controls">
                    <button class="icon-btn prev-btn" @click="playPrev">◀◀</button>
                    <button class="play-btn" :class="{ playing: isPlaying }" @click="togglePlay">{{ isPlaying ? '暂停' : '播放' }}</button>
                    <button class="icon-btn fav-toggle"
                            :class="{ filled: songList[currentIndex]?.fav }"
                            @click="toggleCurrentFav"
                            :disabled="currentIndex === -1"
                            :title="songList[currentIndex]?.fav ? '取消喜欢' : '添加到我喜欢'">
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
                    <div class="vol-container"
                         @mouseenter="handleVolMouseEnter"
                         @mouseleave="handleVolMouseLeave">
                        <button class="icon-btn" @click="toggleMute" :title="isMuted ? '已静音' : '静音 / 音量'">{{ speakerIcon }}</button>
                        <div class="vol-popup" v-show="showVolSlider">
                            <input class="range vol-vertical" type="range" min="0" max="1" step="0.01" v-model="audioVolume" @input="changeVolume" />
                        </div>
                    </div>
                </div>
            </footer>
        </div>
            </section>
        </transition>

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
                <p class="muted">
                    确定要删除歌单"<strong>{{ deletingPlaylistId ? playlists.find(p => p.id === deletingPlaylistId)?.name : selectedPlaylist?.name || '' }}</strong>"吗？
                </p>
                <p class="muted" style="font-size: 0.85rem; margin-top: 0.5rem;">删除后歌单内歌曲不会从单曲集合中移除</p>
                <div class="modal-actions">
                    <button class="btn green-outline" @click="deleteConfirmOpen = false">取消</button>
                    <button class="btn danger" @click="confirmDeletePlaylist">确认删除</button>
                </div>
            </div>
        </div>

        <!-- 隐藏上传输入，保留可访问性 -->
        <input id="file-ctrl" ref="fileInput" class="sr-only" type="file" accept=".mp3,.wav" multiple @change="handleFileUpload" />
        <input id="cover-ctrl" ref="coverInput" class="sr-only" type="file" accept="image/*" @change="handleCoverUpload" />

        <!-- 修改歌单名称模态 -->
        <div v-if="editPlaylistNameModalOpen" class="modal-overlay" @click.self="closeEditPlaylistNameModal">
            <div class="modal edit-playlist-name-modal">
                <h3>修改歌单名称</h3>
                <div class="edit-playlist-name-form">
                    <label class="form-row">
                        <span class="form-label">歌单名称 <span class="required">*</span></span>
                        <input ref="editPlaylistNameInput"
                               v-model="editPlaylistNameForm.name"
                               placeholder="请输入歌单名称"
                               maxlength="50"
                               @keydown.enter="confirmEditPlaylistName" />
                        <div class="form-error" v-if="editPlaylistNameError.name">{{ editPlaylistNameError.name }}</div>
                    </label>
                    <div class="form-error" v-if="editPlaylistNameError.general">{{ editPlaylistNameError.general }}</div>
                    <div class="modal-actions">
                        <button class="btn green-outline" @click="closeEditPlaylistNameModal" :disabled="editingPlaylistName">取消</button>
                        <button class="btn green" @click="confirmEditPlaylistName" :disabled="editingPlaylistName">
                            <span v-if="editingPlaylistName">保存中...</span>
                            <span v-else>保存</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 上传封面模态 -->
        <div v-if="uploadCoverModalOpen" class="modal-overlay" @click.self="closeUploadCoverModal">
            <div class="modal upload-cover-modal">
                <h3>上传封面图片</h3>
                <div class="upload-cover-form">
                    <div class="form-section">
                        <h4 class="form-section-title">歌曲信息</h4>
                        <div class="song-info-display">
                            <div class="info-item">
                                <span class="info-label">歌曲名称：</span>
                                <span class="info-value">{{ uploadCoverSong?.name || '未知哦哦哦' }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">歌手：</span>
                                <span class="info-value">{{ uploadCoverSong?.artist || '未知' }}</span>
                            </div>
                        </div>
                    </div>

                    <div class="form-section">
                        <h4 class="form-section-title">封面图片</h4>
                        <div class="file-upload-area">
                            <input ref="uploadCoverFileInput"
                                   type="file"
                                   accept="image/*"
                                   @change="handleUploadCoverFileSelect"
                                   class="sr-only"
                                   id="upload-cover-file-input" />
                            <div v-if="!uploadCoverForm.file" class="file-upload-placeholder" @click="openUploadCoverFileDialog">
                                <span class="upload-icon">🖼️</span>
                                <span>点击选择封面图片</span>
                                <span class="upload-hint">支持 JPG、PNG、GIF 等图片格式</span>
                            </div>
                            <div v-else class="file-upload-selected">
                                <div class="file-info">
                                    <img :src="uploadCoverForm.preview" alt="封面预览" class="cover-preview-large" />
                                    <div class="file-details">
                                        <div class="file-name">{{ uploadCoverForm.file.name }}</div>
                                        <div class="file-size">{{ formatFileSize(uploadCoverForm.file.size) }}</div>
                                    </div>
                                </div>
                                <button class="btn small" @click="removeUploadCoverFile">移除</button>
                            </div>
                        </div>
                    </div>

                    <div class="form-error" v-if="uploadCoverError">{{ uploadCoverError }}</div>
                    <div class="modal-actions">
                        <button class="btn green-outline" @click="closeUploadCoverModal" :disabled="uploadingCover">取消</button>
                        <button class="btn green" @click="confirmUploadCover" :disabled="uploadingCover || !uploadCoverForm.file">
                            <span v-if="uploadingCover">上传中...</span>
                            <span v-else>上传封面</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 上传音频模态 -->
        <div v-if="uploadAudioModalOpen" class="modal-overlay" @click.self="closeUploadAudioModal">
            <div class="modal upload-audio-modal">
                <h3>上传音频文件</h3>
                <div class="upload-audio-form">
                    <div class="form-section">
                        <h4 class="form-section-title">歌曲信息</h4>
                        <div class="song-info-display">
                            <div class="info-item">
                                <span class="info-label">歌曲名称：</span>
                                <span class="info-value">{{ uploadAudioSong?.name || '未知' }}</span>
                            </div>
                            <div class="info-item">
                                <span class="info-label">歌手：</span>
                                <span class="info-value">{{ uploadAudioSong?.artist || '未知' }}</span>
                            </div>
                        </div>
                    </div>

                    <div class="form-section">
                        <h4 class="form-section-title">音频文件</h4>
                        <div class="file-upload-area">
                            <input ref="uploadAudioFileInput"
                                   type="file"
                                   accept="audio/*,.mp3,.wav"
                                   @change="handleUploadAudioFileSelect"
                                   class="sr-only"
                                   id="upload-audio-file-input" />
                            <div v-if="!uploadAudioForm.file" class="file-upload-placeholder" @click="openUploadAudioFileDialog">
                                <span class="upload-icon">📁</span>
                                <span>点击选择音频文件</span>
                                <span class="upload-hint">支持 MP3、WAV 格式</span>
                            </div>
                            <div v-else class="file-upload-selected">
                                <div class="file-info">
                                    <span class="file-icon">🎵</span>
                                    <div class="file-details">
                                        <div class="file-name">{{ uploadAudioForm.file.name }}</div>
                                        <div class="file-size">{{ formatFileSize(uploadAudioForm.file.size) }}</div>
                                    </div>
                                </div>
                                <button class="btn small" @click="removeUploadAudioFile">移除</button>
                            </div>
                        </div>
                    </div>

                    <div class="form-error" v-if="uploadAudioError">{{ uploadAudioError }}</div>
                    <div class="modal-actions">
                        <button class="btn green-outline" @click="closeUploadAudioModal" :disabled="uploadingAudio">取消</button>
                        <button class="btn green" @click="confirmUploadAudio" :disabled="uploadingAudio || !uploadAudioForm.file">
                            <span v-if="uploadingAudio">上传中...</span>
                            <span v-else>上传音频</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 添加歌曲模态 -->
        <div v-if="addTrackModalOpen" class="modal-overlay" @click.self="closeAddTrackModal">
            <div class="modal add-track-modal">
                <h3>添加歌曲</h3>
                <div class="add-track-form">
                    <div class="form-section">
                        <h4 class="form-section-title">歌曲信息</h4>
                        <label class="form-row">
                            <span class="form-label">歌曲名称 <span class="required">*</span></span>
                            <input ref="trackTitleInput"
                                   v-model="newTrackForm.title"
                                   placeholder="请输入歌曲名称"
                                   maxlength="100"
                                   @keydown.enter="confirmAddTrack" />
                            <div class="form-error" v-if="addTrackError.title">{{ addTrackError.title }}</div>
                        </label>
                        <label class="form-row">
                            <span class="form-label">歌手 <span class="required">*</span></span>
                            <input v-model="newTrackForm.artist"
                                   placeholder="请输入歌手名称"
                                   maxlength="50"
                                   @keydown.enter="confirmAddTrack" />
                            <div class="form-error" v-if="addTrackError.artist">{{ addTrackError.artist }}</div>
                        </label>
                        <label class="form-row">
                            <span class="form-label">专辑</span>
                            <input v-model="newTrackForm.album"
                                   placeholder="请输入专辑名称（可选）"
                                   maxlength="50" />
                        </label>
                    </div>

                    <div class="form-section">
                        <h4 class="form-section-title">音频文件</h4>
                        <div class="file-upload-area">
                            <input ref="trackFileInput"
                                   type="file"
                                   accept="audio/*,.mp3,.wav"
                                   @change="handleTrackFileSelect"
                                   class="sr-only"
                                   id="track-file-input" />
                            <div v-if="!newTrackForm.file" class="file-upload-placeholder" @click="openTrackFileDialog">
                                <span class="upload-icon">📁</span>
                                <span>点击选择音频文件（可选）</span>
                                <span class="upload-hint">支持 MP3、WAV 格式</span>
                            </div>
                            <div v-else class="file-upload-selected">
                                <div class="file-info">
                                    <span class="file-icon">🎵</span>
                                    <div class="file-details">
                                        <div class="file-name">{{ newTrackForm.file.name }}</div>
                                        <div class="file-size">{{ formatFileSize(newTrackForm.file.size) }}</div>
                                    </div>
                                </div>
                                <button class="btn small" @click="removeTrackFile">移除</button>
                            </div>
                        </div>
                    </div>

                    <div class="form-section">
                        <h4 class="form-section-title">封面图片（可选）</h4>
                        <div class="file-upload-area">
                            <input ref="trackCoverInput"
                                   type="file"
                                   accept="image/*"
                                   @change="handleTrackCoverSelect"
                                   class="sr-only"
                                   id="track-cover-input" />
                            <div v-if="!newTrackForm.coverFile" class="file-upload-placeholder" @click="openTrackCoverDialog">
                                <span class="upload-icon">🖼️</span>
                                <span>点击选择封面图片（可选）</span>
                            </div>
                            <div v-else class="file-upload-selected">
                                <div class="file-info">
                                    <img :src="newTrackForm.coverPreview" alt="封面预览" class="cover-preview" />
                                    <div class="file-details">
                                        <div class="file-name">{{ newTrackForm.coverFile.name }}</div>
                                    </div>
                                </div>
                                <button class="btn small" @click="removeTrackCover">移除</button>
                            </div>
                        </div>
                    </div>

                    <div class="form-error" v-if="addTrackError.general">{{ addTrackError.general }}</div>
                    <div class="modal-actions">
                        <button class="btn green-outline" @click="closeAddTrackModal" :disabled="addingTrack">取消</button>
                        <button class="btn green" @click="confirmAddTrack" :disabled="addingTrack">
                            <span v-if="addingTrack">添加中...</span>
                            <span v-else>添加歌曲</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 创建歌单模态 -->
        <div v-if="createPlaylistModalOpen" class="modal-overlay" @click.self="closeCreatePlaylistModal">
            <div class="modal create-playlist-modal">
                <h3>创建新歌单</h3>
                <div class="create-playlist-form">
                    <label class="form-row">
                        <span class="form-label">歌单名称 <span class="required">*</span></span>
                        <input ref="playlistNameInput"
                               v-model="newPlaylistForm.name"
                               placeholder="请输入歌单名称"
                               maxlength="50"
                               @keydown.enter="confirmCreatePlaylist" />
                        <div class="form-error" v-if="createPlaylistError.name">{{ createPlaylistError.name }}</div>
                    </label>
                    <label class="form-row">
                        <span class="form-label">歌单描述</span>
                        <textarea v-model="newPlaylistForm.desc"
                                  placeholder="请输入歌单描述（可选）"
                                  rows="3"
                                  maxlength="200"></textarea>
                    </label>
                    <div class="form-error" v-if="createPlaylistError.general">{{ createPlaylistError.general }}</div>
                    <div class="modal-actions">
                        <button class="btn green-outline" @click="closeCreatePlaylistModal" :disabled="creatingPlaylist">取消</button>
                        <button class="btn green" @click="confirmCreatePlaylist" :disabled="creatingPlaylist">
                            <span v-if="creatingPlaylist">创建中...</span>
                            <span v-else>创建</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>

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
import { useRouter } from 'vue-router'
import { api } from '../api.js'

// 主题切换
const isDarkMode = ref(localStorage.getItem('theme') !== 'light')
const toggleTheme = () => {
  isDarkMode.value = !isDarkMode.value
  localStorage.setItem('theme', isDarkMode.value ? 'dark' : 'light')
  updateTheme()
}
const updateTheme = () => {
  document.documentElement.setAttribute('data-theme', isDarkMode.value ? 'dark' : 'light')
}
onMounted(() => {
  updateTheme()
})

// 基本播放数据
const songList = ref([])
const currentIndex = ref(-1)

// 当前播放歌曲
const currentSong = computed(() => {
  if (currentIndex.value < 0 || currentIndex.value >= songList.value.length) {
    return null
  }
  return songList.value[currentIndex.value]
})

// 默认封面
const defaultCover = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='200' height='200' viewBox='0 0 24 24' fill='none' stroke='%2360a5fa' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Ccircle cx='12' cy='12' r='10'%3E%3C/circle%3E%3Cpath d='M9 9v6l5-3z'%3E%3C/path%3E%3C/svg%3E"

const isPlaying = ref(false)
const audio = ref(new Audio())
const audioDuration = ref(0)
const currentTime = ref(0)
const audioVolume = ref(0.7)
const isMuted = ref(false)
const prevVolume = ref(audioVolume.value)
// 音量滑块显示控制
const showVolSlider = ref(false)
// 歌词面板显示控制
const showDetail = ref(false)

// 切换歌词面板显示
const toggleDetail = () => {
  showDetail.value = !showDetail.value
}
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
  const data = await api.getPlaylists()
  if (data.code === 200) {
    playlists.value = data.data
  }
}

//初始化加载用户数据
const fetchFavTracks = async () => {
  // 注意：接口文档中没有专门的收藏列表接口，这里暂时保留原逻辑
  // 如果需要，可以通过获取所有歌单中的歌曲来获取收藏状态
}

onMounted(() => {
  audio.value.onloadedmetadata = () => {
    audioDuration.value = audio.value.duration
  }
  audio.value.ontimeupdate = () => {
    if (!audio.value.seeking) {
      currentTime.value = audio.value.currentTime
      updateActiveLrcIndex() // 更新当前歌词索引
    }
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
  
  // 初始化用户信息
  if (token.value) {
    fetchUserInfo().then(() => {
      // 用户信息加载完成后，会自动加载歌单（在 fetchUserInfo 中调用）
    }).catch(() => {
      // 如果获取用户信息失败，保持当前状态
    })
  }
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
  return { backgroundImage: 'linear-gradient(90deg,var(--bg-secondary),var(--bg-tertiary))' }
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
const currentUser = ref(null)
const token = ref(localStorage.getItem('token') || '')

// 统一登录弹窗打开方法
const openAuth = (mode) => {
  authMode.value = mode
  authForm.value = { username: '', email: '', password: '' }
  authError.value = ''
  authModalOpen.value = true
  // 打开后自动聚焦到邮箱输入
  nextTick(() => {
    try { authEmailInput.value && authEmailInput.value.focus() } catch (e) {}
  })
}
const closeAuth = () => {
  authModalOpen.value = false
}

const router = useRouter()  

const setToken = (t) => {
  token.value = t
  if (t) localStorage.setItem('token', t)
  else localStorage.removeItem('token')
}

// 登录成功 → 关闭登录弹窗 + 进入个人主页
const login = async () => {
  authError.value = ''
  if (!authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写邮箱与密码'; return 
  }
  try {
    const data = await api.login(authForm.value.email, authForm.value.password)
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false
      viewMode.value = 'profile'
      // 登录成功后加载用户数据
      await fetchPlaylists()
    } else {
      authError.value = data.msg || '登录失败'
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

// 注册成功 → 关闭登录弹窗 + 进入个人主页
const register = async () => {
  authError.value = ''
  if (!authForm.value.username || !authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写用户名、邮箱与密码'; return 
  }
  try {
    const data = await api.register(authForm.value.username, authForm.value.email, authForm.value.password)
    if (data && data.code === 200) {
      setToken(data.data.token)
      currentUser.value = data.data
      authModalOpen.value = false
      viewMode.value = 'profile'
      // 注册成功后加载用户数据
      await fetchPlaylists()
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
    const data = await api.getUserInfo()
    if (data && data.code === 200) {
      currentUser.value = data.data
      // 获取用户信息后加载歌单
      await fetchPlaylists()
      viewMode.value = 'profile'
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
    try { await api.logout() } catch (e) { console.error(e) }
  }
  setToken('')
  currentUser.value = null
  playlists.value = []
  viewMode.value = 'all'
  // 退出登录后跳转到首屏
  router.push('/')
}

// 视图控制
const viewMode = ref('all')
const showProfileModal = ref(false)
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

  // 如果没有登录，提示用户登录
  if (!token.value) {
    alert('请先登录后再上传歌曲')
    openAuth('login')
    return
  }

  // 如果没有选中歌单，提示用户选择或创建歌单
  if (!selectedPlaylistId.value) {
    const create = confirm('请先选择或创建一个歌单，是否现在创建？')
    if (create) {
      // 打开创建歌单模态，用户创建完成后会自动选中新歌单
      openCreatePlaylistModal()
      // 提示用户创建完成后可以继续上传
      alert('请先创建歌单，创建完成后可以继续上传歌曲')
      return
    } else {
      return
    }
  }

  // 上传文件到选中的歌单
  for (const file of files) {
    if (!['audio/mpeg', 'audio/wav', 'audio/mp3'].includes(file.type)) continue

    const title = file.name.replace(/\.(mp3|wav)$/i, '')
    const artist = '未知'

    try {
      // 使用接口11：向歌单添加歌曲
      const data = await api.addTrackToPlaylist({
        playlistId: selectedPlaylistId.value,
        title: title,
        artist: artist,
        file: file
      })
      
      if (data.code === 200) {
        // 如果上传了音频文件，data.data 会返回音频链接
        // 需要重新获取歌单中的歌曲列表来更新界面
        await loadPlaylistTracks(selectedPlaylistId.value)
      }
    } catch (err) {
      console.error('上传失败:', err)
      alert('上传失败: ' + (err.message || '未知错误'))
    }
  }
}

// 加载歌单中的歌曲列表
const loadPlaylistTracks = async (playlistId) => {
  try {
    const data = await api.getTracksByPlaylist(playlistId)
    if (data.code === 200 && Array.isArray(data.data)) {
      // 将歌曲添加到 songList，并更新歌单的歌曲列表
      const playlist = playlists.value.find(p => p.id === playlistId)
      if (playlist) {
        // 更新歌单中的歌曲索引
        const trackIndices = data.data.map(track => {
          // 检查歌曲是否已存在于 songList
          let songIndex = songList.value.findIndex(s => s.id === track.id)
          if (songIndex === -1) {
            // 添加新歌曲到 songList
            songList.value.push({
              id: track.id,
              name: track.title, // 接口返回的是 title
              artist: track.artist,
              album: track.album,
              url: track.filePath, // 接口返回的是 filePath
              duration: track.duration || 0,
              coverUrl: track.coverUrl,
              fav: false
            })
            songIndex = songList.value.length - 1
          } else {
            // 更新已存在的歌曲信息
            songList.value[songIndex] = {
              ...songList.value[songIndex],
              name: track.title,
              artist: track.artist,
              album: track.album,
              url: track.filePath,
              duration: track.duration || songList.value[songIndex].duration,
              coverUrl: track.coverUrl || songList.value[songIndex].coverUrl
            }
          }
          return songIndex
        })
        playlist.songs = trackIndices
      }
    }
  } catch (err) {
    console.error('加载歌单歌曲失败:', err)
  }
}

// 歌单相关
const playlists = ref([])
const playlistsOpen = ref(true) // 默认展开
const selectedPlaylistId = ref(null)
const editing = ref(false)
const editName = ref('')
const editDesc = ref('')
const manageModalOpen = ref(false)
const manageSelection = ref(new Set())
const deleteConfirmOpen = ref(false)
const deletingPlaylistId = ref(null)

// 修改歌单名称相关状态
const editPlaylistNameModalOpen = ref(false)
const editingPlaylistName = ref(false)
const editPlaylistNameForm = ref({ name: '' })
const editPlaylistNameError = ref({ name: '', general: '' })
const editPlaylistNameInput = ref(null)
const editingPlaylistId = ref(null)

// 创建歌单相关状态
const createPlaylistModalOpen = ref(false)
const creatingPlaylist = ref(false)
const newPlaylistForm = ref({ name: '', desc: '' })
const createPlaylistError = ref({ name: '', general: '' })
const playlistNameInput = ref(null)

// 打开创建歌单模态
const openCreatePlaylistModal = () => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  // 重置表单和错误
  newPlaylistForm.value = { name: '', desc: '' }
  createPlaylistError.value = { name: '', general: '' }
  createPlaylistModalOpen.value = true
  // 自动聚焦到名称输入框
  nextTick(() => {
    try { playlistNameInput.value && playlistNameInput.value.focus() } catch (e) {}
  })
}

// 关闭创建歌单模态
const closeCreatePlaylistModal = () => {
  if (creatingPlaylist.value) return // 创建中时不允许关闭
  createPlaylistModalOpen.value = false
  newPlaylistForm.value = { name: '', desc: '' }
  createPlaylistError.value = { name: '', general: '' }
}

// 确认创建歌单
const confirmCreatePlaylist = async () => {
  // 重置错误
  createPlaylistError.value = { name: '', general: '' }
  
  // 验证表单
  const name = newPlaylistForm.value.name.trim()
  if (!name) {
    createPlaylistError.value.name = '歌单名称不能为空'
    return
  }
  if (name.length > 50) {
    createPlaylistError.value.name = '歌单名称不能超过50个字符'
    return
  }
  
  // 检查名称是否重复
  if (playlists.value.some(p => p.name === name)) {
    createPlaylistError.value.name = '歌单名称已存在，请使用其他名称'
    return
  }

  creatingPlaylist.value = true

  try {
    // 使用接口7：创建新歌单
    const data = await api.createPlaylist({ 
      name: name, 
      desc: newPlaylistForm.value.desc.trim() || undefined,
      sort: 1, 
      status: true 
    })
    
    if (data.code === 200) {
      // 重新获取歌单列表
      await fetchPlaylists()
      // 找到新创建的歌单并选中
      const newPlaylist = playlists.value.find(p => p.name === name)
      if (newPlaylist) {
        selectPlaylist(newPlaylist.id)
      }
      // 关闭模态
      createPlaylistModalOpen.value = false
      newPlaylistForm.value = { name: '', desc: '' }
    } else {
      createPlaylistError.value.general = data.msg || '创建失败，请重试'
    }
  } catch (err) {
    console.error('创建歌单失败', err)
    createPlaylistError.value.general = err.message || '网络错误，请重试'
  } finally {
    creatingPlaylist.value = false
  }
}

// 保持向后兼容：createPlaylist 现在打开模态
const createPlaylist = openCreatePlaylistModal

// 添加歌曲相关状态
const addTrackModalOpen = ref(false)
const addingTrack = ref(false)
const newTrackForm = ref({ 
  title: '', 
  artist: '', 
  album: '', 
  file: null,
  coverFile: null,
  coverPreview: null
})
const addTrackError = ref({ title: '', artist: '', general: '' })
const trackFileInput = ref(null)
const trackCoverInput = ref(null)
const trackTitleInput = ref(null)

// 打开添加歌曲模态
const openAddTrackModal = () => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  if (!selectedPlaylistId.value) {
    alert('请先选择一个歌单')
    return
  }
  // 重置表单和错误
  newTrackForm.value = { 
    title: '', 
    artist: '', 
    album: '', 
    file: null,
    coverFile: null,
    coverPreview: null
  }
  addTrackError.value = { title: '', artist: '', general: '' }
  addTrackModalOpen.value = true
  // 自动聚焦到标题输入框
  nextTick(() => {
    try { trackTitleInput.value && trackTitleInput.value.focus() } catch (e) {}
  })
}

// 关闭添加歌曲模态
const closeAddTrackModal = () => {
  if (addingTrack.value) return // 添加中时不允许关闭
  // 清理预览URL（在重置之前）
  if (newTrackForm.value.coverPreview && newTrackForm.value.coverPreview.startsWith('blob:')) {
    try { URL.revokeObjectURL(newTrackForm.value.coverPreview) } catch (e) {}
  }
  addTrackModalOpen.value = false
  newTrackForm.value = { 
    title: '', 
    artist: '', 
    album: '', 
    file: null,
    coverFile: null,
    coverPreview: null
  }
  addTrackError.value = { title: '', artist: '', general: '' }
}

// 打开文件选择对话框
const openTrackFileDialog = () => {
  if (trackFileInput.value) {
    trackFileInput.value.value = ''
    trackFileInput.value.click()
  }
}

// 打开封面选择对话框
const openTrackCoverDialog = () => {
  if (trackCoverInput.value) {
    trackCoverInput.value.value = ''
    trackCoverInput.value.click()
  }
}

// 处理音频文件选择
const handleTrackFileSelect = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('audio/') && !file.name.match(/\.(mp3|wav)$/i)) {
    addTrackError.value.general = '请选择音频文件（MP3、WAV格式）'
    return
  }
  
  newTrackForm.value.file = file
  
  // 如果标题为空，尝试从文件名提取
  if (!newTrackForm.value.title.trim()) {
    newTrackForm.value.title = file.name.replace(/\.(mp3|wav)$/i, '')
  }
}

// 处理封面文件选择
const handleTrackCoverSelect = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    addTrackError.value.general = '请选择图片文件'
    return
  }
  
  newTrackForm.value.coverFile = file
  
  // 创建预览
  if (newTrackForm.value.coverPreview && newTrackForm.value.coverPreview.startsWith('blob:')) {
    try { URL.revokeObjectURL(newTrackForm.value.coverPreview) } catch (e) {}
  }
  newTrackForm.value.coverPreview = URL.createObjectURL(file)
}

// 移除音频文件
const removeTrackFile = () => {
  newTrackForm.value.file = null
}

// 移除封面
const removeTrackCover = () => {
  if (newTrackForm.value.coverPreview && newTrackForm.value.coverPreview.startsWith('blob:')) {
    try { URL.revokeObjectURL(newTrackForm.value.coverPreview) } catch (e) {}
  }
  newTrackForm.value.coverFile = null
  newTrackForm.value.coverPreview = null
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 上传音频相关状态
const uploadAudioModalOpen = ref(false)
const uploadingAudio = ref(false)
const uploadAudioForm = ref({ file: null })
const uploadAudioError = ref('')
const uploadAudioFileInput = ref(null)
const uploadAudioSongIndex = ref(null)
const uploadAudioSong = computed(() => {
  if (uploadAudioSongIndex.value === null || uploadAudioSongIndex.value === undefined) return null
  return songList.value[uploadAudioSongIndex.value] || null
})

// 打开上传音频模态
const openUploadAudioModal = (songIndex) => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  
  const song = songList.value[songIndex]
  if (!song || !song.id) {
    alert('歌曲信息不完整')
    return
  }
  
  uploadAudioSongIndex.value = songIndex
  uploadAudioForm.value = { file: null }
  uploadAudioError.value = ''
  uploadAudioModalOpen.value = true
}

// 关闭上传音频模态
const closeUploadAudioModal = () => {
  if (uploadingAudio.value) return // 上传中时不允许关闭
  uploadAudioModalOpen.value = false
  uploadAudioForm.value = { file: null }
  uploadAudioError.value = ''
  uploadAudioSongIndex.value = null
}

// 上传封面相关状态
const uploadCoverModalOpen = ref(false)
const uploadingCover = ref(false)
const uploadCoverForm = ref({ file: null, preview: null })
const uploadCoverError = ref('')
const uploadCoverFileInput = ref(null)
const uploadCoverSongIndex = ref(null)
const uploadCoverSong = computed(() => {
  if (uploadCoverSongIndex.value === null || uploadCoverSongIndex.value === undefined) return null
  return songList.value[uploadCoverSongIndex.value] || null
})

// 打开上传封面模态
const openUploadCoverModal = (songIndex) => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  
  const song = songList.value[songIndex]
  if (!song || !song.id) {
    alert('歌曲信息不完整')
    return
  }
  
  uploadCoverSongIndex.value = songIndex
  uploadCoverForm.value = { file: null, preview: null }
  uploadCoverError.value = ''
  uploadCoverModalOpen.value = true
}

// 关闭上传封面模态
const closeUploadCoverModal = () => {
  if (uploadingCover.value) return // 上传中时不允许关闭
  // 清理预览URL
  if (uploadCoverForm.value.preview && uploadCoverForm.value.preview.startsWith('blob:')) {
    try { URL.revokeObjectURL(uploadCoverForm.value.preview) } catch (e) {}
  }
  uploadCoverModalOpen.value = false
  uploadCoverForm.value = { file: null, preview: null }
  uploadCoverError.value = ''
  uploadCoverSongIndex.value = null
}

// 打开封面文件选择对话框
const openUploadCoverFileDialog = () => {
  if (uploadCoverFileInput.value) {
    uploadCoverFileInput.value.value = ''
    uploadCoverFileInput.value.click()
  }
}

// 处理封面文件选择
const handleUploadCoverFileSelect = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    uploadCoverError.value = '请选择图片文件'
    return
  }
  
  uploadCoverForm.value.file = file
  
  // 创建预览
  if (uploadCoverForm.value.preview && uploadCoverForm.value.preview.startsWith('blob:')) {
    try { URL.revokeObjectURL(uploadCoverForm.value.preview) } catch (e) {}
  }
  uploadCoverForm.value.preview = URL.createObjectURL(file)
  uploadCoverError.value = ''
}

// 移除封面文件
const removeUploadCoverFile = () => {
  if (uploadCoverForm.value.preview && uploadCoverForm.value.preview.startsWith('blob:')) {
    try { URL.revokeObjectURL(uploadCoverForm.value.preview) } catch (e) {}
  }
  uploadCoverForm.value.file = null
  uploadCoverForm.value.preview = null
}

// 确认上传封面
const confirmUploadCover = async () => {
  if (!uploadCoverForm.value.file) {
    uploadCoverError.value = '请选择封面图片'
    return
  }
  
  const song = uploadCoverSong.value
  if (!song || !song.id) {
    uploadCoverError.value = '歌曲信息不完整'
    return
  }

  uploadingCover.value = true
  uploadCoverError.value = ''
  
  try {
    // 使用接口14：上传歌曲封面
    const data = await api.uploadTrackCover(song.id, uploadCoverForm.value.file)
    
    if (data.code === 200) {
      // 更新歌曲的封面URL
      if (song) {
        song.coverUrl = data.data // 接口返回封面链接
      }
      
      // 重新加载歌单歌曲列表（如果在歌单视图中）
      if (viewMode.value === 'playlist' && selectedPlaylistId.value) {
        await loadPlaylistTracks(selectedPlaylistId.value)
        // 重新加载后，确保封面URL已更新
        const updatedSong = songList.value.find(s => s.id === song.id)
        if (updatedSong && !updatedSong.coverUrl && data.data) {
          updatedSong.coverUrl = data.data
        }
      }
      
      // 关闭模态
      closeUploadCoverModal()
    } else {
      uploadCoverError.value = data.msg || '上传失败，请重试'
    }
  } catch (err) {
    console.error('上传封面失败', err)
    uploadCoverError.value = err.message || '网络错误，请重试'
  } finally {
    uploadingCover.value = false
  }
}

// 打开文件选择对话框
const openUploadAudioFileDialog = () => {
  if (uploadAudioFileInput.value) {
    uploadAudioFileInput.value.value = ''
    uploadAudioFileInput.value.click()
  }
}

// 处理音频文件选择
const handleUploadAudioFileSelect = (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  
  // 验证文件类型
  if (!file.type.startsWith('audio/') && !file.name.match(/\.(mp3|wav)$/i)) {
    uploadAudioError.value = '请选择音频文件（MP3、WAV格式）'
    return
  }
  
  uploadAudioForm.value.file = file
  uploadAudioError.value = ''
}

// 移除音频文件
const removeUploadAudioFile = () => {
  uploadAudioForm.value.file = null
}

// 确认上传音频
const confirmUploadAudio = async () => {
  if (!uploadAudioForm.value.file) {
    uploadAudioError.value = '请选择音频文件'
    return
  }
  
  const song = uploadAudioSong.value
  if (!song || !song.id) {
    uploadAudioError.value = '歌曲信息不完整'
    return
  }

  uploadingAudio.value = true
  uploadAudioError.value = ''
  
  try {
    // 使用接口15：上传歌曲音频
    const data = await api.uploadTrackAudio(song.id, uploadAudioForm.value.file)
    
    if (data.code === 200) {
      // 更新歌曲的音频URL（立即更新，提供即时反馈）
      if (song) {
        song.url = data.data // 接口返回音频链接
        // 如果当前正在播放这首歌曲，更新音频源
        if (currentIndex.value === uploadAudioSongIndex.value) {
          audio.value.src = data.data
          // 如果正在播放，重新加载元数据
          audio.value.load()
        }
      }
      
      // 重新加载歌单歌曲列表（如果在歌单视图中），确保数据同步
      if (viewMode.value === 'playlist' && selectedPlaylistId.value) {
        await loadPlaylistTracks(selectedPlaylistId.value)
        // 重新加载后，确保URL已更新（因为loadPlaylistTracks会从后端获取最新数据）
        const updatedSong = songList.value.find(s => s.id === song.id)
        if (updatedSong && !updatedSong.url && data.data) {
          updatedSong.url = data.data
        }
      }
      
      // 关闭模态
      closeUploadAudioModal()
    } else {
      uploadAudioError.value = data.msg || '上传失败，请重试'
    }
  } catch (err) {
    console.error('上传音频失败', err)
    uploadAudioError.value = err.message || '网络错误，请重试'
  } finally {
    uploadingAudio.value = false
  }
}

// 确认添加歌曲
const confirmAddTrack = async () => {
  // 重置错误
  addTrackError.value = { title: '', artist: '', general: '' }
  
  // 验证表单
  const title = newTrackForm.value.title.trim()
  const artist = newTrackForm.value.artist.trim()
  
  if (!title) {
    addTrackError.value.title = '歌曲名称不能为空'
    return
  }
  if (title.length > 100) {
    addTrackError.value.title = '歌曲名称不能超过100个字符'
    return
  }
  
  if (!artist) {
    addTrackError.value.artist = '歌手名称不能为空'
    return
  }
  if (artist.length > 50) {
    addTrackError.value.artist = '歌手名称不能超过50个字符'
    return
  }
  
  // 如果没有选择文件，至少需要标题和歌手
  if (!newTrackForm.value.file && !title && !artist) {
    addTrackError.value.general = '请至少填写歌曲名称和歌手，或上传音频文件'
    return
  }

  addingTrack.value = true
  
  try {
    // 准备上传数据
    const uploadData = {
      playlistId: selectedPlaylistId.value,
      title: title,
      artist: artist
    }
    
    // 可选字段
    if (newTrackForm.value.album.trim()) {
      uploadData.album = newTrackForm.value.album.trim()
    }
    
    if (newTrackForm.value.file) {
      uploadData.file = newTrackForm.value.file
    }
    
    // 注意：接口文档中coverUrl是URL字符串，不是文件
    // 如果需要上传封面文件，可能需要先上传获取URL，或者后端支持直接上传文件
    // 这里暂时只支持URL，如果需要上传文件，需要额外的接口
    
    // 使用接口11：向歌单添加歌曲
    const data = await api.addTrackToPlaylist(uploadData)
    
    if (data.code === 200) {
      // 重新加载歌单歌曲列表
      await loadPlaylistTracks(selectedPlaylistId.value)
      
      // 如果用户上传了封面文件，尝试上传封面
      if (newTrackForm.value.coverFile) {
        // 从刚加载的歌曲列表中找到刚添加的歌曲（通过标题和歌手匹配）
        const playlist = playlists.value.find(p => p.id === selectedPlaylistId.value)
        if (playlist && playlist.songs && playlist.songs.length > 0) {
          // 获取最后添加的歌曲（假设是按顺序添加的）
          const lastSongIndex = playlist.songs[playlist.songs.length - 1]
          const lastSong = songList.value[lastSongIndex]
          
          // 如果标题和歌手匹配，说明是刚添加的歌曲
          if (lastSong && lastSong.name === title && lastSong.artist === artist && lastSong.id) {
            try {
              // 使用接口14：上传歌曲封面
              const coverData = await api.uploadTrackCover(lastSong.id, newTrackForm.value.coverFile)
              if (coverData.code === 200) {
                // 更新歌曲的封面URL
                lastSong.coverUrl = coverData.data
                // 重新加载歌单以更新封面
                await loadPlaylistTracks(selectedPlaylistId.value)
              }
            } catch (coverErr) {
              console.error('上传封面失败', coverErr)
              // 封面上传失败不影响主流程，只记录错误
            }
          }
        }
      }
      
      // 关闭模态
      closeAddTrackModal()
    } else {
      addTrackError.value.general = data.msg || '添加失败，请重试'
    }
  } catch (err) {
    console.error('添加歌曲失败', err)
    addTrackError.value.general = err.message || '网络错误，请重试'
  } finally {
    addingTrack.value = false
  }
}

const selectPlaylist = async (id) => {
  selectedPlaylistId.value = id
  viewMode.value = 'playlist'
  const pl = playlists.value.find(p => p.id === id)
  if (pl) {
    editName.value = pl.name
    editDesc.value = pl.desc || ''
    // 加载歌单中的歌曲
    await loadPlaylistTracks(id)
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

// 打开删除歌单确认对话框
const openDeletePlaylistConfirm = (playlistId) => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  deletingPlaylistId.value = playlistId
  deleteConfirmOpen.value = true
}

const confirmDeletePlaylist = async () => {
  const playlistId = deletingPlaylistId.value || selectedPlaylist.value?.id
  if (!playlistId) {
    deleteConfirmOpen.value = false
    return
  }
  
  try {
    // 使用接口9：删除歌单
    const data = await api.deletePlaylist(playlistId)
    if (data.code === 200) {
      // 从列表移除
      playlists.value = playlists.value.filter(p => p.id !== playlistId)
      // 如果删除的是当前选中的歌单，切换到其他视图
      if (selectedPlaylistId.value === playlistId) {
        viewMode.value = 'all'
        selectedPlaylistId.value = null
      }
    } else {
      alert(data.msg || '删除失败')
    }
  } catch (err) {
    console.error('删除失败', err)
    alert('删除失败: ' + (err.message || '未知错误'))
  }
  deleteConfirmOpen.value = false
  deletingPlaylistId.value = null
}

// 单曲删除相关
const openSongDeleteConfirm = (idx) => {
  songDeleteIndex.value = idx
  songDeleteConfirmOpen.value = true
}
const confirmDeleteSong = async () => {
  const idx = songDeleteIndex.value
  if (idx === null || idx === undefined) {
    songDeleteConfirmOpen.value = false
    return
  }
  
  const song = songList.value[idx]
  if (!song || !song.id) {
    songDeleteConfirmOpen.value = false
    return
  }

  // 如果当前在歌单视图中，从该歌单中移除歌曲
  if (viewMode.value === 'playlist' && selectedPlaylist.value) {
    try {
      // 使用接口12：从歌单中移除歌曲
      const data = await api.removeTrackFromPlaylist(selectedPlaylist.value.id, song.id)
      if (data.code === 200) {
        // 从歌单的歌曲列表中移除
        if (selectedPlaylist.value.songs) {
          selectedPlaylist.value.songs = selectedPlaylist.value.songs.filter(i => i !== idx)
        }
        // 如果正在播放被删除的歌曲，停止播放
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
        // 重新加载歌单歌曲列表
        await loadPlaylistTracks(selectedPlaylist.value.id)
      } else {
        alert(data.msg || '删除失败')
      }
    } catch (err) {
      console.error('删除失败', err)
      alert('删除失败: ' + (err.message || '未知错误'))
    }
  } else {
    // 如果不在歌单视图中，只从前端列表中移除（不调用后端）
    // 注意：接口文档中没有删除歌曲本身的接口，只有从歌单中移除的接口
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
  }
  
  songDeleteConfirmOpen.value = false
  songDeleteIndex.value = null
}

const toggleEditContent = async () => {
  if (!selectedPlaylist.value) return
  if (editing.value) {
    // 保存修改
    try {
      const data = await api.updatePlaylist({
        id: selectedPlaylist.value.id,
        name: editName.value || selectedPlaylist.value.name,
        sort: selectedPlaylist.value.sort,
        status: selectedPlaylist.value.status
      })
      if (data.code === 200) {
        selectedPlaylist.value.name = editName.value || selectedPlaylist.value.name
        editing.value = false
        // 重新获取歌单列表以确保数据同步
        await fetchPlaylists()
      } else {
        alert(data.msg || '保存失败')
      }
    } catch (err) {
      console.error('保存失败', err)
      alert('保存失败: ' + (err.message || '未知错误'))
    }
  } else {
    editing.value = true
    editName.value = selectedPlaylist.value.name
    editDesc.value = selectedPlaylist.value.desc || ''
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
    // 获取选中的歌曲ID列表
    const selectedIndices = Array.from(manageSelection.value)
    const trackIds = selectedIndices.map(i => songList.value[i]?.id).filter(id => id)
    
    try {
      // 使用接口13：重新排序歌单内歌曲
      const data = await api.reorderTracks(selectedPlaylist.value.id, trackIds)
      if (data.code === 200) {
        // 更新前端歌单的歌曲列表
        selectedPlaylist.value.songs = selectedIndices
        // 重新加载歌单歌曲以确保顺序正确
        await loadPlaylistTracks(selectedPlaylist.value.id)
      } else {
        alert(data.msg || '更新失败')
      }
    } catch (err) {
      console.error('更新歌单失败', err)
      alert('更新失败: ' + (err.message || '未知错误'))
    }
  }
  manageModalOpen.value = false
}

const closeManageSongs = () => { manageModalOpen.value = false }

// 打开修改歌单名称模态
const openEditPlaylistNameModal = (playlistId) => {
  if (!token.value) {
    alert('请先登录')
    openAuth('login')
    return
  }
  
  const playlist = playlists.value.find(p => p.id === playlistId)
  if (!playlist) {
    alert('歌单不存在')
    return
  }
  
  editingPlaylistId.value = playlistId
  editPlaylistNameForm.value = { name: playlist.name }
  editPlaylistNameError.value = { name: '', general: '' }
  editPlaylistNameModalOpen.value = true
  
  // 自动聚焦到输入框
  nextTick(() => {
    try { editPlaylistNameInput.value && editPlaylistNameInput.value.focus() } catch (e) {}
  })
}

// 关闭修改歌单名称模态
const closeEditPlaylistNameModal = () => {
  if (editingPlaylistName.value) return // 保存中时不允许关闭
  editPlaylistNameModalOpen.value = false
  editPlaylistNameForm.value = { name: '' }
  editPlaylistNameError.value = { name: '', general: '' }
  editingPlaylistId.value = null
}

// 确认修改歌单名称
const confirmEditPlaylistName = async () => {
  // 重置错误
  editPlaylistNameError.value = { name: '', general: '' }
  
  // 验证表单
  const name = editPlaylistNameForm.value.name.trim()
  if (!name) {
    editPlaylistNameError.value.name = '歌单名称不能为空'
    return
  }
  if (name.length > 50) {
    editPlaylistNameError.value.name = '歌单名称不能超过50个字符'
    return
  }
  
  // 检查名称是否重复（排除当前歌单）
  const playlist = playlists.value.find(p => p.id === editingPlaylistId.value)
  if (!playlist) {
    editPlaylistNameError.value.general = '歌单不存在'
    return
  }
  
  if (playlists.value.some(p => p.name === name && p.id !== editingPlaylistId.value)) {
    editPlaylistNameError.value.name = '歌单名称已存在，请使用其他名称'
    return
  }

  editingPlaylistName.value = true
  
  try {
    // 使用接口8：修改歌单信息
    const data = await api.updatePlaylist({ 
      id: editingPlaylistId.value,
      name: name, 
      sort: playlist.sort, 
      status: playlist.status 
    })
    
    if (data.code === 200) {
      // 更新本地歌单名称
      if (playlist) {
        playlist.name = name
      }
      // 如果当前选中的是这个歌单，更新显示
      if (selectedPlaylistId.value === editingPlaylistId.value) {
        editName.value = name
      }
      // 重新获取歌单列表以确保数据同步
      await fetchPlaylists()
      // 关闭模态
      editPlaylistNameModalOpen.value = false
      editPlaylistNameForm.value = { name: '' }
    } else {
      editPlaylistNameError.value.general = data.msg || '保存失败，请重试'
    }
  } catch (err) {
    console.error('修改歌单名称失败', err)
    editPlaylistNameError.value.general = err.message || '网络错误，请重试'
  } finally {
    editingPlaylistName.value = false
  }
}

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

// 歌词解析相关
const parsedLrc = ref([])
const activeLrcIndex = ref(-1)
const lrcList = ref(null)

// 解析LRC格式歌词
const parseLrc = (lrcText) => {
  if (!lrcText) return []
  
  const lrcLines = lrcText.split('\n')
  const lrcArray = []
  
  // 支持两种时间格式：[mm:ss.xx] 和 [mm:ss.xxx]
  const timeRegex = /\[(\d{2}):(\d{2})\.(\d{2,3})\]/g
  
  lrcLines.forEach(line => {
    const timeMatches = [...line.matchAll(timeRegex)]
    if (timeMatches.length === 0) return
    
    // 提取歌词文本
    const text = line.replace(timeRegex, '').trim()
    if (!text) return
    
    // 提取所有时间标签
    timeMatches.forEach(match => {
      const minutes = parseInt(match[1])
      const seconds = parseInt(match[2])
      const milliseconds = parseInt(match[3]) * (match[3].length === 2 ? 10 : 1) // 处理两位数和三位数的毫秒
      const totalSeconds = minutes * 60 + seconds + milliseconds / 1000
      
      lrcArray.push({
        time: totalSeconds,
        text: text
      })
    })
  })
  
  // 按时间排序
  return lrcArray.sort((a, b) => a.time - b.time)
}

// 获取歌曲歌词
const fetchLyrics = async (songId) => {
  if (!songId) {
    parsedLrc.value = []
    return
  }
  
  try {
    const data = await api.getLyrics(songId)
    if (data.code === 200) {
      parsedLrc.value = parseLrc(data.data)
    } else {
      console.warn('获取歌词失败:', data.msg)
      parsedLrc.value = []
    }
  } catch (err) {
    console.error('获取歌词网络错误:', err)
    parsedLrc.value = []
  }
}

// 歌词滚动定位
const scrollToActiveLyric = () => {
  if (!lrcList.value || activeLrcIndex.value === -1) return
  
  const activeLine = lrcList.value.children[activeLrcIndex.value]
  if (!activeLine) return
  
  const containerHeight = lrcList.value.clientHeight
  const lineHeight = activeLine.clientHeight
  const scrollTop = activeLine.offsetTop - containerHeight / 2 + lineHeight / 2
  
  lrcList.value.scrollTo({
    top: scrollTop,
    behavior: 'smooth'
  })
}

// 更新当前歌词索引
const updateActiveLrcIndex = () => {
  if (!parsedLrc.value.length) {
    activeLrcIndex.value = -1
    return
  }
  
  const currentTime = audio.value.currentTime
  for (let i = parsedLrc.value.length - 1; i >= 0; i--) {
    if (currentTime >= parsedLrc.value[i].time) {
      if (activeLrcIndex.value !== i) {
        activeLrcIndex.value = i
        scrollToActiveLyric()
      }
      break
    }
  }
}

// 播放控制相关
const playSong = (i) => {
  if (!songList.value.length || i < 0 || i >= songList.value.length) return
  const song = songList.value[i]
  if (!song || !song.url || song.url === '') {
    console.warn('歌曲没有音频URL，无法播放')
    return
  }
  currentIndex.value = i
  audio.value.src = song.url
  currentTime.value = 0
  audioDuration.value = 0
  
  // 加载歌词
  fetchLyrics(song.id)
  
  audio.value.play().then(() => isPlaying.value = true).catch((err) => {
    isPlaying.value = false
    console.error('播放失败:', err)
    alert('播放失败: ' + (err.message || '未知错误'))
  })
}

// 处理播放按钮点击
const handlePlayButtonClick = (i) => {
  // 如果点击的是当前正在播放的歌曲，则暂停/继续
  if (currentIndex.value === i) {
    togglePlay()
  } else {
    // 否则切换播放新歌曲
    playSong(i)
  }
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

  // 注意：接口文档中没有专门的收藏/取消收藏接口
  // 这里暂时保留前端状态切换，实际项目中可能需要后端支持
  // 或者通过其他方式实现（如使用"我喜欢的"歌单）
  song.fav = !song.fav
  
  // 如果已登录，可以考虑将收藏的歌曲添加到"我喜欢的"歌单
  // 这里暂时只做前端状态切换
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
    // 使用接口5：修改用户信息
    const data = await api.updateProfile({ username, email, gender })
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

// 新增：上传文件到后端（已废弃，头像上传直接使用 updateProfile）

// 修改 handleAvatarUpload
const handleAvatarUpload = async (e) => {
  const f = e.target.files?.[0]
  if (!f || !f.type.startsWith('image/')) return

  try {
    // 先创建临时预览
    const previewUrl = URL.createObjectURL(f)
    currentUser.value.avatar = previewUrl
    
    // 注意：接口文档中没有专门的头像上传接口
    // 这里需要先上传文件获取URL，然后通过 updateProfile 更新
    // 由于接口文档中没有通用文件上传接口，这里暂时只做前端预览
    // 实际项目中需要后端提供文件上传接口，或者使用第三方存储服务
    
    // 临时方案：提示用户功能暂未实现
    alert('头像上传功能需要后端支持文件上传接口，当前仅支持预览')
    
    // 如果需要完整实现，可以这样：
    // 1. 先上传文件到服务器获取URL（需要后端提供上传接口）
    // 2. 使用 api.updateProfile({ avatar: url }) 更新用户信息
  } catch (err) {
    console.error(err)
    alert('头像上传失败')
  }
}


</script>