import { getUserByToken } from '../data.js'

export const ensureAuth = (req, res, next) => {
  const auth = req.headers['authorization'] || ''
  if (!auth || !auth.startsWith('Bearer ')) {
    return res.status(401).json({ code: 401, msg: 'Unauthorized', data: null })
  }
  const token = auth.slice(7)
  if (!token) return res.status(401).json({ code: 401, msg: 'Unauthorized', data: null })
  const user = getUserByToken(token)
  if (!user) return res.status(401).json({ code: 401, msg: 'Invalid token', data: null })
  // attach user and token
  req.user = user
  req.token = token
  next()
}