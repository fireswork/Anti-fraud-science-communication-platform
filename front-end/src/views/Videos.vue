<template>
  <div class="videos-container">
    <div class="header-actions">
      <a-button type="primary" @click="showUploadModal">
        <template #icon><UploadOutlined /></template>
        上传视频
      </a-button>
    </div>

    <a-table
      :columns="columns"
      :data-source="videoList"
      :loading="loading"
      rowKey="id"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'title'">
          <div class="video-title">
            <video-camera-outlined />
            <span>{{ record.title }}</span>
          </div>
        </template>

        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handlePreview(record)">预览</a-button>
            <a-popconfirm title="确定要删除这个视频吗？" @confirm="handleDelete(record)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 上传视频弹窗 -->
    <a-modal
      v-model:open="uploadModalVisible"
      title="上传视频"
      @ok="handleUploadSubmit"
      :confirmLoading="uploading"
    >
      <a-form :model="uploadForm" layout="vertical">
        <a-form-item
          label="视频标题"
          name="title"
          :rules="[{ required: true, message: '请输入视频标题' }]"
        >
          <a-input v-model:value="uploadForm.title" placeholder="请输入视频标题" />
        </a-form-item>

        <a-form-item
          label="视频描述"
          name="description"
          :rules="[{ required: true, message: '请输入视频描述' }]"
        >
          <a-textarea
            v-model:value="uploadForm.description"
            :rows="4"
            placeholder="请输入视频描述"
          />
        </a-form-item>

        <a-form-item
          label="上传视频"
          name="video"
          :rules="[{ required: true, message: '请上传视频文件' }]"
        >
          <a-upload
            v-model:fileList="uploadForm.fileList"
            :beforeUpload="beforeUpload"
            :maxCount="1"
          >
            <a-button>
              <template #icon><upload-outlined /></template>
              选择视频文件
            </a-button>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 预览视频弹窗 -->
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'
import { getVideoList, uploadVideo, deleteVideo, updateVideo, getVideoDetail, checkVideoFile } from '@/api/video'
import dayjs from 'dayjs'
import 'video.js/dist/video-js.css'

// 表格列定义
const columns = [
  {
    title: '视频标题',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '视频描述',
    dataIndex: "description",
    key: "description"
  },
  {
    title: '上传时间',
    dataIndex: 'uploadTime',
    key: 'uploadTime',
    customRender: ({ text }) => formatDate(text)
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 视频列表数据
const videoList = ref([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total) => `共 ${total} 条`,
})

// 加载状态
const loading = ref(false)

// 上传相关
const uploadModalVisible = ref(false)
const uploading = ref(false)
const uploadForm = reactive({
  title: '',
  description: '',
  fileList: [],
})

// 预览相关
const previewModalVisible = ref(false)
const previewVideo = ref(null)
const videoError = ref(false)
const videoPlayer = ref(null)
const player = ref(null)
// API基础URL
const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
// 当前使用的视频URL
const currentVideoUrl = ref('')

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

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

// 事件处理
const showUploadModal = () => {
  uploadModalVisible.value = true
}

const beforeUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  if (!isVideo) {
    message.error('只能上传视频文件！')
    return false
  }
  const isLt500M = file.size / 1024 / 1024 < 500
  if (!isLt500M) {
    message.error('视频大小不能超过 500MB！')
    return false
  }
  return false // 阻止自动上传
}

const handleUploadSubmit = async () => {
  if (!uploadForm.title.trim()) {
    message.warning('请输入视频标题')
    return
  }
  
  if (uploadForm.fileList.length === 0) {
    message.warning('请选择视频文件')
    return
  }
  
  uploading.value = true
  try {
    // 获取当前登录用户ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (!userInfo.userId) {
      message.error('请先登录')
      uploading.value = false
      return
    }
    
    // 创建表单数据
    const formData = new FormData()
    formData.append('file', uploadForm.fileList[0].originFileObj)
    formData.append('title', uploadForm.title)
    formData.append('description', uploadForm.description || '')
    formData.append('userId', userInfo.userId)
    
    // 调用上传API
    const res = await uploadVideo(formData)
    
    if (res.code === 200) {
      message.success('视频上传成功')
      uploadModalVisible.value = false
      
      // 重置表单
      uploadForm.title = ''
      uploadForm.description = ''
      uploadForm.fileList = []
      
      // 刷新视频列表
      fetchVideoList()
    } else {
      message.error(res.message || '上传失败')
    }
  } catch (error) {
    console.error('上传视频失败:', error)
    message.error('上传视频失败')
  } finally {
    uploading.value = false
  }
}

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

const handleDelete = async (record) => {
  try {
    // 获取当前登录用户ID
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (!userInfo.userId) {
      message.error('请先登录')
      return
    }
    
    const res = await deleteVideo(record.id, userInfo.userId)
    if (res.code === 200) {
      message.success('删除成功')
      // 刷新视频列表
      fetchVideoList()
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除视频失败:', error)
    message.error('删除视频失败')
  }
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchVideoList()
}

const handleVideoError = (e) => {
  console.error('视频加载错误:', e)
  const video = e.target
  console.log('视频元素状态:', {
    error: video.error ? {
      code: video.error.code,
      message: video.error.message
    } : null,
    currentSrc: video.currentSrc
  })
  videoError.value = true
  message.error('视频加载失败，请稍后重试')
}

const retryVideo = () => {
  if (videoPlayer.value) {
    videoError.value = false
    videoPlayer.value.load()
  }
}

// 下载视频
const downloadVideo = () => {
  if (!previewVideo.value || !previewVideo.value.url) return;
  
  const videoUrl = `${baseUrl}${previewVideo.value.url}`;
  const link = document.createElement('a');
  link.href = videoUrl;
  link.download = previewVideo.value.title + videoUrl.substring(videoUrl.lastIndexOf('.'));
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

// 页面加载时获取视频列表
onMounted(() => {
  fetchVideoList()
})

onUnmounted(() => {
  if (player.value) {
    player.value.dispose()
  }
})
</script>

<style lang="less" scoped>
.videos-container {
  .header-actions {
    margin-bottom: 16px;
  }

  .video-title {
    display: flex;
    align-items: center;
    gap: 8px;

    .anticon {
      color: #1890ff;
    }
  }
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
  padding: 8px;
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
</style>
