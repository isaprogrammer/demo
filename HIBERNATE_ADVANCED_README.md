# Hibernate 高级功能演示项目

本项目全面演示了 Hibernate/JPA 的各种高级功能和最佳实践，涵盖了企业级应用开发中常用的所有 Hibernate 特性。

## 🚀 项目特性

### 1. 实体关系映射
- **一对一关系**: User ↔ UserProfile
- **一对多关系**: User → Orders, Order → OrderItems, Product → OrderItems
- **多对多关系**: User ↔ Role, Role ↔ Permission
- **复杂关联**: 级联操作、孤儿删除、懒加载/急加载

### 2. 高级注解使用
- `@Entity`, `@Table`, `@Index` - 实体和表映射
- `@Id`, `@GeneratedValue` - 主键生成策略
- `@Column`, `@Enumerated`, `@Temporal` - 字段映射
- `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany` - 关联关系
- `@JoinTable`, `@JoinColumn` - 连接表配置
- `@Cache`, `@Cacheable` - 二级缓存
- `@NamedQuery`, `@NamedQueries` - 命名查询
- `@Filter`, `@FilterDef` - 动态过滤器
- `@Formula` - 计算字段
- `@CreationTimestamp`, `@UpdateTimestamp` - 时间戳
- `@SQLDelete`, `@Where` - 软删除

### 3. 查询功能

#### 3.1 Repository 查询方法
- 方法名查询: `findByStatus`, `findByUsernameContaining`
- 复杂条件查询: `findByStatusAndCreatedAtBetween`
- 排序和分页: `findByStatusOrderByCreatedAtDesc`

#### 3.2 JPQL 查询
- 基本 JPQL: `SELECT u FROM User u WHERE u.status = :status`
- 连接查询: `SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName`
- 子查询: `SELECT u FROM User u WHERE u.id IN (...)`
- 聚合查询: `SELECT COUNT(u), u.status FROM User u GROUP BY u.status`

#### 3.3 原生 SQL 查询
- 简单原生查询: `SELECT * FROM users WHERE created_at > ?`
- 复杂统计查询: 包含 GROUP BY, HAVING, 聚合函数
- 存储过程调用: `CALL GetUsersByAgeRange(:minAge, :maxAge)`

#### 3.4 Criteria API
- 动态查询构建
- 类型安全的查询
- 复杂条件组合
- 连接查询和子查询

#### 3.5 Specification 查询
- Spring Data JPA Specification
- 动态条件构建
- 可重用的查询逻辑

### 4. 缓存机制

#### 4.1 一级缓存（Session 缓存）
- 自动启用的 EntityManager 缓存
- 同一事务内的实体缓存

#### 4.2 二级缓存
- 实体级缓存: `@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)`
- 集合缓存: 关联集合的缓存
- 查询缓存: `hibernate.cache.use_query_cache=true`

#### 4.3 Spring 缓存
- `@Cacheable` - 方法结果缓存
- `@CachePut` - 缓存更新
- `@CacheEvict` - 缓存清除

### 5. 事务管理

#### 5.1 声明式事务
- `@Transactional` - 方法级事务
- 事务传播行为: `REQUIRED`, `REQUIRES_NEW`, `NESTED`
- 隔离级别: `READ_COMMITTED`, `REPEATABLE_READ`
- 回滚策略: `rollbackFor`, `noRollbackFor`

#### 5.2 编程式事务
- TransactionTemplate 使用
- 手动事务控制

### 6. 性能优化

#### 6.1 批处理操作
- 批量插入: `hibernate.jdbc.batch_size=20`
- 批量更新: `@Modifying` 查询
- 批量删除: 大数据量删除优化

#### 6.2 懒加载优化
- `FetchType.LAZY` - 延迟加载
- `@EntityGraph` - 解决 N+1 问题
- `JOIN FETCH` - 急加载优化

#### 6.3 查询优化
- 索引使用: `@Index` 注解
- 查询计划缓存
- 投影查询: 只查询需要的字段

### 7. 高级特性

#### 7.1 动态过滤器
- `@Filter` - 实体级过滤
- 运行时启用/禁用过滤器
- 参数化过滤条件

#### 7.2 审计功能
- `@CreationTimestamp` - 创建时间自动设置
- `@UpdateTimestamp` - 更新时间自动设置
- `@PrePersist`, `@PreUpdate` - 生命周期回调

#### 7.3 软删除
- `@SQLDelete` - 自定义删除 SQL
- `@Where` - 查询时自动过滤已删除数据

#### 7.4 乐观锁
- `@Version` - 版本控制
- 并发更新检测
- `OptimisticLockException` 处理

#### 7.5 命名策略
- `CamelCaseToUnderscoresNamingStrategy` - 驼峰转下划线
- 自定义命名策略

## 📁 项目结构

```
src/main/java/com/example/demo/
├── entity/                     # 实体类
│   ├── User.java              # 用户实体（演示多种关联关系）
│   ├── Role.java              # 角色实体（多对多关系）
│   ├── Permission.java        # 权限实体（多对多关系）
│   ├── UserProfile.java       # 用户档案（一对多关系）
│   ├── Order.java             # 订单实体（复杂业务实体）
│   ├── OrderItem.java         # 订单项（关联实体）
│   └── Product.java           # 产品实体（演示计算字段）
├── repository/                 # 数据访问层
│   └── AdvancedUserRepository.java  # 高级查询方法
├── service/                    # 服务层
│   └── AdvancedHibernateService.java # Hibernate高级功能服务
├── controller/                 # 控制器层
│   └── HibernateAdvancedController.java # API接口
└── config/                     # 配置类
    ├── HibernateAdvancedConfig.java     # Hibernate配置
    └── HibernateDataInitializer.java    # 测试数据初始化
```

## 🛠️ 技术栈

- **Spring Boot 3.x** - 应用框架
- **Spring Data JPA** - 数据访问抽象
- **Hibernate 6.x** - ORM 框架
- **H2 Database** - 内存数据库（演示用）
- **Spring Cache** - 缓存抽象
- **Lombok** - 代码简化

## 🚀 快速开始

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 访问演示接口
```bash
# 演示所有Hibernate功能
curl http://localhost:8081/api/hibernate-advanced/demo

# 缓存操作
curl http://localhost:8081/api/hibernate-advanced/cache/user/1

# 复杂查询
curl "http://localhost:8081/api/hibernate-advanced/users/search?username=admin&page=0&size=10"

# 统计查询
curl http://localhost:8081/api/hibernate-advanced/statistics/users
```

### 3. 访问 H2 控制台
- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- 用户名: `sa`
- 密码: (空)

## 📊 演示的 Hibernate 功能清单

### ✅ 基础功能
- [x] 实体映射和表创建
- [x] 基本 CRUD 操作
- [x] 主键生成策略
- [x] 字段类型映射
- [x] 枚举类型映射

### ✅ 关联关系
- [x] 一对一关联 (OneToOne)
- [x] 一对多关联 (OneToMany)
- [x] 多对一关联 (ManyToOne)
- [x] 多对多关联 (ManyToMany)
- [x] 级联操作 (Cascade)
- [x] 孤儿删除 (OrphanRemoval)
- [x] 懒加载和急加载

### ✅ 查询功能
- [x] Repository 方法查询
- [x] JPQL 查询
- [x] 原生 SQL 查询
- [x] Criteria API
- [x] Specification 查询
- [x] 命名查询 (NamedQuery)
- [x] 投影查询
- [x] 分页查询
- [x] 排序查询
- [x] 聚合查询
- [x] 子查询
- [x] 连接查询

### ✅ 缓存机制
- [x] 一级缓存 (Session Cache)
- [x] 二级缓存 (Second Level Cache)
- [x] 查询缓存 (Query Cache)
- [x] 集合缓存 (Collection Cache)
- [x] Spring 缓存集成

### ✅ 事务管理
- [x] 声明式事务
- [x] 事务传播行为
- [x] 事务隔离级别
- [x] 事务回滚策略
- [x] 嵌套事务

### ✅ 性能优化
- [x] 批处理操作
- [x] N+1 问题解决
- [x] EntityGraph 使用
- [x] 查询优化
- [x] 索引使用
- [x] 连接池配置

### ✅ 高级特性
- [x] 动态过滤器 (Filter)
- [x] 审计功能 (Auditing)
- [x] 软删除 (Soft Delete)
- [x] 乐观锁 (Optimistic Locking)
- [x] 悲观锁 (Pessimistic Locking)
- [x] 版本控制 (Versioning)
- [x] 生命周期回调
- [x] 计算字段 (Formula)
- [x] 自定义类型
- [x] 命名策略

### ✅ 配置和监控
- [x] Hibernate 配置优化
- [x] 统计信息收集
- [x] SQL 日志记录
- [x] 性能监控

## 🔧 配置说明

### application.yml 关键配置
```yaml
spring:
  jpa:
    properties:
      hibernate:
        # 二级缓存
        cache:
          use_second_level_cache: true
          use_query_cache: true
        # 批处理
        jdbc:
          batch_size: 20
          batch_versioned_data: true
        # 统计信息
        generate_statistics: true
        # 懒加载
        enable_lazy_load_no_trans: true
```

## 📝 使用示例

### 1. 复杂查询示例
```java
// Criteria API 动态查询
public List<User> findUsersByCriteria(String username, String email, UserStatus status) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    Root<User> root = query.from(User.class);
    
    List<Predicate> predicates = new ArrayList<>();
    if (username != null) {
        predicates.add(cb.like(root.get("username"), "%" + username + "%"));
    }
    // ... 更多条件
    
    query.where(predicates.toArray(new Predicate[0]));
    return entityManager.createQuery(query).getResultList();
}
```

### 2. 缓存使用示例
```java
@Cacheable(value = "users", key = "#id")
public Optional<User> getUserWithCache(Long id) {
    return userRepository.findById(id);
}

@CacheEvict(value = "users", key = "#id")
public void evictUserFromCache(Long id) {
    // 缓存清除
}
```

### 3. 事务管理示例
```java
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public User createUserWithTransaction(User user) {
    User savedUser = userRepository.save(user);
    // 其他业务逻辑
    return savedUser;
}
```

## 🎯 学习要点

1. **实体设计**: 合理设计实体关系，避免循环引用
2. **查询优化**: 选择合适的查询方式，避免 N+1 问题
3. **缓存策略**: 合理使用缓存，提高查询性能
4. **事务管理**: 正确使用事务，保证数据一致性
5. **性能调优**: 通过批处理、索引等方式优化性能

## 🔍 监控和调试

- 启用 SQL 日志: `hibernate.show_sql=true`
- 格式化 SQL: `hibernate.format_sql=true`
- 统计信息: `hibernate.generate_statistics=true`
- H2 控制台: 查看数据库结构和数据

这个项目涵盖了 Hibernate 在企业级应用中的几乎所有常用功能，是学习和参考 Hibernate 高级特性的完整示例。