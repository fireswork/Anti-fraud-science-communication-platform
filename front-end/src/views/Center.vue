<template>
  <div class="center-container">
    <AppHeader />

    <div class="page-header">
      <h1 class="title">科普中心</h1>
      <div class="subtitle">探索科学知识，防范诈骗风险</div>
    </div>

    <div class="content">
      <a-spin :spinning="loading">
        <div v-if="videoList.length > 0" class="video-grid">
          <div v-for="video in videoList" :key="video.id" class="video-card" @click="handlePreview(video)">
            <div class="video-thumbnail">
              <video-camera-outlined class="video-icon" />
              <div class="play-button">
                <play-circle-outlined />
              </div>
            </div>
            <div class="video-info">
              <div class="video-title">{{ video.title }}</div>
              <div class="video-desc">{{ video.description }}</div>
              <div class="video-meta">
                <calendar-outlined />
                <span>{{ formatDate(video.uploadTime) }}</span>
              </div>
            </div>
          </div>
        </div>
        <a-empty v-else description="暂无视频" />
      </a-spin>
      
      <div class="pagination-container">
        <a-pagination
          v-if="videoList.length > 0"
          v-model:current="pagination.current"
          :total="pagination.total"
          :pageSize="pagination.pageSize"
          @change="handlePageChange"
          show-quick-jumper
          :showTotal="total => `共 ${total} 个视频`"
        />
      </div>
    </div>

    <!-- 视频预览弹窗 -->
    <a-modal v-model:open="previewModalVisible" title="视频预览" width="800px" :footer="null">
      <template v-if="previewVideo">
        <div class="video-details">
          <h3>{{ previewVideo.title }}</h3>
          <p class="video-upload-time">上传时间：{{ formatDate(previewVideo.uploadTime) }}</p>
          <div class="video-description">{{ previewVideo.description }}</div>
        </div>
        <div class="video-player">
          <video 
            ref="videoPlayer"
            controls 
            style="width: 100%; max-height: 70vh;"
            controlsList="nodownload"
            autoplay
            :key="currentVideoUrl"
          >
            <source :src="currentVideoUrl" type="video/mp4">
            您的浏览器不支持 HTML5 视频播放
          </video>
          
          <!-- 备用播放方式 -->
          <div v-if="videoError" class="video-fallback">
            <iframe 
              :src="currentVideoUrl" 
              style="width: 100%; height: 400px; border: none;"
              allowfullscreen
            ></iframe>
          </div>
          
          <div v-if="videoError" class="video-error">
            <p>视频加载失败，请<a href="#" @click.prevent="retryVideo">重新加载</a>或者 
              <a :href="currentVideoUrl" target="_blank">在新窗口打开</a>
            </p>
            <div class="video-actions">
              <a-button type="primary" @click="downloadVideo">下载视频</a-button>
            </div>
          </div>
        </div>
      </template>
      <template v-else>
        <a-spin tip="加载中..."></a-spin>
      </template>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { 
  VideoCameraOutlined, 
  PlayCircleOutlined, 
  CalendarOutlined
} from '@ant-design/icons-vue'
import { getVideoList, getVideoDetail } from '@/api/video'
import dayjs from 'dayjs'
import AppHeader from '@/components/AppHeader.vue'

// 视频列表数据
const videoList = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 12,
  total: 0,
})

// 加载状态
const loading = ref(false)

// 视频预览相关
const previewModalVisible = ref(false)
const previewVideo = ref(null)
const videoError = ref(false)
const videoPlayer = ref(null)
const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
const currentVideoUrl = ref('')

// 获取视频列表
const fetchVideoList = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize
    }
    
    const res = await getVideoList(params)
    if (res.code === 200) {
      videoList.value = res.data.records
      pagination.total = res.data.total
    } else {
      message.error(res.message || '获取视频列表失败')
    }
  } catch (error) {
    console.error('获取视频列表失败:', error)
    message.error('获取视频列表失败')
  } finally {
    loading.value = false
  }
}

// 分页处理
const handlePageChange = (page) => {
  pagination.current = page
  fetchVideoList()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 预览视频
const handlePreview = async (record) => {
  try {
    const res = await getVideoDetail(record.id)
    if (res.code === 200) {
      previewVideo.value = res.data
      if (previewVideo.value && previewVideo.value.url) {
        // 检查URL是否已经包含完整域名
        if (previewVideo.value.url.startsWith('http')) {
          currentVideoUrl.value = previewVideo.value.url
        } else {
          currentVideoUrl.value = `${baseUrl}${previewVideo.value.url}`
        }
        console.log('完整视频URL:', currentVideoUrl.value)
        previewModalVisible.value = true
        videoError.value = false
      } else {
        message.error('视频URL不存在')
      }
    } else {
      message.error(res.message || '获取视频详情失败')
    }
  } catch (error) {
    console.error('获取视频详情失败:', error)
    message.error('获取视频详情失败')
  }
}

// 视频错误处理
const handleVideoError = (e) => {
  console.error('视频加载错误:', e)
  videoError.value = true
  message.error('视频加载失败，请稍后重试')
}

// 重试加载视频
const retryVideo = () => {
  if (videoPlayer.value) {
    videoError.value = false
    videoPlayer.value.load()
  }
}

// 下载视频
const downloadVideo = () => {
  if (!previewVideo.value || !previewVideo.value.url) return;
  
  const videoUrl = currentVideoUrl.value;
  const link = document.createElement('a');
  link.href = videoUrl;
  link.download = previewVideo.value.title + '.mp4';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

// 页面加载时获取视频列表
onMounted(() => {
  fetchVideoList()
})
</script>

<style lang="less" scoped>
.center-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.page-header {
  padding: 40px 0;
  text-align: center;
  background: linear-gradient(135deg, #1890ff, #722ed1);
  color: #fff;
  margin-bottom: 40px;
  
  .title {
    font-size: 32px;
    font-weight: bold;
    margin-bottom: 8px;
  }
  
  .subtitle {
    font-size: 16px;
    opacity: 0.8;
  }
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px 40px;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.video-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
    
    .play-button {
      opacity: 1;
    }
  }
  
  .video-thumbnail {
    position: relative;
    height: 180px;
    background: #f6f6f6;
    display: flex;
    justify-content: center;
    align-items: center;
    
    .video-icon {
      font-size: 50px;
      color: #1890ff;
      opacity: 0.5;
    }
    
    .play-button {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-size: 48px;
      color: #fff;
      opacity: 0;
      transition: opacity 0.3s ease;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      padding: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
    }
  }
  
  .video-info {
    padding: 16px;
    
    .video-title {
      font-size: 16px;
      font-weight: bold;
      margin-bottom: 8px;
      color: #262626;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    .video-desc {
      font-size: 14px;
      color: #595959;
      margin-bottom: 12px;
      height: 40px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    
    .video-meta {
      font-size: 12px;
      color: #8c8c8c;
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

// 视频预览样式
.video-details {
  margin-bottom: 16px;
  
  h3 {
    margin-bottom: 8px;
    font-size: 18px;
    font-weight: 600;
  }
  
  .video-upload-time {
    margin-bottom: 8px;
    color: #666;
    font-size: 14px;
  }
  
  .video-description {
    margin-bottom: 16px;
    white-space: pre-line;
    color: #333;
  }
}

.video-player {
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
  
  video {
    display: block;
    width: 100%;
    max-height: 450px;
    outline: none;
  }
}

.video-error {
  margin-top: 16px;
  padding: 16px;
  background-color: #fff;
  border: 1px solid #ffa39e;
  border-radius: 4px;
  color: #ff4d4f;

  p {
    margin: 0;
  }

  a {
    color: #1890ff;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.video-actions {
  margin-top: 16px;
  text-align: right;
}

@media (max-width: 768px) {
  .video-grid {
    grid-template-columns: 1fr 1fr;
  }
  
  .page-header {
    padding: 30px 0;
    
    .title {
      font-size: 24px;
    }
  }
}

@media (max-width: 480px) {
  .video-grid {
    grid-template-columns: 1fr;
  }
}
</style> 