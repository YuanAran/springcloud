// Token管理工具
const TOKEN_KEY = 'user_token'
const USER_ID_KEY = 'user_id'

// 保存token到localStorage
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

// 从localStorage获取token
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

// 删除token
export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

// 保存userId到localStorage
export function setUserId(userId) {
  if (userId === undefined || userId === null) return
  localStorage.setItem(USER_ID_KEY, String(userId))
}

// 获取userId
export function getUserId() {
  return localStorage.getItem(USER_ID_KEY)
}

// 删除userId
export function removeUserId() {
  localStorage.removeItem(USER_ID_KEY)
}

// 检查是否已登录（是否有token）
export function isLoggedIn() {
  return !!getToken()
}

// 全局token对象，可以在其他页面直接使用
export const Auth = {
  setToken,
  getToken,
  removeToken,
  isLoggedIn,

  setUserId,
  getUserId,
  removeUserId,

  // 获取当前token（响应式）
  get token() {
    return getToken()
  },

  // 获取当前用户ID（响应式）
  get userId() {
    return getUserId()
  }
}
