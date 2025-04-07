package com.kepu.server.repository;

import com.kepu.server.entity.Post;
import com.kepu.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 按作者查询
    Page<Post> findByAuthor(User author, Pageable pageable);
    
    // 按标题或内容模糊查询
    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    // 按位置模糊查询
    Page<Post> findByLocationContaining(String location, Pageable pageable);
} 