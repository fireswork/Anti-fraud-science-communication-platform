package com.kepu.server.controller;

import com.kepu.server.dto.ResponseResult;
import com.kepu.server.entity.User;
import com.kepu.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user);
            // 返回结果不包含密码
            registeredUser.setPassword(null);
            return ResponseResult.success("注册成功", registeredUser);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseResult login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            User user = userService.login(username, password);
            // 返回结果不包含密码
            user.setPassword(null);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("role", user.getRole());
            
            return ResponseResult.success("登录成功", data);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public ResponseResult getUserInfo(@RequestParam String userId) {
        try {
            // TODO: 根据用户ID获取用户信息，这里需要实现
            // 简单示例，实际中应该从数据库获取
            User user = userService.getUserById(Long.parseLong(userId));
            if (user != null) {
                user.setPassword(null);
                return ResponseResult.success(user);
            }
            return ResponseResult.fail(404, "用户不存在");
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    public ResponseResult logout() {
        // 前端清除localStorage中的用户信息即可
        return ResponseResult.success("已退出登录", null);
    }
}