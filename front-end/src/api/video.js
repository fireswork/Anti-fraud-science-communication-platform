import request from '@/utils/request'

// 获取视频列表
export function getVideoList(params) {
  return request({
    url: '/video/list',
    method: 'get',
    params
  })
}

// 获取视频详情
export function getVideoDetail(videoId) {
  return request({
    url: '/video/detail',
    method: 'get',
    params: { videoId }
  })
}

// 上传视频
export function uploadVideo(formData) {
  return request({
    url: '/video/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 更新视频信息
export function updateVideo(data) {
  return request({
    url: '/video/update',
    method: 'put',
    data
  })
}

// 删除视频
export function deleteVideo(videoId, userId) {
  return request({
    url: '/video/delete',
    method: 'delete',
    params: { videoId, userId }
  })
}

// 更新视频状态
export function updateVideoStatus(videoId, status, userId) {
  return request({
    url: '/video/status',
    method: 'post',
    params: { videoId, status, userId }
  })
}

// 检查视频文件是否存在
export function checkVideoFile(filename) {
  return request({
    url: `/video/check/${filename}`,
    method: 'get'
  })
} 