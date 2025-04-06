<template>
  <div class="users-container">
    <div class="header-actions">
      <a-button type="primary" @click="showAddUserModal">
        <template #icon><UserAddOutlined /></template>
        添加用户
      </a-button>
      <a-input-search
        v-model:value="searchText"
        placeholder="搜索用户名或邮箱"
        style="width: 300px; margin-left: 16px"
        @search="handleSearch"
      />
    </div>

    <a-table
      :columns="columns"
      :data-source="userList"
      :loading="loading"
      rowKey="id"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'avatar'">
          <a-avatar :size="40" :src="record.avatar" />
        </template>

        <template v-if="column.key === 'role'">
          <a-tag :color="getRoleColor(record.role)">
            {{ getRoleText(record.role) }}
          </a-tag>
        </template>

        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>

        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" @click="handleResetPassword(record)">重置密码</a-button>
            <a-popconfirm
              :title="record.status === 'active' ? '确定要禁用该用户吗？' : '确定要启用该用户吗？'"
              @confirm="handleToggleStatus(record)"
            >
              <a-button type="link" :danger="record.status === 'active'">
                {{ record.status === 'active' ? '禁用' : '启用' }}
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 添加/编辑用户弹窗 -->
    <a-modal
      v-model:visible="userModalVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      @ok="handleUserSubmit"
      :confirmLoading="submitting"
    >
      <a-form :model="userForm" layout="vertical">
        <a-form-item
          label="用户名"
          name="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input v-model:value="userForm.username" placeholder="请输入用户名" />
        </a-form-item>

        <a-form-item
          label="邮箱"
          name="email"
          :rules="[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '请输入有效的邮箱地址' },
          ]"
        >
          <a-input v-model:value="userForm.email" placeholder="请输入邮箱" />
        </a-form-item>

        <a-form-item
          label="手机号"
          name="phone"
          :rules="[{ required: true, message: '请输入手机号' }]"
        >
          <a-input v-model:value="userForm.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item label="角色" name="role" :rules="[{ required: true, message: '请选择角色' }]">
          <a-select v-model:value="userForm.role" placeholder="请选择角色">
            <a-select-option value="admin">管理员</a-select-option>
            <a-select-option value="editor">编辑</a-select-option>
            <a-select-option value="user">普通用户</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          v-if="!isEdit"
          label="密码"
          name="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password v-model:value="userForm.password" placeholder="请输入密码" />
        </a-form-item>

        <a-form-item
          v-if="!isEdit"
          label="确认密码"
          name="confirmPassword"
          :rules="[
            { required: true, message: '请确认密码' },
            { validator: validateConfirmPassword },
          ]"
        >
          <a-input-password v-model:value="userForm.confirmPassword" placeholder="请确认密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { UserAddOutlined } from '@ant-design/icons-vue'

// 表格列定义
const columns = [
  {
    title: '头像',
    dataIndex: 'avatar',
    key: 'avatar',
    width: 80,
  },
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email',
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '注册时间',
    dataIndex: 'registerTime',
    key: 'registerTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 模拟用户数据
const userList = ref([
  {
    id: 1,
    username: '管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    role: 'admin',
    status: 'active',
    registerTime: '2024-01-01 12:00',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',
  },
  {
    id: 2,
    username: '编辑员',
    email: 'editor@example.com',
    phone: '13800138001',
    role: 'editor',
    status: 'active',
    registerTime: '2024-02-15 14:30',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=editor',
  },
  {
    id: 3,
    username: '测试用户',
    email: 'user@example.com',
    phone: '13800138002',
    role: 'user',
    status: 'inactive',
    registerTime: '2024-03-10 09:15',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=user',
  },
])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 100,
})

// 加载状态
const loading = ref(false)
const submitting = ref(false)

// 搜索
const searchText = ref('')

// 用户表单
const userModalVisible = ref(false)
const isEdit = ref(false)
const userForm = reactive({
  id: null,
  username: '',
  email: '',
  phone: '',
  role: undefined,
  password: '',
  confirmPassword: '',
})

// 状态处理
const getRoleColor = (role) => {
  const colors = {
    admin: 'red',
    editor: 'blue',
    user: 'green',
  }
  return colors[role] || 'default'
}

const getRoleText = (role) => {
  const texts = {
    admin: '管理员',
    editor: '编辑',
    user: '普通用户',
  }
  return texts[role] || '未知角色'
}

const getStatusColor = (status) => {
  const colors = {
    active: 'success',
    inactive: 'default',
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    active: '正常',
    inactive: '禁用',
  }
  return texts[status] || '未知状态'
}

// 表单验证
const validateConfirmPassword = async (rule, value) => {
  if (value !== userForm.password) {
    throw new Error('两次输入的密码不一致')
  }
}

// 事件处理
const showAddUserModal = () => {
  isEdit.value = false
  userForm.id = null
  userForm.username = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.role = undefined
  userForm.password = ''
  userForm.confirmPassword = ''
  userModalVisible.value = true
}

const handleSearch = (value) => {
  console.log('搜索:', value)
  // 这里实现搜索逻辑
}

const handleUserSubmit = () => {
  submitting.value = true
  // 模拟提交过程
  setTimeout(() => {
    submitting.value = false
    userModalVisible.value = false
    message.success(isEdit.value ? '用户信息更新成功' : '用户添加成功')
  }, 1000)
}

const handleEdit = (record) => {
  isEdit.value = true
  userForm.id = record.id
  userForm.username = record.username
  userForm.email = record.email
  userForm.phone = record.phone
  userForm.role = record.role
  userModalVisible.value = true
}

const handleResetPassword = (record) => {
  message.success(`已重置用户 ${record.username} 的密码`)
}

const handleToggleStatus = (record) => {
  const newStatus = record.status === 'active' ? 'inactive' : 'active'
  record.status = newStatus
  message.success(`已${newStatus === 'active' ? '启用' : '禁用'}用户 ${record.username}`)
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  // 这里可以调用接口重新加载数据
}
</script>

<style lang="less" scoped>
.users-container {
  .header-actions {
    margin-bottom: 16px;
    display: flex;
    align-items: center;
  }
}
</style>
