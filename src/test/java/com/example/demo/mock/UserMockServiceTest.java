package com.example.demo.mock;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.mock.impl.UserMockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserMockService 测试类
 * 演示如何使用Mock服务进行单元测试
 */
@DisplayName("用户Mock服务测试")
class UserMockServiceTest {
    
    private UserMockService userMockService;
    
    @BeforeEach
    void setUp() {
        userMockService = new UserMockServiceImpl();
    }
    
    @Test
    @DisplayName("测试生成Mock用户")
    void testGenerateMockUser() {
        // Given & When
        User mockUser = userMockService.generateMockUser();
        
        // Then
        assertNotNull(mockUser);
        assertNotNull(mockUser.getId());
        assertNotNull(mockUser.getUsername());
        assertNotNull(mockUser.getEmail());
        assertNotNull(mockUser.getPassword());
        assertNotNull(mockUser.getFullName());
        assertNotNull(mockUser.getStatus());
        assertTrue(mockUser.getUsername().startsWith("user"));
        assertTrue(mockUser.getEmail().contains("@"));
    }
    
    @Test
    @DisplayName("测试生成多个Mock用户")
    void testGenerateMockUsers() {
        // Given
        int count = 5;
        
        // When
        List<User> mockUsers = userMockService.generateMockUsers(count);
        
        // Then
        assertNotNull(mockUsers);
        assertEquals(count, mockUsers.size());
        
        // 验证每个用户都有唯一的ID和用户名
        long uniqueIds = mockUsers.stream().mapToLong(User::getId).distinct().count();
        long uniqueUsernames = mockUsers.stream().map(User::getUsername).distinct().count();
        assertEquals(count, uniqueIds);
        assertEquals(count, uniqueUsernames);
    }
    
    @Test
    @DisplayName("测试创建和获取Mock用户")
    void testCreateAndGetMockUser() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullName("测试用户");
        user.setStatus(UserStatus.ACTIVE);
        
        // When
        User createdUser = userMockService.createMockUser(user);
        Optional<User> retrievedUser = userMockService.getMockUserById(createdUser.getId());
        
        // Then
        assertNotNull(createdUser.getId());
        assertNotNull(createdUser.getCreatedAt());
        assertNotNull(createdUser.getUpdatedAt());
        assertTrue(retrievedUser.isPresent());
        assertEquals("testuser", retrievedUser.get().getUsername());
        assertEquals("test@example.com", retrievedUser.get().getEmail());
    }
    
    @Test
    @DisplayName("测试根据用户名查找Mock用户")
    void testGetMockUserByUsername() {
        // Given
        User user = new User();
        user.setUsername("findme");
        user.setEmail("findme@example.com");
        user.setPassword("password");
        user.setStatus(UserStatus.ACTIVE);
        userMockService.createMockUser(user);
        
        // When
        Optional<User> foundUser = userMockService.getMockUserByUsername("findme");
        Optional<User> notFoundUser = userMockService.getMockUserByUsername("notexist");
        
        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("findme@example.com", foundUser.get().getEmail());
        assertFalse(notFoundUser.isPresent());
    }
    
    @Test
    @DisplayName("测试根据状态获取Mock用户")
    void testGetMockUsersByStatus() {
        // Given
        User activeUser = createTestUser("active", UserStatus.ACTIVE);
        User inactiveUser = createTestUser("inactive", UserStatus.INACTIVE);
        User suspendedUser = createTestUser("suspended", UserStatus.SUSPENDED);
        
        userMockService.createMockUser(activeUser);
        userMockService.createMockUser(inactiveUser);
        userMockService.createMockUser(suspendedUser);
        
        // When
        List<User> activeUsers = userMockService.getMockUsersByStatus(UserStatus.ACTIVE);
        List<User> inactiveUsers = userMockService.getMockUsersByStatus(UserStatus.INACTIVE);
        
        // Then
        assertTrue(activeUsers.size() >= 1);
        assertTrue(inactiveUsers.size() >= 1);
        assertTrue(activeUsers.stream().allMatch(u -> u.getStatus() == UserStatus.ACTIVE));
        assertTrue(inactiveUsers.stream().allMatch(u -> u.getStatus() == UserStatus.INACTIVE));
    }
    
    @Test
    @DisplayName("测试更新Mock用户")
    void testUpdateMockUser() {
        // Given
        User user = createTestUser("updateme", UserStatus.ACTIVE);
        User createdUser = userMockService.createMockUser(user);
        
        // When
        createdUser.setFullName("更新后的姓名");
        createdUser.setStatus(UserStatus.INACTIVE);
        User updatedUser = userMockService.updateMockUser(createdUser);
        
        // Then
        assertEquals("更新后的姓名", updatedUser.getFullName());
        assertEquals(UserStatus.INACTIVE, updatedUser.getStatus());
        assertNotNull(updatedUser.getUpdatedAt());
    }
    
    @Test
    @DisplayName("测试删除Mock用户")
    void testDeleteMockUser() {
        // Given
        User user = createTestUser("deleteme", UserStatus.ACTIVE);
        User createdUser = userMockService.createMockUser(user);
        Long userId = createdUser.getId();
        
        // When
        boolean deleted = userMockService.deleteMockUser(userId);
        Optional<User> retrievedUser = userMockService.getMockUserById(userId);
        
        // Then
        assertTrue(deleted);
        assertFalse(retrievedUser.isPresent());
    }
    
    @Test
    @DisplayName("测试用户名和邮箱存在性检查")
    void testUsernameAndEmailExistence() {
        // Given
        User user = createTestUser("checkme", UserStatus.ACTIVE);
        user.setEmail("checkme@example.com");
        userMockService.createMockUser(user);
        
        // When & Then
        assertTrue(userMockService.mockUsernameExists("checkme"));
        assertFalse(userMockService.mockUsernameExists("notexist"));
        assertTrue(userMockService.mockEmailExists("checkme@example.com"));
        assertFalse(userMockService.mockEmailExists("notexist@example.com"));
    }
    
    @Test
    @DisplayName("测试登录验证")
    void testMockValidateLogin() {
        // Given
        User user = createTestUser("loginuser", UserStatus.ACTIVE);
        user.setPassword("correctpassword");
        userMockService.createMockUser(user);
        
        // When & Then
        assertTrue(userMockService.mockValidateLogin("loginuser", "correctpassword"));
        assertFalse(userMockService.mockValidateLogin("loginuser", "wrongpassword"));
        assertFalse(userMockService.mockValidateLogin("notexist", "anypassword"));
    }
    
    @Test
    @DisplayName("测试Mock数据统计")
    void testGetMockDataStats() {
        // Given
        userMockService.clearAllMockData();
        userMockService.createMockUser(createTestUser("active1", UserStatus.ACTIVE));
        userMockService.createMockUser(createTestUser("active2", UserStatus.ACTIVE));
        userMockService.createMockUser(createTestUser("inactive1", UserStatus.INACTIVE));
        userMockService.createMockUser(createTestUser("suspended1", UserStatus.SUSPENDED));
        
        // When
        UserMockService.MockDataStats stats = userMockService.getMockDataStats();
        
        // Then
        assertEquals(4, stats.getTotalUsers());
        assertEquals(2, stats.getActiveUsers());
        assertEquals(1, stats.getInactiveUsers());
        assertEquals(1, stats.getSuspendedUsers());
    }
    
    @Test
    @DisplayName("测试清空Mock数据")
    void testClearAllMockData() {
        // Given
        userMockService.createMockUser(createTestUser("test", UserStatus.ACTIVE));
        assertTrue(userMockService.getAllMockUsers().size() > 0);
        
        // When
        userMockService.clearAllMockData();
        
        // Then
        assertEquals(0, userMockService.getAllMockUsers().size());
    }
    
    private User createTestUser(String username, UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("password123");
        user.setFullName("测试用户 " + username);
        user.setStatus(status);
        return user;
    }
}