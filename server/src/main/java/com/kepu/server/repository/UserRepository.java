package com.kepu.server.repository;

import com.kepu.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    // 根据用户名、姓名或邮箱模糊查询
    Page<User> findByUsernameContainingOrNameContainingOrEmailContaining(
        String username, String name, String email, Pageable pageable);
} 