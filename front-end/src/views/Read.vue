<template>
  <div class="read-container">
    <AppHeader />
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
                  <EnvironmentOutlined style="margin-right: 8px" />
                  {{ item.location }}
                </span>
                <span>
                  <ClockCircleOutlined style="margin-right: 8px" />
                  {{ formatDate(item.createTime) }}
                </span>
                <span @click.stop="viewArticleComments(item)">
                  <MessageOutlined style="margin-right: 8px" />
                  {{ item.commentCount || 0 }} 评论
                </span>
                <span v-if="isCurrentUserAuthor(item)">
                  <a-popconfirm
                    title="确定要删除这篇文章吗？"
                    ok-text="确定"
                    cancel-text="取消"
                    @confirm="handleDeletePost(item)"
                  >
                    <DeleteOutlined style="margin-right: 8px; color: #ff4d4f;" />
                    删除
                  </a-popconfirm>
                </span>
              </template>
              <a-list-item-meta>
                <template #title>
                  <a @click="viewArticle(item)">{{ item.title }}</a>
                </template>
                <template #avatar>
                  <a-avatar>
                    <template #icon><UserOutlined /></template>
                  </a-avatar>
                </template>
                <template #description>
                  <span>作者: {{ item.author ? item.author.name : '未知' }}</span>
                </template>
              </a-list-item-meta>
              <div class="article-content">
                <div class="article-text">{{ truncateText(item.content, 150) }}</div>
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
      <template #footer>
        <div class="modal-footer">
          <a-button key="close" @click="handleModalOk">关闭</a-button>
          <a-popconfirm
            v-if="isCurrentUserAuthor(currentArticle)"
            title="确定要删除这篇文章吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="handleDeleteCurrentArticle"
          >
            <a-button key="delete" danger><DeleteOutlined />删除文章</a-button>
          </a-popconfirm>
        </div>
      </template>
      <div class="article-detail">
        <div class="article-info">
          <a-avatar>
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <span class="author">{{ currentArticle.author ? currentArticle.author.name : '未知' }}</span>
          <span class="date">{{ formatDate(currentArticle.createTime) }}</span>
        </div>

        <div class="article-body">
          {{ currentArticle.content }}
        </div>

        <div v-if="currentArticle.location" class="location-section">
          <div class="location-info">
            <EnvironmentOutlined />
            <span>{{ currentArticle.location }}</span>
          </div>
          <div id="map-container" class="map-container"></div>
        </div>

        <a-divider />

        <!-- 评论部分 -->
        <div class="comments-section">
          <div class="comments-header">
            <h3>评论 ({{ commentTotal }})</h3>
            <!-- 添加评论按钮，当已登录时显示 -->
            <a-button 
              v-if="currentUser" 
              type="primary" 
              @click="toggleCommentForm"
            >
              <template #icon><MessageOutlined /></template>
              发表评论
            </a-button>
            <!-- 未登录时显示登录提示 -->
            <a-button
              v-else
              @click="goToLogin"
            >
              <template #icon><UserOutlined /></template>
              登录后评论
            </a-button>
          </div>
          
          <!-- 发表评论 -->
          <div class="comment-form" v-if="showCommentForm && currentUser">
            <a-form-item>
              <a-textarea 
                v-model:value="commentText" 
                :rows="4" 
                placeholder="写下你的评论..."
                :maxLength="500"
                show-count
              />
            </a-form-item>
            <div class="comment-form-actions">
              <a-button @click="toggleCommentForm">取消</a-button>
              <a-button 
                type="primary" 
                :loading="submitting" 
                @click="handleSubmitComment"
              >
                发表评论
              </a-button>
            </div>
          </div>
          
          <!-- 评论列表 -->
          <a-list
            class="comment-list"
            :loading="commentsLoading"
            :data-source="comments"
            item-layout="horizontal"
            :pagination="commentPagination"
          >
            <template #empty>
              <div class="no-comment">
                暂无评论，
                <a v-if="currentUser" @click="toggleCommentForm">快来发表第一条评论吧！</a>
                <span v-else>登录后发表第一条评论吧！</span>
              </div>
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
                  <template #actions v-if="canDeleteComment(item)">
                    <a-popconfirm
                      title="确定要删除这条评论吗？"
                      ok-text="确定"
                      cancel-text="取消"
                      @confirm="handleDeleteComment(item)"
                    >
                      <span class="comment-action">删除</span>
                    </a-popconfirm>
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
import { ref, reactive, onMounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  ClockCircleOutlined,
  EnvironmentOutlined,
  DeleteOutlined,
  MessageOutlined
} from '@ant-design/icons-vue'
import AppHeader from '@/components/AppHeader.vue'
import { getPostList, getPostDetail, deletePost } from '@/api/post'
import { getCommentList, addComment, deleteComment, getCommentCount } from '@/api/comment'
import dayjs from 'dayjs'
import { useRouter } from 'vue-router'

const router = useRouter()

// 高德地图Key
const AMAP_KEY = 'b6d79262c73c4413d9e82736c346f2a2'
const AMAP_API_URL = 'https://webapi.amap.com/maps?v=2.0&key=' + AMAP_KEY
const AMAP_SECURITY_CONFIG = {
  securityJsCode: '5077eda0e025be5a95ffaea82fff34cf',
}

// 配置安全密钥
window._AMapSecurityConfig = AMAP_SECURITY_CONFIG

const loading = ref(false)
const searchText = ref('')
const modalVisible = ref(false)
const articles = ref([])
let map = null

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  onChange: (page) => {
    pagination.current = page
    fetchArticles()
  },
})

const currentArticle = reactive({
  id: null,
  title: '',
  author: null,
  content: '',
  location: '',
  longitude: null,
  latitude: null,
  createTime: null,
})

// 当前用户信息
const currentUser = ref(null)

// 评论相关状态
const commentText = ref('')
const submitting = ref(false)
const comments = ref([])
const commentsLoading = ref(false)
const commentTotal = ref(0)

// 评论分页
const commentPagination = reactive({
  onChange: (page) => {
    fetchComments(currentArticle.id, page)
  },
  pageSize: 10
})

// 检查文章作者是否为当前登录用户
const isCurrentUserAuthor = (post) => {
  if (!currentUser.value || !post.author) return false
  return post.author.id === currentUser.value.userId
}

// 获取当前用户信息
const getCurrentUser = () => {
  try {
    // 尝试从localStorage获取用户信息，可能有不同的key
    let userInfo = JSON.parse(localStorage.getItem('userInfo'))
    
    // 如果未找到，尝试其他可能的key
    if (!userInfo || !userInfo.userId) {
      userInfo = JSON.parse(localStorage.getItem('userInfo'))
    }
    
    if (userInfo && (userInfo.userId || userInfo.id)) {
      currentUser.value = {
        userId: userInfo.userId || userInfo.id,
        username: userInfo.username,
        role: userInfo.role
      }
    }
    console.log(currentUser.value, 222)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      pageSize: pagination.pageSize,
      keyword: searchText.value || undefined,
    }
    
    const res = await getPostList(params)
    if (res.code === 200) {
      // 获取到文章列表
      const articleList = res.data.records;
      
      // 获取每篇文章的评论数
      for (const article of articleList) {
        try {
          const countRes = await getCommentCount(article.id);
          if (countRes.code === 200) {
            article.commentCount = countRes.data;
          }
        } catch (error) {
          console.error('获取评论数失败:', error);
          article.commentCount = 0;
        }
      }
      
      articles.value = articleList;
      pagination.total = res.data.total;
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

// 截断文本
const truncateText = (text, length) => {
  if (!text) return ''
  if (text.length <= length) return text
  return text.substring(0, length) + '...'
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 搜索文章
const onSearch = (value) => {
  searchText.value = value
  pagination.current = 1
  fetchArticles()
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
const initMap = async (longitude, latitude) => {
  try {
    await loadAMapScript()
    
    if (map) {
      map.destroy()
    }
    
    map = new AMap.Map('map-container', {
      zoom: 14,
      center: [longitude, latitude],
    })
    
    // 添加标记
    new AMap.Marker({
      position: [longitude, latitude],
      map: map,
    })
    
  } catch (error) {
    console.error('地图加载失败:', error)
  }
}

// 获取文章评论
const fetchComments = async (postId, page = 1) => {
  if (!postId) return
  
  commentsLoading.value = true
  try {
    const res = await getCommentList(postId)
    if (res.code === 200) {
      comments.value = res.data
      commentTotal.value = res.data.length
      
      // 同时更新文章列表中对应文章的评论数
      const articleIndex = articles.value.findIndex(a => a.id === postId)
      if (articleIndex !== -1) {
        articles.value[articleIndex].commentCount = res.data.length
      }
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

// 是否显示评论表单
const showCommentForm = ref(false)

// 切换评论表单显示/隐藏
const toggleCommentForm = () => {
  showCommentForm.value = !showCommentForm.value
  if (showCommentForm.value) {
    // 如果显示评论表单，则聚焦
    nextTick(() => {
      const textarea = document.querySelector('.comment-form .ant-textarea')
      if (textarea) {
        textarea.focus()
      }
    })
  }
}

// 跳转到登录页
const goToLogin = () => {
  router.push('/login')
}

// 修改提交评论方法
const handleSubmitComment = async () => {
  if (!commentText.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  
  if (!currentUser.value) {
    message.warning('请先登录后再发表评论')
    return
  }
  
  submitting.value = true
  try {
    const commentData = {
      postId: currentArticle.id,
      userId: currentUser.value.userId,
      content: commentText.value
    }
    
    const res = await addComment(commentData)
    if (res.code === 200) {
      message.success('评论发表成功')
      commentText.value = ''
      showCommentForm.value = false // 隐藏评论表单
      // 重新获取评论列表
      fetchComments(currentArticle.id)
      
      // 更新当前文章的评论数
      const articleIndex = articles.value.findIndex(a => a.id === currentArticle.id)
      if (articleIndex !== -1) {
        articles.value[articleIndex].commentCount = (articles.value[articleIndex].commentCount || 0) + 1
      }
    } else {
      message.error(res.message || '评论发表失败')
    }
  } catch (error) {
    console.error('评论发表失败:', error)
    message.error('评论发表失败')
  } finally {
    submitting.value = false
  }
}

// 判断是否可以删除评论
const canDeleteComment = (comment) => {
  if (!currentUser.value) return false
  
  // 评论作者或文章作者可以删除评论
  return (
    comment.author && comment.author.id === currentUser.value.userId || 
    (currentArticle.author && currentArticle.author.id === currentUser.value.userId)
  )
}

// 修改删除评论的方法
const handleDeleteComment = async (comment) => {
  if (!currentUser.value) {
    message.warning('请先登录')
    return
  }
  
  try {
    const res = await deleteComment(comment.id, currentUser.value.userId)
    if (res.code === 200) {
      message.success('评论删除成功')
      
      // 重新获取评论列表
      fetchComments(currentArticle.id)
      
      // 更新当前文章的评论数
      const articleIndex = articles.value.findIndex(a => a.id === currentArticle.id)
      if (articleIndex !== -1) {
        const newCount = Math.max(0, (articles.value[articleIndex].commentCount || 0) - 1)
        articles.value[articleIndex].commentCount = newCount
      }
      
      // 更新评论总数
      commentTotal.value = Math.max(0, commentTotal.value - 1)
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除评论失败:', error)
    message.error('删除评论失败')
  }
}

// 查看文章详情
const viewArticle = async (article) => {
  try {
    loading.value = true
    const res = await getPostDetail(article.id)
    
    if (res.code === 200) {
      Object.assign(currentArticle, res.data)
      modalVisible.value = true
      
      // 获取文章评论
      fetchComments(currentArticle.id)
      
      // 等待DOM更新后初始化地图
      if (currentArticle.longitude && currentArticle.latitude) {
        nextTick(() => {
          initMap(currentArticle.longitude, currentArticle.latitude)
        })
      }
    } else {
      message.error(res.message || '获取文章详情失败')
    }
  } catch (error) {
    console.error('获取文章详情失败:', error)
    message.error('获取文章详情失败')
  } finally {
    loading.value = false
  }
}

const handleModalOk = () => {
  modalVisible.value = false
  if (map) {
    map.destroy()
    map = null
  }
}

// 删除文章
const handleDeletePost = async (post) => {
  if (!currentUser.value) {
    message.error('请先登录')
    return
  }
  
  try {
    const res = await deletePost(post.id, currentUser.value.userId)
    if (res.code === 200) {
      message.success('文章删除成功')
      // 刷新文章列表
      fetchArticles()
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除文章失败:', error)
    message.error('删除文章失败')
  }
}

// 删除当前查看的文章
const handleDeleteCurrentArticle = async () => {
  if (!currentUser.value) {
    message.error('请先登录')
    return
  }
  
  try {
    const res = await deletePost(currentArticle.id, currentUser.value.userId)
    if (res.code === 200) {
      message.success('文章删除成功')
      modalVisible.value = false
      // 刷新文章列表
      fetchArticles()
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除文章失败:', error)
    message.error('删除文章失败')
  }
}

// 添加直接查看评论的方法
const viewArticleComments = async (article) => {
  await viewArticle(article);
  // 滚动到评论区
  nextTick(() => {
    const commentsSection = document.querySelector('.comments-section');
    if (commentsSection) {
      commentsSection.scrollIntoView({ behavior: 'smooth' });
    }
  });
}

// 组件挂载时获取文章列表和用户信息
onMounted(() => {
  getCurrentUser()
  fetchArticles()
})
</script>

<style lang="less" scoped>
.read-container {
  min-height: 100vh;
  background-color: #f0f2f5;
}

.read-content {
  max-width: 1000px;
  margin: 40px auto;
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

  .location-section {
    margin: 24px 0;

    .location-info {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      color: #666;
    }

    .map-container {
      height: 300px;
      border-radius: 8px;
      overflow: hidden;
    }
  }
}

.modal-footer {
  display: flex;
  justify-content: space-between;
}

.comments-section {
  margin-top: 24px;
  
  .comments-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    
    h3 {
      margin: 0;
      font-size: 18px;
    }
  }
  
  .comment-form {
    margin-bottom: 24px;
    
    :deep(.ant-form-item) {
      margin-bottom: 12px;
    }
  }
  
  .comment-tip {
    margin-bottom: 24px;
  }
  
  .comment-list {
    margin-top: 24px;
    
    :deep(.ant-comment-actions) {
      margin-top: 0;
    }
    
    .comment-action {
      color: #999;
      
      &:hover {
        color: #1890ff;
      }
    }
  }
  
  .no-comment {
    text-align: center;
    color: #999;
    padding: 20px 0;
  }
}

.comment-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.no-comment {
  text-align: center;
  color: #999;
  padding: 20px 0;
  
  a {
    color: #1890ff;
    cursor: pointer;
  }
}
</style>
