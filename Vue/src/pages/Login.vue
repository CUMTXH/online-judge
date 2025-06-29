<template>
    <div class="login-wrapper">
        <div class="login-container">
            <div class="form-header">
                <h2>用户登录</h2>
                <p>欢迎回来，请登录您的账号</p>
            </div>
            <form @submit.prevent="handleLogin" class="floating-form">
                <div class="input-group">
                    <input type="text" id="username" v-model.trim="form.username" autocomplete="off"
                        @input="validateInput" required />
                    <label for="username">用户名</label>
                    <span class="highlight"></span>
                </div>
                <div class="input-group">
                    <input type="password" id="password" v-model.trim="form.password" autocomplete="off"
                        @input="validateInput" required />
                    <label for="password">密码</label>
                    <span class="highlight"></span>
                </div>
                <div class="error-message" v-if="errorMsg">{{ errorMsg }}</div>
                <button type="submit" class="submit-btn" >
                    <span>登录</span>
                    <i class="arrow-icon"></i>
                </button>
                <div class="form-footer">
                    <span>还没有账号？</span>
                    <a href="/Register">立即注册</a>
                </div>
            </form>
        </div>
    </div>
</template>
 
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '~/composables/useAuth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const { login } = useAuth()

// 表单数据
const form = ref({
  username: '',
  password: ''
})

const errorMsg = ref('')

const handleLogin = async () => {
  try {
    await login(form.value)
    const redirect = router.currentRoute.value.query.redirect || '/notes'
    router.push(redirect)
  } catch (err) {
  errorMsg.value = err.message || '登录失败' // 同时注意模板中使用的变量名是 errorMsg
  ElMessage.error(errorMsg.value) // 添加 Element Plus 提示
  }
}


</script>
 
<style scoped>
.login-wrapper {
    min-height: 93.5vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #f5f7fa 0%, rgb(166, 189, 226) 100%);
}

.login-container {
    width: 100%;
    max-width: 480px;
    background: white;
    border-radius: 20px;
    padding: 40px;
    padding-right: 80px;
    box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.form-header {
    text-align: center;
    margin-bottom: 40px;
}

.form-header h2 {
    color: #2c3e50;
    font-size: 32px;
    margin-bottom: 10px;
    font-weight: 700;
}

.form-header p {
    color: #95a5a6;
    font-size: 16px;
}

.floating-form .input-group {
    position: relative;
    margin-bottom: 30px;
}

.input-group input {
    width: 100%;
    padding: 15px;
    border: 2px solid #e0e0e0;
    border-radius: 12px;
    font-size: 16px;
    transition: all 0.3s ease;
    background: transparent;
}

.input-group label {
    position: absolute;
    left: 15px;
    top: 50%;
    transform: translateY(-50%);
    background: white;
    padding: 0 5px;
    color: #95a5a6;
    font-size: 16px;
    transition: all 0.3s ease;
    pointer-events: none;
}

.input-group input:focus,
.input-group input:valid {
    border-color: #3498db;
}

.input-group input:focus+label,
.input-group input:valid+label {
    top: 0;
    font-size: 14px;
    color: #3498db;
}

.submit-btn {
    width: 100%;
    padding: 15px;
    margin-left: 15px;
    background: linear-gradient(to right, #3498db, #2980b9);
    color: white;
    border: none;
    border-radius: 12px;
    font-size: 18px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
}

.submit-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(52, 152, 219, 0.3);
}

.arrow-icon {
    border: solid white;
    border-width: 0 2px 2px 0;
    display: inline-block;
    padding: 3px;
    transform: rotate(-45deg);
}

.form-footer {
    text-align: center;
    margin-top: 20px;
    color: #95a5a6;
}

.form-footer a {
    color: #3498db;
    text-decoration: none;
    margin-left: 5px;
    font-weight: 600;
}

.form-footer a:hover {
    text-decoration: underline;
}

.error-message {
    color: #f56c6c;
    font-size: 14px;
    text-align: center;
    margin-bottom: 20px;
}

@media (max-width: 480px) {
    .login-container {
        padding: 20px;
    }

    .form-header h2 {
        font-size: 24px;
    }

    .input-group input {
        padding: 12px;
    }
}

@media (max-width: 768px) {
    .login-container {
        max-width: 400px;
        padding: 30px;
    }

    .form-header h2 {
        font-size: 28px;
    }

    .form-header p {
        font-size: 14px;
    }
}

@media (max-width: 480px) {
    .login-container {
        padding: 20px;
        margin: 10px;
        max-width: 100%;
    }

    .form-header h2 {
        font-size: 24px;
    }

    .form-header p {
        font-size: 14px;
    }

    .input-group input {
        padding: 12px;
        font-size: 14px;
    }

    .input-group label {
        font-size: 14px;
    }

    .submit-btn {
        padding: 12px;
        font-size: 16px;
    }
}

@media (max-width: 320px) {
    .login-container {
        padding: 15px;
    }

    .form-header h2 {
        font-size: 20px;
    }

    .input-group {
        margin-bottom: 20px;
    }
}
</style>