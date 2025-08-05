package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.mock.UserMockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 用户Mock数据控制器
 * 提供Mock数据的REST API接口
 */
@RestController
@RequestMapping("/api/mock/users")
@RequiredArgsConstructor
public class UserMockController {
    
    private final UserMockService userMockService;
    
    /**
     * 获取所有Mock用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllMockUsers() {
        List<User> users = userMockService.getAllMockUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取Mock用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getMockUserById(@PathVariable Long id) {
        Optional<User> user = userMockService.getMockUserById(id);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户名获取Mock用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getMockUserByUsername(@PathVariable String username) {
        Optional<User> user = userMockService.getMockUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据状态获取Mock用户列表
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<User>> getMockUsersByStatus(@PathVariable UserStatus status) {
        List<User> users = userMockService.getMockUsersByStatus(status);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 生成新的Mock用户
     */
    @PostMapping("/generate")
    public ResponseEntity<User> generateMockUser() {
        User user = userMockService.generateMockUser();
        User createdUser = userMockService.createMockUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    /**
     * 生成指定数量的Mock用户
     */
    @PostMapping("/generate/{count}")
    public ResponseEntity<List<User>> generateMockUsers(@PathVariable int count) {
        if (count <= 0 || count > 100) {
            return ResponseEntity.badRequest().build();
        }
        
        List<User> users = userMockService.generateMockUsers(count);
        users.forEach(userMockService::createMockUser);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 创建Mock用户
     */
    @PostMapping
    public ResponseEntity<User> createMockUser(@RequestBody User user) {
        try {
            User createdUser = userMockService.createMockUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新Mock用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateMockUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userMockService.updateMockUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 删除Mock用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMockUser(@PathVariable Long id) {
        boolean deleted = userMockService.deleteMockUser(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * 检查用户名是否存在
     */
    @GetMapping("/check/username/{username}")
    public ResponseEntity<Boolean> checkUsernameExists(@PathVariable String username) {
        boolean exists = userMockService.mockUsernameExists(username);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * 检查邮箱是否存在
     */
    @GetMapping("/check/email/{email}")
    public ResponseEntity<Boolean> checkEmailExists(@PathVariable String email) {
        boolean exists = userMockService.mockEmailExists(email);
        return ResponseEntity.ok(exists);
    }
    
    /**
     * 模拟用户登录验证
     */
    @PostMapping("/validate-login")
    public ResponseEntity<Boolean> validateLogin(@RequestBody LoginRequest request) {
        boolean valid = userMockService.mockValidateLogin(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(valid);
    }
    
    /**
     * 获取Mock数据统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<UserMockService.MockDataStats> getMockDataStats() {
        UserMockService.MockDataStats stats = userMockService.getMockDataStats();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 重新初始化Mock数据
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetMockData() {
        userMockService.initializeDefaultMockData();
        return ResponseEntity.ok("Mock data has been reset successfully");
    }
    
    /**
     * 清空所有Mock数据
     */
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearMockData() {
        userMockService.clearAllMockData();
        return ResponseEntity.ok("All mock data has been cleared");
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