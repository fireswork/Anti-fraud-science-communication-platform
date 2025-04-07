<template>
  <div class="read-container">
    <div class="read-content">
      <div class="page-header">
        <h2>文章列表</h2>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索文章"
          style="width: 300px"
          @search="onSearch"
        />
      </div>

      <div class="article-list">
        <a-list
          :data-source="articles"
          :loading="loading"
          item-layout="vertical"
          :pagination="pagination"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <template #actions>
                <span>
                  <MessageOutlined style="margin-right: 8px" />
                  {{ item.comments }} 评论
                </span>
                <span>
                  <ClockCircleOutlined style="margin-right: 8px" />
                  {{ item.date }}
                </span>
              </template>
              <a-list-item-meta :description="item.description">
                <template #title>
                  <a @click="viewArticle(item)">{{ item.title }}</a>
                </template>
                <template #avatar>
                  <a-avatar>
                    <template #icon><UserOutlined /></template>
                  </a-avatar>
                </template>
              </a-list-item-meta>
              <div class="article-content">
                <div class="article-text">{{ item.content }}</div>
              </div>
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
        </div>

        <div class="article-body">
          {{ currentArticle.content }}
        </div>

        <div v-if="currentArticle.video_url" class="video-section">
          <div class="video-placeholder">
            <PlayCircleOutlined />
            <span>视频内容</span>
          </div>
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

          <div class="comment-form">
            <a-form-item>
              <a-textarea v-model:value="commentText" :rows="4" placeholder="写下你的评论..." />
            </a-form-item>
            <a-button type="primary" :loading="submitting" @click="handleComment">
              发表评论
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  MessageOutlined,
  ClockCircleOutlined,
  VideoCameraOutlined,
  PlayCircleOutlined,
} from '@ant-design/icons-vue'

const loading = ref(false)
const searchText = ref('')
const modalVisible = ref(false)
const commentText = ref('')
const submitting = ref(false)

// 模拟文章数据
const articles = ref([
  {
    id: 1,
    title: '防诈骗知识分享',
    author: '张三',
    description: '分享一些常见的诈骗手段和防范方法',
    content: '近年来，电信诈骗案件频发，本文将详细介绍几种常见的诈骗手段，以及相应的防范措施...',
    date: '2024-03-06',
    comments: 5,
    video_url: 'https://example.com/video1',
    commentList: [
      {
        author: '李四',
        avatar: null,
        content: '非常实用的文章，感谢分享！',
        datetime: '2024-03-06 14:30',
      },
    ],
  },
  {
    id: 2,
    title: '如何识别网络诈骗',
    author: '王五',
    description: '详细讲解网络诈骗的特征',
    content: '网络诈骗形式多样，本文将帮助大家学会识别各种网络诈骗的特征...',
    date: '2024-03-05',
    comments: 3,
    commentList: [],
  },
])

const pagination = {
  onChange: (page) => {
    console.log(page)
  },
  pageSize: 10,
}

const currentArticle = reactive({
  title: '',
  author: '',
  content: '',
  date: '',
  video_url: '',
  commentList: [],
})

const onSearch = (value) => {
  console.log('搜索:', value)
}

const viewArticle = (article) => {
  Object.assign(currentArticle, article)
  modalVisible.value = true
}

const handleModalOk = () => {
  modalVisible.value = false
}

const handleComment = () => {
  if (!commentText.value) {
    message.warning('请输入评论内容')
    return
  }

  submitting.value = true
  // 模拟提交评论
  setTimeout(() => {
    currentArticle.commentList.unshift({
      author: '当前用户',
      avatar: null,
      content: commentText.value,
      datetime: new Date().toLocaleString(),
    })
    commentText.value = ''
    submitting.value = false
    message.success('评论成功')
  }, 1000)
}
</script>

<style lang="less" scoped>
.read-container {
  min-height: 100vh;
  background-color: #f0f2f5;
  padding: 24px;
}

.read-content {
  max-width: 1000px;
  margin: 0 auto;
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;

  h2 {
    font-size: 24px;
    color: #001529;
    margin: 0;
  }
}

.article-list {
  :deep(.ant-list-item) {
    padding: 20px;
    transition: all 0.3s;

    &:hover {
      background-color: #fafafa;
    }
  }

  :deep(.ant-list-item-meta-title) {
    margin-bottom: 8px;

    a {
      color: #001529;
      font-size: 16px;
      font-weight: 500;

      &:hover {
        color: #1890ff;
      }
    }
  }
}

.article-content {
  margin-top: 16px;

  .article-text {
    color: #666;
    margin-bottom: 12px;
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

    .date {
      color: #999;
    }
  }

  .article-body {
    font-size: 14px;
    line-height: 1.8;
    color: #333;
    margin-bottom: 24px;
  }

  .video-section {
    margin: 24px 0;

    .video-placeholder {
      background: #f0f2f5;
      height: 200px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      border-radius: 8px;
      color: #999;

      .anticon {
        font-size: 32px;
        margin-bottom: 8px;
      }
    }
  }
}

.comments-section {
  h3 {
    margin-bottom: 24px;
  }

  .comment-form {
    margin-top: 24px;
  }
}
</style>
