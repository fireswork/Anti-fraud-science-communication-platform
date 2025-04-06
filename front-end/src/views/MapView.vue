<template>
  <div class="map-view-container">
    <div class="map-content">
      <div class="map-wrapper" id="container"></div>
      <div class="article-list">
        <h3>附近的文章</h3>
        <a-list :data-source="nearbyArticles" size="small">
          <template #renderItem="{ item }">
            <a-list-item>
              <a-list-item-meta>
                <template #title>
                  <a @click="viewArticle(item)">{{ item.title }}</a>
                </template>
                <template #description>
                  <div>{{ item.location }}</div>
                  <div class="article-meta">
                    <span>{{ item.author }}</span>
                    <span>{{ item.date }}</span>
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
          <span class="author">{{ currentArticle.author }}</span>
          <span class="date">{{ currentArticle.date }}</span>
          <span class="location">{{ currentArticle.location }}</span>
        </div>

        <div class="article-body">
          {{ currentArticle.content }}
        </div>

        <a-divider />

        <div class="comments-section">
          <h3>评论</h3>
          <a-list
            :data-source="currentArticle.commentList"
            item-layout="horizontal"
            :pagination="false"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-comment
                  :author="item.author"
                  :avatar="item.avatar"
                  :content="item.content"
                  :datetime="item.datetime"
                />
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

// 模拟文章数据
const mockArticles = [
  {
    id: 1,
    title: '成都某小区电信诈骗案例分析',
    author: '张三',
    content: '最近在成都市武侯区某小区发生了一起电信诈骗案件...',
    location: '成都市武侯区天府三街',
    longitude: 104.06476,
    latitude: 30.54876,
    date: '2024-03-08',
    commentList: [
      {
        author: '李四',
        avatar: null,
        content: '这个案例很有警示意义',
        datetime: '2024-03-08 14:30',
      },
    ],
  },
  {
    id: 2,
    title: '防范校园诈骗指南',
    author: '王五',
    content: '针对在四川大学周边发生的几起诈骗案件，整理了以下防范建议...',
    location: '成都市望江路',
    longitude: 104.08476,
    latitude: 30.63876,
    date: '2024-03-07',
    commentList: [],
  },
  {
    id: 3,
    title: '春熙路商圈诈骗手段分析',
    author: '赵六',
    content: '春熙路商圈人流量大，容易发生以下几种诈骗情况...',
    location: '成都市春熙路',
    longitude: 104.06976,
    latitude: 30.65876,
    date: '2024-03-06',
    commentList: [],
  },
]

let map = null
const modalVisible = ref(false)
const nearbyArticles = ref([])
const currentArticle = ref(mockArticles[0])

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
      center: [104.06476, 30.54876], // 成都市中心
    })

    // 添加地图控件
    map.plugin(['AMap.Scale', 'AMap.ToolBar'], () => {
      map.addControl(new AMap.Scale())
      map.addControl(new AMap.ToolBar())
    })

    // 添加文章标记
    addArticleMarkers()
  } catch (error) {
    console.error('地图加载失败:', error)
  }
}

// 添加文章标记
const addArticleMarkers = () => {
  const markers = mockArticles.map((article) => {
    const marker = new AMap.Marker({
      position: [article.longitude, article.latitude],
      title: article.title,
      map: map, // 直接将标记添加到地图上
    })

    // 添加点击事件
    marker.on('click', () => {
      viewArticle(article)
      // 更新附近文章列表
      updateNearbyArticles(article)
    })

    return marker
  })

  // 添加标记聚合
  map.plugin(['AMap.MarkerCluster'], () => {
    const cluster = new AMap.MarkerCluster(map, markers, {
      gridSize: 80,
      minClusterSize: 2,
    })
    cluster.setMap(map) // 将聚合添加到地图上
  })

  // 调整地图视野以包含所有标记
  map.setFitView()

  // 初始化显示所有文章
  nearbyArticles.value = mockArticles
}

// 更新附近文章列表
const updateNearbyArticles = (centerArticle) => {
  // 这里可以根据距离进行排序，这里简单处理，显示所有文章
  nearbyArticles.value = mockArticles.sort((a, b) => {
    if (a.id === centerArticle.id) return -1
    if (b.id === centerArticle.id) return 1
    return 0
  })
}

// 查看文章详情
const viewArticle = (article) => {
  currentArticle.value = article
  modalVisible.value = true
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
}
</style>
