import { getUserByToken } from '../data.js'

export const ensureAuth = (req, res, next) => {
  // 支持标准的 authorization 和自定义的 authentication header
  // 直接使用header值作为token，不使用Bearer前缀
  const token = req.headers['authorization'] || req.headers['authentication'] || ''
  if (!token) {
    return res.status(401).json({ code: 401, msg: 'Unauthorized', data: null })
  }
  const user = getUserByToken(token)
  if (!user) return res.status(401).json({ code: 401, msg: 'Invalid token', data: null })
  // attach user and token
  req.user = user
  req.token = token
  next()
}