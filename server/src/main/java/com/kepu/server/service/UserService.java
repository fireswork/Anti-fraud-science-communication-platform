package com.kepu.server.service;

import com.kepu.server.entity.User;
import com.kepu.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户注册
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认角色为普通用户
        user.setRole("USER");
        
        return userRepository.save(user);
    }

    // 用户登录
    public User login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        
        throw new RuntimeException("用户名或密码错误");
    }
    
    // 根据ID获取用户
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 初始化管理员账号
    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("123456"));
                adminUser.setName("管理员");
                adminUser.setPhone("13800000000");
                adminUser.setEmail("admin@example.com");
                adminUser.setRole("ADMIN");
                userRepository.save(adminUser);
                System.out.println("管理员账号初始化成功");
            }
        };
    }
}