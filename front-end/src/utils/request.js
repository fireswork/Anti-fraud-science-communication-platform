import axios from 'axios'
import { message } from 'ant-design-vue'

// 创建axios实例
const service = axios.create({
  baseURL: '/api', // 使用vite.config.js中配置的代理前缀
  timeout: 10000  // 请求超时时间
})

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 检查返回状态码
    if (res.code !== 200) {
      message.error(res.message || '请求失败');
      
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    console.log('请求错误: ' + error)
    message.error(error.message || '请求失败');
    return Promise.reject(error)
  }
)

export default service 