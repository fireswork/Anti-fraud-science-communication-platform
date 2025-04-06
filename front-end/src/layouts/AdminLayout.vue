<template>
  <div class="admin-layout">
    <AppHeader />
    <a-layout>
      <a-layout-sider width="200" class="sider">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleMenuClick"
        >
          <a-menu-item key="users">
            <template #icon>
              <TeamOutlined />
            </template>
            用户管理
          </a-menu-item>
          <a-menu-item key="videos">
            <template #icon>
              <VideoCameraOutlined />
            </template>
            视频管理
          </a-menu-item>
        </a-menu>
      </a-layout-sider>

      <a-layout-content class="content">
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { TeamOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'

const router = useRouter()
const route = useRoute()

const selectedKeys = ref(['users'])

onMounted(() => {
  // 根据当前路由设置选中的菜单项
  const path = route.path.split('/')[2] || 'users'
  selectedKeys.value = [path]
})

// 处理菜单点击事件
const handleMenuClick = ({ key }) => {
  router.push(`/admin/${key}`)
}
</script>

<style lang="less" scoped>
.ant-layout {
  height: calc(100vh - 64px);
}
.admin-layout {
  min-height: 100vh;
}

.sider {
  background: #fff;
}

.content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  min-height: 280px;
}
</style>
