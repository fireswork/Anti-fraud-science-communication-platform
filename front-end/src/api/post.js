import request from '@/utils/request'

// 创建文章
export function createPost(data) {
  return request({
    url: '/post/create',
    method: 'post',
    data
  })
}

// 获取文章详情
export function getPostDetail(postId) {
  return request({
    url: '/post/detail',
    method: 'get',
    params: { postId }
  })
}

// 更新文章
export function updatePost(data) {
  return request({
    url: '/post/update',
    method: 'put',
    data
  })
}

// 删除文章
export function deletePost(postId, userId) {
  return request({
    url: '/post/delete',
    method: 'delete',
    params: { postId, userId }
  })
}

// 获取文章列表
export function getPostList(params) {
  return request({
    url: '/post/list',
    method: 'get',
    params
  })
}

// 获取用户的文章列表
export function getUserPosts(userId, params) {
  return request({
    url: '/post/user',
    method: 'get',
    params: { userId, ...params }
  })
}

// 按位置搜索文章
export function searchPostsByLocation(location, params) {
  return request({
    url: '/post/search/location',
    method: 'get',
    params: { location, ...params }
  })
} 