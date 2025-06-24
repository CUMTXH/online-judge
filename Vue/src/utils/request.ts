import axios from 'axios'

const service = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000,
    withCredentials: true
})

// 请求拦截器：注入token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  config.headers = config.headers || {}

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }else {
    console.log("No token found in localStorage")
  }

  //  打印请求详情
  console.log("发起请求：", config.method?.toUpperCase(), config.url)
  console.log("请求头：", config.headers)
  if (config.data) console.log("请求体：", config.data)

  return config
})

// 响应拦截器：处理401错误
service.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default service