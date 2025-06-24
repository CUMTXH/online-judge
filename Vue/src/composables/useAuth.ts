import { ref } from 'vue'
import request from '../utils/request'
import { ElMessage } from 'element-plus'
import { ApiResponse } from '~/types'

export function useAuth() {
  const isAuthenticated = ref(!!localStorage.getItem('token'))

  const login = async (credentials: { username: string; password: string }) => {
    const { data } = await request.post<ApiResponse<string>>('/login', credentials)
    if (data.code === 200 && data.data) {
      localStorage.setItem('token', data.data)
      isAuthenticated.value = true
    } else {
      throw new Error(data.message || '登录失败')
    }
  }

  const register = async (credentials: { username: string; password: string }) => {
    const { data } = await request.post<ApiResponse<null>>('/register', credentials)
    if (data.code === 200) {
      ElMessage.success('注册成功，请登录')
    } else {
      throw new Error(data.message || '注册失败')
    }
  }

  const logout = () => {
    localStorage.removeItem('token')
    isAuthenticated.value = false
  }
  
  const checkAuth = async () => {
    try {
      const { data } = await request.get<ApiResponse<boolean>>('/auth/validate')
      return data.code === 200 && data.data === true
    } catch {
      return false
    }
  }

  return { isAuthenticated, login, logout, checkAuth ,register}
}