<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <SafetyCertificateOutlined class="logo-icon" />
        <h2>防诈科普及交流平台</h2>
      </div>

      <a-form
        :model="formState"
        name="login"
        autocomplete="off"
        @finish="onFinish"
        class="login-form"
      >
        <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="formState.username" placeholder="用户名">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="formState.password" placeholder="密码">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" class="login-button" :loading="loading">
            登录
          </a-button>
        </a-form-item>

        <div class="form-footer">
          <!-- <a-checkbox v-model:checked="formState.remember">记住我</a-checkbox> -->
          <div class="links">
            <!-- <a @click="handleForgotPassword">忘记密码</a> -->
            <a @click="$router.push('/register')">注册账号</a>
          </div>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: '',
  remember: false,
})

const onFinish = async (values) => {
  try {
    loading.value = true
    // TODO: 实现登录逻辑
    console.log('登录信息:', values)
    message.success('登录成功')
    router.push('/')
  } catch (error) {
    message.error('登录失败')
  } finally {
    loading.value = false
  }
}

const handleForgotPassword = () => {
  message.info('请联系管理员重置密码')
}
</script>

<style lang="less" scoped>
.login-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #001529 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 32px;

  .logo-icon {
    font-size: 32px;
    color: #1890ff;
  }

  h2 {
    margin: 0;
    color: #001529;
    font-size: 24px;
  }
}

.login-form {
  .login-button {
    width: 100%;
    height: 40px;
    font-size: 16px;
  }
}

.form-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: -12px;

  .links {
    display: flex;
    gap: 16px;

    a {
      color: #1890ff;
      transition: color 0.3s;

      &:hover {
        color: #40a9ff;
      }
    }
  }
}
</style>
