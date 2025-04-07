package com.kepu.server.repository;

import com.kepu.server.entity.Comment;
import com.kepu.server.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 根据文章ID查询评论列表
    List<Comment> findByPostOrderByCreateTimeDesc(Post post);
    
    // 根据文章ID分页查询评论
    Page<Comment> findByPost(Post post, Pageable pageable);
    
    // 统计文章的评论数
    long countByPost(Post post);
} 