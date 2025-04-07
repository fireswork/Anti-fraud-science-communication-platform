// 用户信息存储的key
const USER_KEY = 'userInfo'

// 获取用户信息
export function getUserInfo() {
  const userInfoStr = localStorage.getItem(USER_KEY)
  return userInfoStr ? JSON.parse(userInfoStr) : null
}

// 设置用户信息
export function setUserInfo(userInfo) {
  if (userInfo) {
    localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
  }
}

// 清除用户信息
export function removeUserInfo() {
  localStorage.removeItem(USER_KEY)
}

// 获取用户ID
export function getUserId() {
  const userInfo = getUserInfo()
  return userInfo ? userInfo.userId : null
}

// 获取用户角色
export function getUserRole() {
  const userInfo = getUserInfo()
  return userInfo ? userInfo.role : null
}

// 检查用户是否已登录
export function isLoggedIn() {
  return !!getUserInfo()
}

// 检查用户是否是管理员
export function isAdmin() {
  return getUserRole() === 'ADMIN'
} 