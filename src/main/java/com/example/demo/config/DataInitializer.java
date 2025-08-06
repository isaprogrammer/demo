package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库数据初始化器
 * 在应用启动时向Hibernate数据库插入初始测试数据
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查数据库是否已有数据
        if (userRepository.count() > 0) {
            log.info("Database already contains data, skipping initialization");
            return;
        }

        log.info("Initializing database with test data...");

        // 创建初始用户数据
        List<User> initialUsers = Arrays.asList(
                createUser("admin", "admin@example.com", "admin123", "系统管理员", "13800000001", UserStatus.ACTIVE),
                createUser("testuser", "test@example.com", "test123", "测试用户", "13800000002", UserStatus.ACTIVE),
                createUser("demouser", "demo@example.com", "demo123", "演示用户", "13800000003", UserStatus.INACTIVE),
                createUser("john_doe", "john@example.com", "john123", "约翰·多伊", "13800000004", UserStatus.ACTIVE),
                createUser("jane_smith", "jane@example.com", "jane123", "简·史密斯", "13800000005", UserStatus.ACTIVE),
                createUser("bob_wilson", "bob@example.com", "bob123", "鲍勃·威尔逊", "13800000006", UserStatus.SUSPENDED),
                createUser("alice_brown", "alice@example.com", "alice123", "爱丽丝·布朗", "13800000007", UserStatus.ACTIVE),
                createUser("charlie_davis", "charlie@example.com", "charlie123", "查理·戴维斯", "13800000008", UserStatus.INACTIVE),
                createUser("diana_miller", "diana@example.com", "diana123", "戴安娜·米勒", "13800000009", UserStatus.ACTIVE),
                createUser("edward_jones", "edward@example.com", "edward123", "爱德华·琼斯", "13800000010", UserStatus.ACTIVE)
        );

        // 批量保存到数据库
        userRepository.saveAll(initialUsers);

        log.info("Successfully initialized database with {} users", initialUsers.size());

        // 输出统计信息
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countActiveUsers();
        userRepository.findByFullNameContaining("test");
        log.info("Database statistics: Total users: {}, Active users: {}", totalUsers, activeUsers);
    }

    /**
     * 创建用户对象
     */
    private User createUser(String username, String email, String password,
                            String fullName, String phone, UserStatus status) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setStatus(status);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }
}