# Hibernate Mock 接口使用指南

本项目提供了完整的Hibernate Mock接口实现，用于开发和测试阶段的数据模拟。

## 项目结构

```
src/main/java/com/example/demo/
├── entity/
│   └── User.java                    # 用户实体类
├── repository/
│   └── UserRepository.java          # 用户数据访问层
├── service/
│   ├── UserService.java             # 用户服务接口
│   └── impl/
│       └── UserServiceImpl.java     # 用户服务实现
├── mock/
│   ├── UserMockService.java         # Mock服务接口
│   └── impl/
│       └── UserMockServiceImpl.java # Mock服务实现
└── controller/
    └── UserMockController.java      # Mock数据REST API
```

## 功能特性

### 1. 实体类 (User)
- 完整的JPA注解配置
- 支持用户状态枚举 (ACTIVE, INACTIVE, SUSPENDED)
- 自动时间戳管理
- Lombok注解简化代码

### 2. Repository层
- 继承JpaRepository提供基础CRUD操作
- 自定义查询方法
- 支持用户名、邮箱查找
- 状态筛选和模糊搜索

### 3. Service层
- 完整的业务逻辑封装
- 事务管理
- 数据验证
- 异常处理

### 4. Mock服务
- 内存数据存储
- 随机数据生成
- 完整的CRUD操作
- 数据统计功能

## 快速开始

### 1. 启动应用

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

### 2. 访问H2数据库控制台

访问: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- 用户名: `sa`
- 密码: (空)

### 3. 使用Mock API

#### 获取所有Mock用户
```bash
curl -X GET http://localhost:8080/api/mock/users
```

#### 生成新的Mock用户
```bash
curl -X POST http://localhost:8080/api/mock/users/generate
```

#### 生成多个Mock用户
```bash
curl -X POST http://localhost:8080/api/mock/users/generate/5
```

#### 根据ID获取用户
```bash
curl -X GET http://localhost:8080/api/mock/users/1
```

#### 根据状态获取用户
```bash
curl -X GET http://localhost:8080/api/mock/users/status/ACTIVE
```

#### 创建自定义用户
```bash
curl -X POST http://localhost:8080/api/mock/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "测试用户",
    "phone": "13800138000",
    "status": "ACTIVE"
  }'
```

#### 验证登录
```bash
curl -X POST http://localhost:8080/api/mock/users/validate-login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

#### 获取统计信息
```bash
curl -X GET http://localhost:8080/api/mock/users/stats
```

#### 重置Mock数据
```bash
curl -X POST http://localhost:8080/api/mock/users/reset
```

## 在测试中使用

### 1. 单元测试示例

```java
@Test
void testMockUserCreation() {
    // 创建Mock用户
    User mockUser = userMockService.generateMockUser();
    User createdUser = userMockService.createMockUser(mockUser);
    
    // 验证结果
    assertNotNull(createdUser.getId());
    assertTrue(userMockService.mockUsernameExists(createdUser.getUsername()));
}
```

### 2. 集成测试示例

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceIntegrationTest {
    
    @Autowired
    private UserMockService userMockService;
    
    @Test
    void testUserServiceWithMockData() {
        // 使用Mock数据进行集成测试
        List<User> mockUsers = userMockService.getAllMockUsers();
        assertFalse(mockUsers.isEmpty());
    }
}
```

## 配置说明

### application.yml 关键配置

```yaml
# JPA/Hibernate 配置
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # 开发环境使用，生产环境改为validate
    show-sql: true          # 显示SQL语句
    
# 自定义Mock配置
app:
  mock:
    enabled: true           # 启用Mock功能
    auto-init: true         # 自动初始化Mock数据
    default-user-count: 10  # 默认用户数量
```

## 默认Mock数据

应用启动时会自动创建以下默认用户：

| 用户名 | 邮箱 | 密码 | 状态 |
|--------|------|------|------|
| admin | admin@example.com | password123 | ACTIVE |
| testuser | test@example.com | password123 | ACTIVE |
| demouser | demo@example.com | password123 | INACTIVE |

另外还会随机生成7个用户，总共10个Mock用户。

## 最佳实践

### 1. 开发阶段
- 使用Mock服务快速生成测试数据
- 通过REST API验证业务逻辑
- 利用H2控制台查看数据状态

### 2. 测试阶段
- 在单元测试中使用Mock服务
- 为不同测试场景生成特定数据
- 使用统计功能验证数据完整性

### 3. 生产部署
- 禁用Mock功能 (`app.mock.enabled: false`)
- 更改数据库配置为生产数据库
- 移除H2控制台访问

## 扩展功能

### 1. 添加新的Mock实体

```java
// 1. 创建实体类
@Entity
public class Product {
    // 实体定义
}

// 2. 创建Mock服务
public interface ProductMockService {
    Product generateMockProduct();
    // 其他方法
}

// 3. 实现Mock服务
@Service
public class ProductMockServiceImpl implements ProductMockService {
    // 实现逻辑
}
```

### 2. 自定义数据生成规则

```java
@Component
public class CustomDataGenerator {
    
    public User generateUserWithSpecificPattern(String pattern) {
        // 根据特定模式生成用户
        return user;
    }
}
```

## 故障排除

### 常见问题

1. **H2控制台无法访问**
   - 检查 `spring.h2.console.enabled: true`
   - 确认端口8080未被占用

2. **Mock数据未初始化**
   - 检查 `app.mock.auto-init: true`
   - 查看应用启动日志

3. **JPA实体映射错误**
   - 检查实体类注解
   - 确认数据库方言配置正确

### 日志调试

```yaml
logging:
  level:
    com.example.demo: DEBUG
    org.hibernate.SQL: DEBUG
```

## 总结

本Hibernate Mock接口提供了完整的数据模拟解决方案，支持：
- 快速原型开发
- 单元测试和集成测试
- API接口验证
- 数据结构设计验证

通过合理使用这些Mock接口，可以大大提高开发效率和测试覆盖率。