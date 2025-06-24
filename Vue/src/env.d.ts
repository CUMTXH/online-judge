/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'

  const component: DefineComponent<object, object, any>
  export default component
}

// declare module 'jwt-decode' {
//   function jwt_decode<T = unknown>(token: string): T
//   export default jwt_decode
// }