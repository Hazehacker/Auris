import express from 'express'
import { findUserByEmail, createUser, createTokenForUser, getUserByToken, deleteToken } from '../data.js'
import { ensureAuth } from '../middleware/auth.js'

const router = express.Router()

// POST /user/register
router.post('/register', (req, res) => {
  const { username, email, password } = req.body
  if (!username || !email || !password) return res.status(400).json({ code: 400, msg: '请填写用户名、邮箱与密码', data: null })
  const exists = findUserByEmail(email)
  if (exists) return res.status(400).json({ code: 400, msg: '邮箱已被注册', data: null })
  const user = createUser({ username, email, password })
  const token = createTokenForUser(user.id)
  const out = { ...user }
  delete out.password
  out.token = token
  return res.json({ code: 200, msg: '', data: out })
})

// POST /user/login
router.post('/login', (req, res) => {
  const { email, password } = req.body
  if (!email || !password) return res.status(400).json({ code: 400, msg: '请填写邮箱与密码', data: null })
  const user = findUserByEmail(email)
  if (!user || user.password !== password) return res.status(401).json({ code: 401, msg: '邮箱或密码错误', data: null })
  const token = createTokenForUser(user.id)
  const out = { ...user }
  delete out.password
  out.token = token
  return res.json({ code: 200, msg: '', data: out })
})

// GET /user/userinfo
router.get('/userinfo', ensureAuth, (req, res) => {
  const user = req.user
  const out = { ...user }
  delete out.password
  return res.json({ code: 200, msg: '', data: out })
})

// POST /user/logout
router.post('/logout', ensureAuth, (req, res) => {
  const token = req.token
  deleteToken(token)
  return res.json({ code: 200, msg: '', data: null })
})

export default router