package com.kepu.server.service;

import com.kepu.server.entity.User;
import com.kepu.server.entity.Video;
import com.kepu.server.repository.UserRepository;
import com.kepu.server.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建视频
     */
    public Video createVideo(Video video, Long userId) {
        User uploader = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        video.setUploader(uploader);
        return videoRepository.save(video);
    }

    /**
     * 获取视频详情
     */
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("视频不存在"));
    }

    /**
     * 更新视频
     */
    public Video updateVideo(Video video, Long userId) {
        Video existingVideo = getVideoById(video.getId());
        
        // 检查权限，只有上传者或管理员可以修改
        checkVideoPermission(existingVideo, userId);
        
        // 更新视频信息
        if (StringUtils.hasText(video.getTitle())) {
            existingVideo.setTitle(video.getTitle());
        }
        if (StringUtils.hasText(video.getDescription())) {
            existingVideo.setDescription(video.getDescription());
        }
        if (StringUtils.hasText(video.getUrl())) {
            existingVideo.setUrl(video.getUrl());
        }
        if (StringUtils.hasText(video.getStatus())) {
            existingVideo.setStatus(video.getStatus());
        }
        
        return videoRepository.save(existingVideo);
    }

    /**
     * 删除视频
     */
    public void deleteVideo(Long videoId, Long userId) {
        Video video = getVideoById(videoId);
        
        // 检查权限，只有上传者或管理员可以删除
        checkVideoPermission(video, userId);
        
        videoRepository.delete(video);
    }

    /**
     * 获取视频列表（分页）
     */
    public Map<String, Object> getVideoList(Integer page, Integer pageSize, String title, 
                                           String status, Long uploaderId) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("uploadTime").descending());
        Page<Video> videoPage;
        
        User uploader = null;
        if (uploaderId != null) {
            uploader = userRepository.findById(uploaderId).orElse(null);
        }
        
        // 根据不同的查询参数组合进行查询
        if (StringUtils.hasText(title) && StringUtils.hasText(status) && uploader != null) {
            videoPage = videoRepository.findByTitleContainingAndUploaderAndStatus(title, uploader, status, pageable);
        } else if (StringUtils.hasText(title) && StringUtils.hasText(status)) {
            videoPage = videoRepository.findByTitleContainingAndStatus(title, status, pageable);
        } else if (StringUtils.hasText(title) && uploader != null) {
            videoPage = videoRepository.findByTitleContainingAndUploader(title, uploader, pageable);
        } else if (StringUtils.hasText(status) && uploader != null) {
            videoPage = videoRepository.findByUploaderAndStatus(uploader, status, pageable);
        } else if (StringUtils.hasText(title)) {
            videoPage = videoRepository.findByTitleContaining(title, pageable);
        } else if (StringUtils.hasText(status)) {
            videoPage = videoRepository.findByStatus(status, pageable);
        } else if (uploader != null) {
            videoPage = videoRepository.findByUploader(uploader, pageable);
        } else {
            videoPage = videoRepository.findAll(pageable);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", videoPage.getContent());
        result.put("total", videoPage.getTotalElements());
        result.put("pages", videoPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }
    
    /**
     * 更新视频状态
     */
    public Video updateVideoStatus(Long videoId, String status, Long userId) {
        Video video = getVideoById(videoId);
        
        // 检查权限，只有上传者或管理员可以更新状态
        checkVideoPermission(video, userId);
        
        video.setStatus(status);
        return videoRepository.save(video);
    }
    
    /**
     * 检查视频操作权限
     */
    private void checkVideoPermission(Video video, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 如果不是上传者且不是管理员，则无权限操作
        if (!video.getUploader().getId().equals(userId) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权限操作该视频");
        }
    }
} 