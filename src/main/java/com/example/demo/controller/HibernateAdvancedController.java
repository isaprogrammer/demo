package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.AdvancedHibernateService;
import com.example.demo.repository.AdvancedUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * Hibernate高级功能演示控制器
 */
@RestController
@RequestMapping("/api/hibernate-advanced")
public class HibernateAdvancedController {
    
    @Autowired
    private AdvancedHibernateService hibernateService;
    
    @Autowired
    private AdvancedUserRepository userRepository;
    
    /**
     * 演示Hibernate的各种高级功能
     */
    @GetMapping("/demo")
    public ResponseEntity<String> demonstrateHibernateFeatures() {
        StringBuilder result = new StringBuilder();
        result.append("=== Hibernate高级功能演示 ===\n\n");
        
        try {
            // 1. 实体关联和缓存演示
            result.append("1. 创建测试数据...\n");
            User testUser = createTestUser();
            result.append("   创建用户: ").append(testUser.getUsername()).append("\n");
            
            // 2. 缓存操作演示
            result.append("\n2. 缓存操作演示...\n");
            Optional<User> cachedUser = hibernateService.getUserWithCache(testUser.getId());
            result.append("   从缓存获取用户: ").append(cachedUser.isPresent()).append("\n");
            
            // 3. 复杂查询演示
            result.append("\n3. 复杂查询演示...\n");
            List<User> usersWithRoles = userRepository.findUsersWithMultipleRoles(User.UserStatus.ACTIVE, 0);
            result.append("   查询有角色的用户数量: ").append(usersWithRoles.size()).append("\n");
            
            // 4. Criteria API查询
            result.append("\n4. Criteria API查询...\n");
            List<User> criteriaUsers = hibernateService.findUsersByCriteria("test", null, User.UserStatus.ACTIVE);
            result.append("   Criteria查询结果数量: ").append(criteriaUsers.size()).append("\n");
            
            // 5. 分页查询
            result.append("\n5. 分页查询演示...\n");
            Page<User> userPage = hibernateService.findUsersWithPagination(0, 10, "createdAt", "desc");
            result.append("   分页查询总数: ").append(userPage.getTotalElements()).append("\n");
            result.append("   当前页数据量: ").append(userPage.getContent().size()).append("\n");
            
            // 6. 统计查询
            result.append("\n6. 统计查询演示...\n");
            List<Object[]> statusStats = userRepository.getUserStatusStatistics();
            result.append("   用户状态统计: ");
            for (Object[] stat : statusStats) {
                result.append(stat[0]).append("=").append(stat[1]).append(" ");
            }
            result.append("\n");
            
            // 7. 批处理操作
            result.append("\n7. 批处理操作演示...\n");
            List<User> batchUsers = createBatchTestUsers(5);
            hibernateService.batchCreateUsers(batchUsers);
            result.append("   批量创建用户数量: ").append(batchUsers.size()).append("\n");
            
            // 8. 事务管理演示
            result.append("\n8. 事务管理演示...\n");
            User transactionUser = new User();
            transactionUser.setUsername("transaction_user");
            transactionUser.setEmail("transaction@example.com");
            transactionUser.setFullName("Transaction User");
            User savedTransactionUser = hibernateService.createUserWithTransaction(transactionUser);
            result.append("   事务中创建用户: ").append(savedTransactionUser.getUsername()).append("\n");
            
            // 9. 懒加载演示
            result.append("\n9. 懒加载演示...\n");
            User lazyUser = hibernateService.getUserWithLazyLoading(testUser.getId());
            if (lazyUser != null) {
                result.append("   懒加载用户角色数量: ").append(lazyUser.getRoles().size()).append("\n");
                result.append("   懒加载用户档案数量: ").append(lazyUser.getUserProfiles().size()).append("\n");
            }
            
            // 10. 原生SQL查询
            result.append("\n10. 原生SQL查询演示...\n");
            List<Object[]> userStats = hibernateService.getUserStatistics();
            result.append("   用户统计数据行数: ").append(userStats.size()).append("\n");
            
            // 11. EntityGraph查询
            result.append("\n11. EntityGraph查询演示...\n");
            Optional<User> userWithGraph = userRepository.findByIdWithRolesAndPermissions(testUser.getId());
            result.append("   EntityGraph查询结果: ").append(userWithGraph.isPresent()).append("\n");
            
            // 12. 过滤器演示
            result.append("\n12. Hibernate过滤器演示...\n");
            hibernateService.demonstrateHibernateSession();
            result.append("   过滤器演示完成\n");
            
            // 13. 复杂业务逻辑
            result.append("\n13. 复杂业务逻辑演示...\n");
            createTestOrdersForUser(testUser);
            hibernateService.processUserOrders(testUser.getId());
            result.append("   用户订单处理完成\n");
            
            result.append("\n=== 演示完成 ===\n");
            
        } catch (Exception e) {
            result.append("\n错误: ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result.toString());
    }
    
    /**
     * 缓存操作API
     */
    @GetMapping("/cache/user/{id}")
    public ResponseEntity<Optional<User>> getUserFromCache(@PathVariable Long id) {
        return ResponseEntity.ok(hibernateService.getUserWithCache(id));
    }
    
    @DeleteMapping("/cache/user/{id}")
    public ResponseEntity<String> evictUserFromCache(@PathVariable Long id) {
        hibernateService.evictUserFromCache(id);
        return ResponseEntity.ok("用户缓存已清除");
    }
    
    @DeleteMapping("/cache/users/all")
    public ResponseEntity<String> clearAllUserCache() {
        hibernateService.clearAllUserCache();
        return ResponseEntity.ok("所有用户缓存已清除");
    }
    
    /**
     * 复杂查询API
     */
    @GetMapping("/users/search")
    public ResponseEntity<Page<User>> searchUsers(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) User.UserStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> result = hibernateService.findUsersWithSpecification(username, email, status, pageable);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/users/criteria")
    public ResponseEntity<List<User>> findUsersByCriteria(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) User.UserStatus status) {
        
        List<User> users = hibernateService.findUsersByCriteria(username, email, status);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/with-roles")
    public ResponseEntity<List<User>> getUsersWithMultipleRoles() {
        List<User> users = userRepository.findUsersWithMultipleRoles(User.UserStatus.ACTIVE, 0);
        return ResponseEntity.ok(users);
    }
    
    /**
     * 统计查询API
     */
    @GetMapping("/statistics/users")
    public ResponseEntity<List<Object[]>> getUserStatistics() {
        List<Object[]> stats = hibernateService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/statistics/activity")
    public ResponseEntity<Object[]> getUserActivityStatistics() {
        Object[] stats = hibernateService.getUserActivityStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 批处理API
     */
    @PostMapping("/batch/users")
    public ResponseEntity<String> batchCreateUsers(@RequestParam(defaultValue = "10") int count) {
        List<User> users = createBatchTestUsers(count);
        hibernateService.batchCreateUsers(users);
        return ResponseEntity.ok("批量创建了 " + count + " 个用户");
    }
    
    @PutMapping("/batch/status")
    public ResponseEntity<String> batchUpdateUserStatus(
            @RequestParam User.UserStatus oldStatus,
            @RequestParam User.UserStatus newStatus) {
        
        int updatedCount = hibernateService.batchUpdateUserStatus(oldStatus, newStatus);
        return ResponseEntity.ok("更新了 " + updatedCount + " 个用户的状态");
    }
    
    // 辅助方法
    private User createTestUser() {
        User user = new User();
        user.setUsername("hibernate_test_" + System.currentTimeMillis());
        user.setEmail("hibernate_test@example.com");
        user.setFullName("Hibernate Test User");
        user.setPhone("1234567890");
        user.setStatus(User.UserStatus.ACTIVE);
        
        // 创建用户档案
        UserProfile profile = new UserProfile();
        profile.setProfileType("PREFERENCE");
        profile.setProfileKey("theme");
        profile.setProfileValue("dark");
        profile.setDescription("用户主题偏好");
        user.addUserProfile(profile);
        
        return hibernateService.createUserWithTransaction(user);
    }
    
    private List<User> createBatchTestUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("batch_user_" + i + "_" + System.currentTimeMillis());
            user.setEmail("batch_user_" + i + "@example.com");
            user.setFullName("Batch User " + i);
            user.setPhone("123456789" + i);
            user.setStatus(User.UserStatus.ACTIVE);
            users.add(user);
        }
        return users;
    }
    
    private void createTestOrdersForUser(User user) {
        // 创建测试订单
        com.example.demo.entity.Order order = new com.example.demo.entity.Order();
        order.setOrderNumber("ORDER_" + System.currentTimeMillis());
        order.setTotalAmount(new BigDecimal("1500.00"));
        order.setStatus(com.example.demo.entity.Order.OrderStatus.COMPLETED);
        order.setShippingAddress("测试地址");
        order.setUser(user);
        
        user.addOrder(order);
    }
}