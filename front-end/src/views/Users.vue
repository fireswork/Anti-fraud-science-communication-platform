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
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
            <a-button type="link" @click="handleResetPassword(record)">重置密码</a-button>
            <a-popconfirm
              title="确定要删除该用户吗？"
              @confirm="handleDelete(record)"
            >
              <a-button type="link" danger>删除</a-button>
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
      <a-form
        :model="userForm"
        layout="vertical"
        ref="userFormRef"
        :rules="userFormRules"
      >
        <a-form-item
          label="用户名"
          name="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input v-model:value="userForm.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>

        <a-form-item
          label="姓名"
          name="name"
          :rules="[{ required: true, message: '请输入姓名' }]"
        >
          <a-input v-model:value="userForm.name" placeholder="请输入姓名" />
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
          :rules="[
            { required: true, message: '请输入手机号' },
            { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
          ]"
        >
          <a-input v-model:value="userForm.phone" placeholder="请输入手机号" />
        </a-form-item>

        <a-form-item
          label="地址"
          name="address"
        >
          <a-input v-model:value="userForm.address" placeholder="请输入地址" />
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
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { UserAddOutlined } from '@ant-design/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetPassword } from '@/api/user'

const userFormRef = ref(null)

// 表格列定义
const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name',
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
    title: '地址',
    dataIndex: 'address',
    key: 'address',
  },
  {
    title: '注册时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
  },
]

// 用户列表
const userList = ref([])

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
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
  name: '',
  email: '',
  phone: '',
  address: '',
  password: '',
  confirmPassword: '',
})

// 表单规则
const userFormRules = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 4, message: '用户名至少4个字符' }
  ],
  name: [{ required: true, message: '请输入姓名' }],
  email: [
    { required: true, message: '请输入邮箱' },
    { type: 'email', message: '请输入有效的邮箱地址' }
  ],
  phone: [
    { required: true, message: '请输入手机号' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, message: '密码至少6个字符' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码' }
  ]
}

// 表单验证
const validateConfirmPassword = async (rule, value) => {
  if (value !== userForm.password) {
    throw new Error('两次输入的密码不一致')
  }
}

// 初始化
onMounted(() => {
  fetchUserList()
})

// 获取用户列表
const fetchUserList = async (params = {}) => {
  loading.value = true
  try {
    const queryParams = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      keyword: searchText.value,
      ...params
    }
    
    const res = await getUserList(queryParams)
    if (res.code === 200) {
      // 过滤出普通用户，不显示管理员
      userList.value = res.data.records.filter(user => user.role !== 'ADMIN')
      pagination.total = res.data.total
    } else {
      message.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 事件处理
const showAddUserModal = () => {
  isEdit.value = false
  userForm.id = null
  userForm.username = ''
  userForm.name = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.address = ''
  userForm.password = ''
  userForm.confirmPassword = ''
  userModalVisible.value = true
}

const handleSearch = () => {
  pagination.current = 1
  fetchUserList()
}

const handleUserSubmit = async () => {
  try {
    await userFormRef.value.validate()
    submitting.value = true
    
    let res
    if (isEdit.value) {
      // 编辑用户
      res = await updateUser({
        id: userForm.id,
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone,
        address: userForm.address
      })
    } else {
      // 添加用户
      res = await addUser({
        username: userForm.username,
        name: userForm.name,
        email: userForm.email,
        phone: userForm.phone,
        address: userForm.address,
        password: userForm.password
      })
    }
    
    if (res.code === 200) {
      message.success(isEdit.value ? '用户更新成功' : '用户添加成功')
      userModalVisible.value = false
      fetchUserList()
    } else {
      message.error(res.message || (isEdit.value ? '更新失败' : '添加失败'))
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    submitting.value = false
  }
}

const handleEdit = (record) => {
  isEdit.value = true
  userForm.id = record.id
  userForm.username = record.username
  userForm.name = record.name
  userForm.email = record.email
  userForm.phone = record.phone
  userForm.address = record.address || ''
  userModalVisible.value = true
}

const handleResetPassword = async (record) => {
  try {
    const res = await resetPassword(record.id)
    if (res.code === 200) {
      message.success(`已重置用户 ${record.username} 的密码`)
    } else {
      message.error(res.message || '重置密码失败')
    }
  } catch (error) {
    console.error('重置密码失败:', error)
    message.error('重置密码失败')
  }
}

const handleDelete = async (record) => {
  try {
    const res = await deleteUser(record.id)
    if (res.code === 200) {
      message.success(`已删除用户 ${record.username}`)
      fetchUserList()
    } else {
      message.error(res.message || '删除用户失败')
    }
  } catch (error) {
    console.error('删除用户失败:', error)
    message.error('删除用户失败')
  }
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchUserList()
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
