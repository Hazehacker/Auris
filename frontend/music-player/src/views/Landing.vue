<template>
  <div class="landing-overlay">
    <!-- Floating Lines 动态背景 -->
    <FloatingLines 
      :enabled-waves="['top', 'middle', 'bottom']"
      :line-count="[5, 5, 5]"
      :line-distance="[8, 6, 4]"
      :bend-radius="5.0"
      :bend-strength="-0.5"
      :interactive="true"
      :parallax="true"
    />
    
    <!-- 顶部导航栏 -->
    <nav class="landing-nav">
      <div class="nav-brand">
        <span class="nav-logo-icon">V</span>
        <span class="nav-logo-text">Auris</span>
      </div>
      <div class="nav-links">
        <a href="#" class="nav-link" @click.prevent>首页</a>
        <a href="#" class="nav-link" @click.prevent>文档</a>
      </div>
    </nav>

    <!-- 主内容区 -->
    <div class="landing-container">
      <div class="landing-brand">
        <h1 class="landing-title">Welcome to Auris!</h1>
            <h2>Immersive music experience<br/>
            your personal soundtrack</h2>
      </div>
      <div class="landing-actions">
        <button class="btn btn-white" @click="openAuth('login')">
          登录/注册
        </button>
        <button class="btn btn-purple" @click="enterGuest">
          游客模式
        </button>
      </div>
    </div>

    <!-- 登录 / 注册 模态 -->
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
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '../api.js'
import FloatingLines from '../components/FloatingLines.vue'

const router = useRouter()

// 身份认证相关状态
const authModalOpen = ref(false)
const authMode = ref('login')
const authForm = ref({ username: '', email: '', password: '' })
const authError = ref('')
const authEmailInput = ref(null)
const token = ref(localStorage.getItem('token') || '')

// 打开登录弹窗
const openAuth = (mode) => {
  authMode.value = mode
  authForm.value = { username: '', email: '', password: '' }
  authError.value = ''
  authModalOpen.value = true
  nextTick(() => {
    try { authEmailInput.value && authEmailInput.value.focus() } catch (e) {}
  })
}

const closeAuth = () => {
  authModalOpen.value = false
}

const setToken = (t) => {
  token.value = t
  if (t) localStorage.setItem('token', t)
  else localStorage.removeItem('token')
}

// 登录成功 → 跳转到主页
const login = async () => {
  authError.value = ''
  if (!authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写邮箱与密码'
    return 
  }
  try {
    const data = await api.login(authForm.value.email, authForm.value.password)
    if (data && data.code === 200) {
      setToken(data.data.token)
      authModalOpen.value = false
      // 跳转到主页
      router.push('/index')
    } else {
      authError.value = data.msg || '登录失败'
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

// 注册成功 → 跳转到主页
const register = async () => {
  authError.value = ''
  if (!authForm.value.username || !authForm.value.email || !authForm.value.password) { 
    authError.value = '请填写用户名、邮箱与密码'
    return 
  }
  try {
    const data = await api.register(authForm.value.username, authForm.value.email, authForm.value.password)
    if (data && data.code === 200) {
      setToken(data.data.token)
      authModalOpen.value = false
      // 跳转到主页
      router.push('/index')
    } else {
      authError.value = data.msg || '注册失败'
    }
  } catch (e) {
    console.error(e)
    authError.value = '网络请求失败'
  }
}

// 游客模式：跳转到主页
const enterGuest = () => {
  router.push('/index')
}
</script>

<style scoped>
.landing-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: #000;
  z-index: 9999;
  color: #fff;
  overflow: hidden;
}

/* 顶部导航栏 */
.landing-nav {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  z-index: 10;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  padding: 8px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  font-weight: bold;
  font-size: 18px;
  color: #fff;
}

.nav-logo-text {
  font-weight: 600;
  font-size: 16px;
  color: #fff;
}

.nav-links {
  display: flex;
  gap: 8px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.nav-link {
  color: #fff;
  text-decoration: none;
  padding: 6px 16px;
  font-size: 14px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
}

/* 主内容区 */
.landing-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  text-align: center;
  padding: 0 20px;
  padding-top: 80px;
}

.landing-brand {
  margin-bottom: 48px;
}

.landing-title {
  font-size: 3.5rem;
  font-weight: 700;
  line-height: 1.2;
  color: #fff;
  margin: 0;
  text-shadow: 0 2px 20px rgba(0, 0, 0, 0.5);
  letter-spacing: -0.02em;
}

.landing-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 14px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  cursor: pointer;
  border: none;
  transition: all 0.2s ease;
  font-family: inherit;
}

.btn-white {
  background: #fff;
  color: #000;
}

.btn-white:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 255, 255, 0.2);
}

.btn-purple {
  background: #9333ea;
  color: #fff;
}

.btn-purple:hover {
  background: #7e22ce;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(147, 51, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .landing-title {
    font-size: 2.5rem;
  }
  
  .landing-actions {
    flex-direction: column;
    width: 100%;
    max-width: 300px;
  }
  
  .btn {
    width: 100%;
  }
  
  .nav-links {
    gap: 4px;
    padding: 8px;
  }
  
  .nav-link {
    padding: 6px 12px;
    font-size: 13px;
  }
}
</style>
