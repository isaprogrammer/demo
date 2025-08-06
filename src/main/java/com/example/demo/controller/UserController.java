package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据库操作控制器
 * 提供真实Hibernate数据库的REST API接口
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据邮箱获取用户
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据状态获取用户列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = userService.getUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据姓名模糊搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 更新用户状态
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        try {
            User updatedUser = userService.updateUserStatus(id, request.getStatus());
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * 验证用户登录
     */
    @PostMapping("/validate-login")
    public ResponseEntity<Boolean> validateLogin(@RequestBody LoginRequest request) {
        boolean valid = userService.validatePassword(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(valid);
    }
    
    /**
     * 获取活跃用户数量
     */
    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveUserCount() {
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
    
    /**
     * 根据用户名或邮箱查找用户
     */
    @GetMapping("/find/{identifier}")
    public ResponseEntity<User> findByUsernameOrEmail(@PathVariable String identifier) {
        Optional<User> user = userService.findByUsernameOrEmail(identifier);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 状态更新请求DTO
     */
    public static class StatusUpdateRequest {
        private UserStatus status;
        
        public UserStatus getStatus() { return status; }
        public void setStatus(UserStatus status) { this.status = status; }
    }
    
    /**
     * 登录请求DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}