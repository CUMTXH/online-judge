import type { ViteSSGContext } from 'vite-ssg'

export type UserModule = (ctx: ViteSSGContext) => void

export interface ApiResponse<T = any> {
    code: number
    message?: string
    data: T
  }
  
  // 登录请求类型
  export interface LoginRequest {
    username: string
    password: string
  }

  
export interface JwtPayload {
    username: string
    // 其他可能的字段...
  }