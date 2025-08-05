package com.example.demo.mock;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * 用户Mock服务接口
 * 用于测试和开发阶段的数据模拟
 */
public interface UserMockService {
    
    /**
     * 生成Mock用户数据
     */
    User generateMockUser();
    
    /**
     * 生成指定数量的Mock用户列表
     */
    List<User> generateMockUsers(int count);
    
    /**
     * 根据ID获取Mock用户
     */
    Optional<User> getMockUserById(Long id);
    
    /**
     * 根据用户名获取Mock用户
     */
    Optional<User> getMockUserByUsername(String username);
    
    /**
     * 获取所有Mock用户
     */
    List<User> getAllMockUsers();
    
    /**
     * 根据状态获取Mock用户列表
     */
    List<User> getMockUsersByStatus(UserStatus status);
    
    /**
     * 创建Mock用户
     */
    User createMockUser(User user);
    
    /**
     * 更新Mock用户
     */
    User updateMockUser(User user);
    
    /**
     * 删除Mock用户
     */
    boolean deleteMockUser(Long id);
    
    /**
     * 清空所有Mock数据
     */
    void clearAllMockData();
    
    /**
     * 初始化默认Mock数据
     */
    void initializeDefaultMockData();
    
    /**
     * 检查Mock用户名是否存在
     */
    boolean mockUsernameExists(String username);
    
    /**
     * 检查Mock邮箱是否存在
     */
    boolean mockEmailExists(String email);
    
    /**
     * 模拟用户登录验证
     */
    boolean mockValidateLogin(String username, String password);
    
    /**
     * 获取Mock数据统计信息
     */
    MockDataStats getMockDataStats();
    
    /**
     * Mock数据统计信息类
     */
    class MockDataStats {
        private int totalUsers;
        private int activeUsers;
        private int inactiveUsers;
        private int suspendedUsers;
        
        public MockDataStats(int totalUsers, int activeUsers, int inactiveUsers, int suspendedUsers) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.inactiveUsers = inactiveUsers;
            this.suspendedUsers = suspendedUsers;
        }
        
        // Getters
        public int getTotalUsers() { return totalUsers; }
        public int getActiveUsers() { return activeUsers; }
        public int getInactiveUsers() { return inactiveUsers; }
        public int getSuspendedUsers() { return suspendedUsers; }
    }
}