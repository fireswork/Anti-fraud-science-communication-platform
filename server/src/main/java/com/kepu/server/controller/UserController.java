package com.kepu.server.controller;

import com.kepu.server.dto.ResponseResult;
import com.kepu.server.entity.User;
import com.kepu.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
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
            
            System.out.println("尝试登录: username=" + username);
            
            User user = userService.login(username, password);
            // 返回结果不包含密码
            user.setPassword(null);
            
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("name", user.getName());
            data.put("role", user.getRole());
            
            System.out.println("登录成功，返回数据: " + data);
            
            ResponseResult result = ResponseResult.success("登录成功", data);
            System.out.println("完整响应对象: " + result);
            
            return result;
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    @GetMapping("/info")
    public ResponseResult getUserInfo(@RequestParam String userId) {
        try {
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
    
    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public ResponseResult getUserList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            Map<String, Object> result = userService.getUserList(page, pageSize, keyword);
            return ResponseResult.success(result);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    /**
     * 添加用户
     */
    @PostMapping("/add")
    public ResponseResult addUser(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            newUser.setPassword(null);
            return ResponseResult.success("用户添加成功", newUser);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PutMapping("/update")
    public ResponseResult updateUser(@RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user);
            updatedUser.setPassword(null);
            return ResponseResult.success("用户更新成功", updatedUser);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/delete")
    public ResponseResult deleteUser(@RequestParam Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseResult.success("用户删除成功", null);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseResult resetPassword(@RequestParam Long userId) {
        try {
            String newPassword = userService.resetPassword(userId);
            Map<String, String> data = new HashMap<>();
            data.put("newPassword", newPassword);
            return ResponseResult.success("密码重置成功", data);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ResponseResult changePassword(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.parseLong(params.get("userId").toString());
            String oldPassword = params.get("oldPassword").toString();
            String newPassword = params.get("newPassword").toString();
            
            userService.changePassword(userId, oldPassword, newPassword);
            return ResponseResult.success("密码修改成功", null);
        } catch (Exception e) {
            return ResponseResult.fail(400, e.getMessage());
        }
    }
}