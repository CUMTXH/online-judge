<template>
  <div>
    <h1>欢迎登录个人代码训练日志系统</h1>
    <br>
    <el-button type="primary" @click="goLogin">登录</el-button>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const goLogin = () => {
  router.push('/login')
}
</script>
