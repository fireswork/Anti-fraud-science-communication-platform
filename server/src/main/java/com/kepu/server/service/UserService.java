package com.kepu.server.service;

import com.kepu.server.entity.User;
import com.kepu.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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
    
    // 获取用户列表（分页）
    public Map<String, Object> getUserList(Integer page, Integer pageSize, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("id").descending());
        Page<User> userPage;
        
        if (StringUtils.hasText(keyword)) {
            userPage = userRepository.findByUsernameContainingOrNameContainingOrEmailContaining(
                keyword, keyword, keyword, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        
        List<User> users = userPage.getContent();
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", users);
        result.put("total", userPage.getTotalElements());
        result.put("pages", userPage.getTotalPages());
        result.put("current", page);
        
        return result;
    }
    
    // 更新用户
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        
        // 更新基本信息
        if (StringUtils.hasText(user.getName())) {
            existingUser.setName(user.getName());
        }
        if (StringUtils.hasText(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }
        if (StringUtils.hasText(user.getPhone())) {
            existingUser.setPhone(user.getPhone());
        }
        if (StringUtils.hasText(user.getAddress())) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getBirthday() != null) {
            existingUser.setBirthday(user.getBirthday());
        }
        
        return userRepository.save(existingUser);
    }
    
    // 删除用户
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("用户不存在");
        }
        userRepository.deleteById(userId);
    }
    
    // 重置密码
    public String resetPassword(Long userId) {
        User user = getUserById(userId);
        
        // 生成6位随机密码
        String newPassword = generateRandomPassword(6);
        user.setPassword(passwordEncoder.encode(newPassword));
        
        userRepository.save(user);
        
        return newPassword;
    }
    
    // 修改密码
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("当前密码不正确");
        }
        
        // 设置新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // 生成随机密码
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
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