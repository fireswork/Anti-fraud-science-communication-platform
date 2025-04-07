<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <SafetyCertificateOutlined class="logo-icon" />
        <h2>用户注册</h2>
      </div>

      <a-form
        :model="formState"
        name="register"
        autocomplete="off"
        @finish="onFinish"
        class="register-form"
        :rules="rules"
      >
        <a-form-item name="username">
          <a-input v-model:value="formState.username" placeholder="用户名">
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password v-model:value="formState.password" placeholder="密码">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="confirmPassword">
          <a-input-password v-model:value="formState.confirmPassword" placeholder="确认密码">
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item name="name">
          <a-input v-model:value="formState.name" placeholder="姓名">
            <template #prefix>
              <IdcardOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="phone">
          <a-input v-model:value="formState.phone" placeholder="手机号码">
            <template #prefix>
              <PhoneOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="email">
          <a-input v-model:value="formState.email" placeholder="电子邮箱">
            <template #prefix>
              <MailOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="address">
          <a-input v-model:value="formState.address" placeholder="详细地址">
            <template #prefix>
              <HomeOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="birthday">
          <a-input-group compact>
            <a-input style="width: 40px" class="date-prefix">
              <template #prefix>
                <CalendarOutlined />
              </template>
            </a-input>
            <a-date-picker
              v-model:value="formState.birthday"
              class="birthday-picker"
              placeholder="出生日期"
            />
          </a-input-group>
        </a-form-item>

        <a-form-item>
          <a-button type="primary" html-type="submit" class="register-button" :loading="loading">
            注册
          </a-button>
        </a-form-item>

        <div class="form-footer">
          <span>已有账号？</span>
          <a @click="$router.push('/login')">立即登录</a>
        </div>
      </a-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  LockOutlined,
  IdcardOutlined,
  PhoneOutlined,
  MailOutlined,
  SafetyCertificateOutlined,
  HomeOutlined,
  CalendarOutlined,
} from '@ant-design/icons-vue'
import { register } from '@/api/user' 
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)

const formState = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  birthday: null,
})

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 4, message: '用户名至少4个字符' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6个字符' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    ({ getFieldValue }) => ({
      validator(_, value) {
        if (!value || getFieldValue('password') === value) {
          return Promise.resolve()
        }
        return Promise.reject('两次输入的密码不一致')
      },
    }),
  ],
  name: [{ required: true, message: '请输入姓名' }],
  phone: [
    { required: true, message: '请输入手机号码' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码' },
  ],
  email: [
    { required: true, message: '请输入电子邮箱' },
    { type: 'email', message: '请输入正确的邮箱格式' },
  ],
  address: [{ required: true, message: '请输入详细地址' }],
  birthday: [{ required: true, message: '请选择出生日期' }],
}

const onFinish = async (values) => {
  try {
    loading.value = true
    
    // 处理生日格式 
    const userData = { ...values }
    if (userData.birthday) {
      userData.birthday = dayjs(userData.birthday).format('YYYY-MM-DD')
    }
    
    // 删除确认密码字段，后端不需要
    delete userData.confirmPassword
    
    // 调用注册API
    const res = await register(userData)
    
    if (res.code === 200) {
      message.success(res.message || '注册成功')
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="less" scoped>
.register-container {
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1890ff 0%, #001529 100%);
  padding: 24px;
}

.register-box {
  width: 500px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.register-header {
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

.register-form {
  .birthday-picker {
    width: calc(100% - 40px);
  }

  .date-prefix {
    border-right: 0;
    pointer-events: none;
    background-color: #fafafa;

    :deep(.ant-input) {
      text-align: center;
      border-radius: 6px 0 0 6px;
    }

    :deep(.anticon) {
      color: rgba(0, 0, 0, 0.45);
    }
  }

  .register-button {
    width: 100%;
    height: 40px;
    font-size: 16px;
  }
}

.form-footer {
  text-align: center;
  margin-top: 16px;

  span {
    color: #666;
  }

  a {
    color: #1890ff;
    margin-left: 8px;
    transition: color 0.3s;

    &:hover {
      color: #40a9ff;
    }
  }
}
</style>
