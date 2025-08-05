package com.example.demo.mock.impl;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.mock.UserMockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 用户Mock服务实现类
 */
@Service
@Slf4j
public class UserMockServiceImpl implements UserMockService {
    
    private final Map<Long, User> mockUserStorage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Random random = new Random();
    
    // Mock数据模板
    private final String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
    private final String[] lastNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋"};
    private final String[] domains = {"gmail.com", "163.com", "qq.com", "sina.com", "hotmail.com"};
    
    public UserMockServiceImpl() {
        initializeDefaultMockData();
    }
    
    @Override
    public User generateMockUser() {
        Long id = idGenerator.getAndIncrement();
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        String fullName = firstName + lastName;
        String username = "user" + id;
        String email = username + "@" + domains[random.nextInt(domains.length)];
        String phone = "1" + String.format("%010d", random.nextInt(1000000000));
        UserStatus status = UserStatus.values()[random.nextInt(UserStatus.values().length)];
        
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password" + id);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
        user.setUpdatedAt(LocalDateTime.now());
        
        return user;
    }
    
    @Override
    public List<User> generateMockUsers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateMockUser())
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<User> getMockUserById(Long id) {
        return Optional.ofNullable(mockUserStorage.get(id));
    }
    
    @Override
    public Optional<User> getMockUserByUsername(String username) {
        return mockUserStorage.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    @Override
    public List<User> getAllMockUsers() {
        return new ArrayList<>(mockUserStorage.values());
    }
    
    @Override
    public List<User> getMockUsersByStatus(UserStatus status) {
        return mockUserStorage.values().stream()
                .filter(user -> user.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    @Override
    public User createMockUser(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        mockUserStorage.put(user.getId(), user);
        log.info("Created mock user: {}", user.getUsername());
        return user;
    }
    
    @Override
    public User updateMockUser(User user) {
        if (user.getId() == null || !mockUserStorage.containsKey(user.getId())) {
            throw new IllegalArgumentException("Mock user not found with id: " + user.getId());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        mockUserStorage.put(user.getId(), user);
        log.info("Updated mock user: {}", user.getUsername());
        return user;
    }
    
    @Override
    public boolean deleteMockUser(Long id) {
        User removed = mockUserStorage.remove(id);
        if (removed != null) {
            log.info("Deleted mock user: {}", removed.getUsername());
            return true;
        }
        return false;
    }
    
    @Override
    public void clearAllMockData() {
        mockUserStorage.clear();
        idGenerator.set(1);
        log.info("Cleared all mock data");
    }
    
    @Override
    public void initializeDefaultMockData() {
        clearAllMockData();
        
        // 创建一些默认的Mock用户
        List<User> defaultUsers = Arrays.asList(
            createDefaultUser("admin", "admin@example.com", "管理员", UserStatus.ACTIVE),
            createDefaultUser("testuser", "test@example.com", "测试用户", UserStatus.ACTIVE),
            createDefaultUser("demouser", "demo@example.com", "演示用户", UserStatus.INACTIVE)
        );
        
        defaultUsers.forEach(this::createMockUser);
        
        // 生成额外的随机用户
        generateMockUsers(7).forEach(this::createMockUser);
        
        log.info("Initialized default mock data with {} users", mockUserStorage.size());
    }
    
    @Override
    public boolean mockUsernameExists(String username) {
        return mockUserStorage.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    
    @Override
    public boolean mockEmailExists(String email) {
        return mockUserStorage.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @Override
    public boolean mockValidateLogin(String username, String password) {
        return getMockUserByUsername(username)
                .map(user -> user.getPassword().equals(password) && user.getStatus() == UserStatus.ACTIVE)
                .orElse(false);
    }
    
    @Override
    public MockDataStats getMockDataStats() {
        List<User> allUsers = getAllMockUsers();
        int total = allUsers.size();
        int active = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.ACTIVE).count();
        int inactive = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.INACTIVE).count();
        int suspended = (int) allUsers.stream().filter(u -> u.getStatus() == UserStatus.SUSPENDED).count();
        
        return new MockDataStats(total, active, inactive, suspended);
    }
    
    private User createDefaultUser(String username, String email, String fullName, UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        user.setFullName(fullName);
        user.setPhone("13800138000");
        user.setStatus(status);
        return user;
    }
}