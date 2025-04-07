package com.kepu.server.repository;

import com.kepu.server.entity.User;
import com.kepu.server.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
    // 根据标题模糊查询
    Page<Video> findByTitleContaining(String title, Pageable pageable);
    
    // 根据上传者查询
    Page<Video> findByUploader(User uploader, Pageable pageable);
    
    // 根据状态查询
    Page<Video> findByStatus(String status, Pageable pageable);
    
    // 根据上传者和状态查询
    Page<Video> findByUploaderAndStatus(User uploader, String status, Pageable pageable);
    
    // 根据标题和状态查询
    Page<Video> findByTitleContainingAndStatus(String title, String status, Pageable pageable);
    
    // 根据标题和上传者查询
    Page<Video> findByTitleContainingAndUploader(String title, User uploader, Pageable pageable);
    
    // 根据标题、上传者和状态查询
    Page<Video> findByTitleContainingAndUploaderAndStatus(String title, User uploader, String status, Pageable pageable);
} 