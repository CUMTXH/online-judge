<template>
    <div class="register-wrapper">
        <div class="register-container">
            <div class="form-header">
                <h2>创建账号</h2>

            </div>
            <form @submit.prevent="handleRegister" class="floating-form">
                <div class="input-group">
                    <input type="text" id="username" v-model.trim="form.username" required maxlength="20" autocomplete="off"/>
                    <label for="username">用户名</label>
                    <span class="highlight"></span>
                </div>
                <div class="input-group">
                    <input type="password" id="password" v-model.trim="form.password" required minlength="6" maxlength="20" autocomplete="off"/>
                    <label for="password">密码</label>
                    <span class="highlight"></span>
                </div>
                <div class="input-group">
                    <input type="password" id="confirmPassword" v-model.trim="form.confirmPassword" required minlength="6" maxlength="20" />
                    <label for="confirmPassword">确认密码</label>
                    <span class="highlight"></span>
                </div>
                <div class="error-message" v-if="errorMsg">{{ errorMsg }}</div>
                
                <button type="submit" class="submit-btn">
                    <span>立即注册</span>
                    <i class="arrow-icon"></i>
                </button>
                <div class="form-footer">
                    <span>已有账号？</span>
                    <a href="/login">立即登录</a>
                </div>
            </form>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuth } from '~/composables/useAuth'
import request from '~/utils/request'
 
const router = useRouter()
const { register } = useAuth()

const form = ref({
  username: '',
  password: '',
  confirmPassword: ''
})

const errorMsg = ref('')

const handleRegister = async () => {
  errorMsg.value = ''
  if (form.value.password !== form.value.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  try {
    console.log('当前表单数据：', form.value)
    await register({
      username: form.value.username,
      password: form.value.password
    })
    // 注册成功后跳转到登录页，并携带 from=register 参数
    router.push({ path: '/login', query: { from: 'register' } })
  } catch (err) {
    errorMsg.value = err.message || '注册失败'
  }
}
</script>
 
<style scoped>
.register-wrapper {
    min-height: 93.5vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #f5f7fa 0%, rgb(166, 189, 226) 100%);
}
 
.register-container {
    width: 100%;
    max-width: 480px;
    background: white;
    border-radius: 20px;
    padding: 40px;
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
 
.input-group input:focus + label,
.input-group input:valid + label {
    top: 0;
    font-size: 14px;
    color: #3498db;
}
 
.verification-group {
    display: flex;
    gap: 10px;
}
 
.verification-group input {
    flex: 1;
}
 
.send-code-btn {
    padding: 0 20px;
    background: #e8f5fe;
    color: #3498db;
    border: none;
    border-radius: 12px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;
    white-space: nowrap;
}
 
.send-code-btn:hover {
    background: #d0e9fd;
}
 
.submit-btn {
    width: 100%;
    padding: 15px;
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
 
@media (max-width: 480px) {
    .register-container {
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
    .register-container {
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
    .register-container {
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
 
    .verification-group {
        flex-direction: column;
        gap: 5px;
    }
 
    .send-code-btn {
        width: 100%;
        padding: 12px;
    }
 
    .submit-btn {
        padding: 12px;
        font-size: 16px;
    }
}
 
@media (max-width: 320px) {
    .register-container {
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