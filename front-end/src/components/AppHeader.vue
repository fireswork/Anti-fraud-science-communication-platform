<template>
  <div class="header">
    <div class="logo">
      <SafetyCertificateOutlined class="logo-icon" />
      <h2>防诈科普及交流平台</h2>
    </div>
    <div class="user-center">
      <a-dropdown>
        <div class="user-btn">
          个人中心
          <a-avatar class="avatar-icon">
            <template #icon><UserOutlined /></template>
          </a-avatar>
        </div>
        <template #overlay>
          <a-menu>
            <a-menu-item key="profile" @click="showProfileModal">
              <UserOutlined />
              个人信息
            </a-menu-item>
            <a-menu-item key="password" @click="showPasswordModal">
              <LockOutlined />
              修改密码
            </a-menu-item>
            <a-menu-item key="logout" @click="handleLogout">
              <LogoutOutlined />
              退出登录
            </a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

    <!-- 修改个人信息 Modal -->
    <a-modal
      v-model:visible="profileModalVisible"
      title="修改个人信息"
      @ok="handleProfileSubmit"
      @cancel="handleProfileCancel"
    >
      <a-form
        :model="profileForm"
        :rules="profileRules"
        ref="profileFormRef"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="profileForm.username" disabled>
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="姓名" name="name">
          <a-input v-model:value="profileForm.name">
            <template #prefix><IdcardOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="手机号码" name="phone">
          <a-input v-model:value="profileForm.phone">
            <template #prefix><PhoneOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="电子邮箱" name="email">
          <a-input v-model:value="profileForm.email">
            <template #prefix><MailOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="详细地址" name="address">
          <a-input v-model:value="profileForm.address">
            <template #prefix><HomeOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="出生日期" name="birthday">
          <a-input-group compact>
            <a-input style="width: 40px" class="date-prefix">
              <template #prefix>
                <CalendarOutlined />
              </template>
            </a-input>
            <a-date-picker
              v-model:value="profileForm.birthday"
              class="birthday-picker"
              placeholder="请选择出生日期"
            />
          </a-input-group>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改密码 Modal -->
    <a-modal
      v-model:visible="passwordModalVisible"
      title="修改密码"
      @ok="handlePasswordSubmit"
      @cancel="handlePasswordCancel"
    >
      <a-form
        :model="passwordForm"
        :rules="passwordRules"
        ref="passwordFormRef"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="当前密码" name="currentPassword">
          <a-input-password v-model:value="passwordForm.currentPassword">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item label="新密码" name="newPassword">
          <a-input-password v-model:value="passwordForm.newPassword">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="passwordForm.confirmPassword">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  SafetyCertificateOutlined,
  LockOutlined,
  LogoutOutlined,
  IdcardOutlined,
  PhoneOutlined,
  MailOutlined,
  HomeOutlined,
  CalendarOutlined,
} from '@ant-design/icons-vue'

const router = useRouter()
const profileFormRef = ref()
const passwordFormRef = ref()

// Modal 显示状态
const profileModalVisible = ref(false)
const passwordModalVisible = ref(false)

// 个人信息表单
const profileForm = reactive({
  username: '',
  name: '',
  phone: '',
  email: '',
  address: '',
  birthday: null,
})

// 密码表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
})

// 表单验证规则
const profileRules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 4, message: '用户名至少4个字符' },
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

const passwordRules = {
  currentPassword: [{ required: true, message: '请输入当前密码' }],
  newPassword: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码至少6个字符' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    ({ getFieldValue }) => ({
      validator(_, value) {
        if (!value || getFieldValue('newPassword') === value) {
          return Promise.resolve()
        }
        return Promise.reject('两次输入的密码不一致')
      },
    }),
  ],
}

// 显示修改个人信息 Modal
const showProfileModal = () => {
  profileModalVisible.value = true
}

// 显示修改密码 Modal
const showPasswordModal = () => {
  passwordModalVisible.value = true
}

// 处理个人信息提交
const handleProfileSubmit = async () => {
  try {
    await profileFormRef.value.validate()
    // TODO: 调用API更新用户信息
    message.success('个人信息更新成功')
    profileModalVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 处理密码修改提交
const handlePasswordSubmit = async () => {
  try {
    await passwordFormRef.value.validate()
    // TODO: 调用API更新密码
    message.success('密码修改成功')
    passwordModalVisible.value = false
    // 清空表单
    passwordForm.currentPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 处理 Modal 取消
const handleProfileCancel = () => {
  profileModalVisible.value = false
}

const handlePasswordCancel = () => {
  passwordModalVisible.value = false
  // 清空表单
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
}

// 处理退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('avatar')
  localStorage.removeItem('isAdmin')
  message.success('退出登录成功')
  router.push('/login')
}
</script>

<style lang="less" scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 48px;
  background-color: #000;
  height: 64px;

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    .logo-icon {
      font-size: 40px;
      color: #1890ff;
    }
    h2 {
      font-size: 24px;
      margin-bottom: 0;
      color: #fff;
    }
  }

  .user-center {
    .user-btn {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #fff;
      font-size: 16px;
      cursor: pointer;

      :deep(.avatar-icon) {
        background-color: #1890ff;
      }
    }
  }
}

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
</style>
