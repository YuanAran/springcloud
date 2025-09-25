import axios from 'axios'
import { getToken } from './auth'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 后端服务器地址
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 自动添加token到请求头
request.interceptors.request.use(
  config => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理响应
request.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 统一错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // token过期或无效，跳转到登录页
          console.log('登录已过期，请重新登录')
          break
        case 403:
          console.log('没有权限访问')
          break
        case 500:
          console.log('服务器错误')
          break
        default:
          console.log('请求失败：' + error.response.data.message)
      }
    } else {
      console.log('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
