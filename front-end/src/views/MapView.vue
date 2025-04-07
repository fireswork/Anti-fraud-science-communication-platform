<template>
  <div class="map-view-container">
    <AppHeader />
    <div class="map-content">
      <div class="map-wrapper" id="container"></div>
      <div class="article-list">
        <h3>文章位置标记</h3>
        <a-list 
          :data-source="nearbyArticles" 
          size="small"
          :loading="loading"
        >
          <template #empty>
            <div class="empty-list">暂无文章数据</div>
          </template>
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #title>
                  <a @click="viewArticle(item)">{{ item.title }}</a>
                </template>
                <template #description>
                  <div>{{ item.location }}</div>
                  <div class="article-meta">
                    <span>{{ item.author ? item.author.name : '未知' }}</span>
                    <span>{{ formatDate(item.createTime) }}</span>
                  </div>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>
      </div>
    </div>

    <a-modal
      v-model:open="modalVisible"
      :title="currentArticle.title"
      width="800px"
      @ok="handleModalOk"
    >
      <div class="article-detail">
        <div class="article-info">
          <a-avatar>
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <span class="author">{{ currentArticle.author ? currentArticle.author.name : '未知' }}</span>
          <span class="date">{{ formatDate(currentArticle.createTime) }}</span>
          <span class="location">{{ currentArticle.location }}</span>
        </div>

        <div class="article-body">
          {{ currentArticle.content }}
        </div>

        <a-divider />

        <div class="comments-section">
          <h3>评论</h3>
          <div v-if="commentsLoading" class="comments-loading">
            <a-spin />
          </div>
          <a-list
            v-else
            :data-source="comments"
            item-layout="horizontal"
            :pagination="false"
          >
            <template #empty>
              <div class="no-comments">暂无评论</div>
            </template>
            <template #renderItem="{ item }">
              <a-list-item>
                <a-comment
                  :author="item.author ? item.author.name : '未知用户'"
                  :content="item.content"
                  :datetime="formatDate(item.createTime)"
                >
                  <template #avatar>
                    <a-avatar>
                      <template #icon><UserOutlined /></template>
                    </a-avatar>
                  </template>
                </a-comment>
              </a-list-item>
            </template>
          </a-list>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { UserOutlined } from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import { getPostList, getPostDetail } from '@/api/post'
import { getCommentList } from '@/api/comment'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const AMAP_KEY = 'b6d79262c73c4413d9e82736c346f2a2'
const AMAP_API_URL =
  'https://webapi.amap.com/maps?v=2.0&key=' +
  AMAP_KEY +
  '&plugin=AMap.Scale,AMap.ToolBar,AMap.MarkerCluster'
const AMAP_SECURITY_CONFIG = {
  securityJsCode: '5077eda0e025be5a95ffaea82fff34cf',
}

// 配置安全密钥
window._AMapSecurityConfig = AMAP_SECURITY_CONFIG

let map = null
const modalVisible = ref(false)
const nearbyArticles = ref([])
const comments = ref([])
const loading = ref(false)
const commentsLoading = ref(false)

// 当前选中的文章
const currentArticle = ref({
  id: null,
  title: '',
  content: '',
  location: '',
  author: null,
  createTime: null,
  longitude: null,
  latitude: null
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await getPostList({ pageSize: 100 }) // 获取较多文章以在地图上显示
    if (res.code === 200) {
      const articles = res.data.records
      
      // 过滤出有经纬度的文章
      const validArticles = articles.filter(
        article => article.longitude && article.latitude
      )
      
      nearbyArticles.value = validArticles
      
      // 如果地图已初始化，添加标记
      if (map) {
        addArticleMarkers(validArticles)
      }
    } else {
      message.error(res.message || '获取文章列表失败')
    }
  } catch (error) {
    console.error('获取文章列表失败:', error)
    message.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 加载高德地图脚本
const loadAMapScript = () => {
  return new Promise((resolve, reject) => {
    if (window.AMap) {
      resolve(window.AMap)
      return
    }

    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = AMAP_API_URL
    script.onerror = reject
    script.onload = () => {
      resolve(window.AMap)
    }
    document.head.appendChild(script)
  })
}

// 初始化地图
const initMap = async () => {
  try {
    await loadAMapScript()
    map = new AMap.Map('container', {
      zoom: 12,
      center: [104.06476, 30.54876], // 默认中心点，可以调整
    })

    // 添加地图控件
    map.plugin(['AMap.Scale', 'AMap.ToolBar'], () => {
      map.addControl(new AMap.Scale())
      map.addControl(new AMap.ToolBar())
    })

    // 获取文章数据并添加标记
    await fetchArticles()
  } catch (error) {
    console.error('地图加载失败:', error)
    message.error('地图加载失败')
  }
}

// 添加文章标记
const addArticleMarkers = (articles) => {
  if (!map || !articles.length) return
  
  // 清除现有标记
  map.clearMap()
  
  const markers = articles.map((article) => {
    const marker = new AMap.Marker({
      position: [article.longitude, article.latitude],
      title: article.title,
      map: map
    })

    // 添加点击事件
    marker.on('click', () => {
      viewArticle(article)
      // 根据当前文章更新列表排序
      updateNearbyArticles(article)
    })

    return marker
  })

  // 添加标记聚合
  if (markers.length > 1) {
    map.plugin(['AMap.MarkerCluster'], () => {
      const cluster = new AMap.MarkerCluster(map, markers, {
        gridSize: 80,
        minClusterSize: 2
      })
      cluster.setMap(map)
    })
  }

  // 调整地图视野以包含所有标记
  if (markers.length > 0) {
    map.setFitView()
  }
}

// 更新附近文章列表，根据与当前选中文章的距离排序
const updateNearbyArticles = (centerArticle) => {
  if (!centerArticle || !centerArticle.id) return
  
  // 将选中的文章置顶，其他保持原顺序
  nearbyArticles.value = [...nearbyArticles.value].sort((a, b) => {
    if (a.id === centerArticle.id) return -1
    if (b.id === centerArticle.id) return 1
    return 0
  })
}

// 获取文章评论
const fetchComments = async (postId) => {
  if (!postId) return
  
  commentsLoading.value = true
  try {
    const res = await getCommentList(postId)
    if (res.code === 200) {
      comments.value = res.data
    } else {
      message.error(res.message || '获取评论失败')
    }
  } catch (error) {
    console.error('获取评论失败:', error)
    message.error('获取评论失败')
  } finally {
    commentsLoading.value = false
  }
}

// 查看文章详情
const viewArticle = async (article) => {
  try {
    const res = await getPostDetail(article.id)
    if (res.code === 200) {
      currentArticle.value = res.data
      modalVisible.value = true
      
      // 获取文章评论
      fetchComments(article.id)
    } else {
      message.error(res.message || '获取文章详情失败')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    message.error('获取文章详情失败')
  }
}

const handleModalOk = () => {
  modalVisible.value = false
}

// 组件挂载时初始化地图
onMounted(() => {
  initMap()
})

// 组件卸载时清理地图实例
onBeforeUnmount(() => {
  if (map) {
    map.destroy()
    map = null
  }
})
</script>

<style lang="less" scoped>
.map-view-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f0f2f5;
}

.map-content {
  flex: 1;
  display: flex;
  position: relative;
  overflow: hidden;
  margin-top: 64px; // 为顶部导航栏预留空间

  .map-wrapper {
    flex: 1;
    height: 100%;
  }

  .article-list {
    position: absolute;
    top: 20px;
    right: 20px;
    width: 300px;
    max-height: calc(100% - 40px);
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    overflow-y: auto;
    padding: 16px;

    h3 {
      margin: 0 0 16px;
      color: #001529;
    }

    :deep(.ant-list-item) {
      padding: 12px 0;
      cursor: pointer;

      &:hover {
        background: #f0f2f5;
      }
    }

    .article-meta {
      display: flex;
      gap: 16px;
      color: #999;
      font-size: 12px;
      margin-top: 4px;
    }
    
    .empty-list {
      text-align: center;
      color: #999;
      padding: 20px 0;
    }
  }
}

.article-detail {
  .article-info {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;

    .author {
      font-weight: 500;
    }

    .date,
    .location {
      color: #999;
    }
  }

  .article-body {
    font-size: 14px;
    line-height: 1.8;
    color: #333;
    margin-bottom: 24px;
  }
}

.comments-section {
  h3 {
    margin-bottom: 16px;
  }
  
  .comments-loading {
    text-align: center;
    padding: 20px 0;
  }
  
  .no-comments {
    text-align: center;
    color: #999;
    padding: 20px 0;
  }
}
</style>
