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

        <template v-if="column.key === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>

        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" @click="handlePreview(record)">预览</a-button>
            <a-button type="link" @click="handleEdit(record)">编辑</a-button>
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
      <video v-if="previewVideo" :src="previewVideo.url" controls style="width: 100%"></video>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import { UploadOutlined, VideoCameraOutlined } from '@ant-design/icons-vue'

// 表格列定义
const columns = [
  {
    title: '视频标题',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '上传时间',
    dataIndex: 'uploadTime',
    key: 'uploadTime',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '操作',
    key: 'action',
  },
]

// 模拟视频数据
const videoList = ref([
  {
    id: 1,
    title: '防范电信诈骗指南',
    uploadTime: '2024-03-15 14:30',
    status: 'published',
    url: 'https://example.com/video1.mp4',
  },
  {
    id: 2,
    title: '网络购物防骗技巧',
    uploadTime: '2024-03-14 16:45',
    status: 'processing',
    url: 'https://example.com/video2.mp4',
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

// 状态处理
const getStatusColor = (status) => {
  const colors = {
    published: 'success',
    processing: 'processing',
    failed: 'error',
  }
  return colors[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    published: '已发布',
    processing: '处理中',
    failed: '上传失败',
  }
  return texts[status] || '未知状态'
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

const handleUploadSubmit = () => {
  uploading.value = true
  // 模拟上传过程
  setTimeout(() => {
    uploading.value = false
    uploadModalVisible.value = false
    message.success('视频上传成功')
    // 重置表单
    uploadForm.title = ''
    uploadForm.description = ''
    uploadForm.fileList = []
  }, 2000)
}

const handlePreview = (record) => {
  previewVideo.value = record
  previewModalVisible.value = true
}

const handleEdit = (record) => {
  console.log('编辑视频:', record)
  message.info('编辑功能开发中')
}

const handleDelete = (record) => {
  console.log('删除视频:', record)
  message.success('删除成功')
}

const handleTableChange = (pag) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  // 这里可以调用接口重新加载数据
}
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
</style>
