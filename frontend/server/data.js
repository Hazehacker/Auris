// In-memory mock data store for users and tokens
export const users = new Map()
export const tokens = new Map()

export const findUserByEmail = (email) => {
  for (const u of users.values()) {
    if (u.email === email) return u
  }
  return null
}

export const createUser = ({ username, email, password }) => {
  const id = Date.now().toString() + Math.floor(Math.random() * 1000)
  const user = { id, username, email, password, avatar: null }
  users.set(id, user)
  return user
}

export const createTokenForUser = (userId) => {
  const token = 'tk_' + Date.now().toString(36) + Math.random().toString(36).slice(2,8)
  tokens.set(token, userId)
  return token
}

export const getUserByToken = (token) => {
  const userId = tokens.get(token)
  if (!userId) return null
  return users.get(userId) || null
}

export const deleteToken = (token) => {
  return tokens.delete(token)
}
