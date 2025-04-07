import request from '@/utils/request'

// 添加评论
export function addComment(data) {
  return request({
    url: '/comment/add',
    method: 'post',
    data
  })
}

// 获取文章评论列表
export function getCommentList(postId) {
  return request({
    url: '/comment/list',
    method: 'get',
    params: { postId }
  })
}

// 分页获取文章评论
export function getCommentPage(postId, params) {
  return request({
    url: '/comment/page',
    method: 'get',
    params: { postId, ...params }
  })
}

// 删除评论
export function deleteComment(commentId, userId) {
  return request({
    url: '/comment/delete',
    method: 'delete',
    params: { commentId, userId }
  })
}

// 获取文章评论数
export function getCommentCount(postId) {
  return request({
    url: '/comment/count',
    method: 'get',
    params: { postId }
  })
} 