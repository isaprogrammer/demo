package com.example.demo.config;

import com.example.demo.entity.*;
import com.example.demo.repository.AdvancedUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Hibernate演示数据初始化器
 */
@Component
public class HibernateDataInitializer implements CommandLineRunner {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AdvancedUserRepository userRepository;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("开始执行Hibernate数据库操作演示...");
        
        // 演示Hibernate的各种操作
        demonstrateHibernateOperations();
        
        System.out.println("Hibernate数据库操作演示完成!");
    }
    
    @Transactional
    public void demonstrateHibernateOperations() {
        System.out.println("=== Hibernate操作演示开始 ===");
        
        try {
            // 1. 查询操作演示
            demonstrateQueryOperations();
            
            // 2. 插入操作演示
            demonstrateInsertOperations();
            
            // 3. 更新操作演示
            demonstrateUpdateOperations();
            
            // 4. 删除操作演示
            demonstrateDeleteOperations();
            
            // 5. 批量操作演示
            demonstrateBatchOperations();
            
            System.out.println("=== Hibernate操作演示结束 ===");
        } catch (Exception e) {
            System.err.println("Hibernate操作演示过程中出现错误: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Hibernate操作演示失败", e);
        }
    }
    
    @Transactional
    public void demonstrateQueryOperations() {
        System.out.println("\n--- 查询操作演示 ---");
        
        // 查询所有用户
        List<User> allUsers = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        System.out.println("查询到 " + allUsers.size() + " 个用户");
        
        // 条件查询
        List<User> activeUsers = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.status = :status", User.class)
            .setParameter("status", User.UserStatus.ACTIVE)
            .getResultList();
        System.out.println("活跃用户数量: " + activeUsers.size());
        
        // 分页查询
        List<User> pagedUsers = entityManager.createQuery("SELECT u FROM User u", User.class)
            .setFirstResult(0)
            .setMaxResults(3)
            .getResultList();
        System.out.println("分页查询前3个用户: " + pagedUsers.size());
    }
    
    @Transactional
    public void demonstrateInsertOperations() {
        System.out.println("\n--- 插入操作演示 ---");
        
        // 创建新产品
        Product newProduct = new Product();
        newProduct.setSku("DEMO-" + System.currentTimeMillis());
        newProduct.setName("Hibernate演示产品");
        newProduct.setDescription("这是一个用于演示Hibernate操作的产品");
        newProduct.setCategory("演示类别");
        newProduct.setPrice(new BigDecimal("99.99"));
        newProduct.setCost(new BigDecimal("50.00"));
        newProduct.setStockQuantity(100);
        newProduct.setWeight(new BigDecimal("1.5"));
        newProduct.setCreatedAt(LocalDateTime.now());
        newProduct.setUpdatedAt(LocalDateTime.now());
        
        entityManager.persist(newProduct);
        System.out.println("成功插入新产品: " + newProduct.getName());
    }
    
    @Transactional
    public void demonstrateUpdateOperations() {
        System.out.println("\n--- 更新操作演示 ---");
        
        // 查找第一个用户并更新
        List<User> users = entityManager.createQuery("SELECT u FROM User u", User.class)
            .setMaxResults(1)
            .getResultList();
        
        if (!users.isEmpty()) {
            User user = users.get(0);
            String oldFullName = user.getFullName();
            user.setFullName(user.getFullName() + " (已更新)");
            user.setUpdatedAt(LocalDateTime.now());
            
            entityManager.merge(user);
            System.out.println("更新用户: " + oldFullName + " -> " + user.getFullName());
        }
        
        // 注释掉批量更新操作，因为需要特殊的事务处理
        // int updatedCount = entityManager.createQuery(
        //     "UPDATE User u SET u.updatedAt = :now WHERE u.status = :status")
        //     .setParameter("now", LocalDateTime.now())
        //     .setParameter("status", User.UserStatus.ACTIVE)
        //     .executeUpdate();
        // System.out.println("批量更新了 " + updatedCount + " 个活跃用户的更新时间");
        System.out.println("批量更新操作已跳过（需要特殊事务处理）");
    }
    
    @Transactional
    public void demonstrateDeleteOperations() {
        System.out.println("\n--- 删除操作演示 ---");
        
        // 查找演示产品并删除
        List<Product> demoProducts = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.name LIKE :name", Product.class)
            .setParameter("name", "%演示%")
            .getResultList();
        
        for (Product product : demoProducts) {
            entityManager.remove(product);
            System.out.println("删除演示产品: " + product.getName());
        }
    }
    
    @Transactional
    public void demonstrateBatchOperations() {
        System.out.println("\n--- 批量操作演示 ---");
        
        try {
            // 批量插入产品
            for (int i = 1; i <= 3; i++) {
                Product product = new Product();
                product.setSku("BATCH-" + i + "-" + System.currentTimeMillis());
                product.setName("批量产品 " + i);
                product.setDescription("批量操作演示产品 " + i);
                product.setCategory("批量类别");
                product.setPrice(new BigDecimal("10." + i + "0"));
                product.setCost(new BigDecimal("5." + i + "0"));
                product.setStockQuantity(10 * i);
                product.setWeight(new BigDecimal("0." + i));
                product.setCreatedAt(LocalDateTime.now());
                product.setUpdatedAt(LocalDateTime.now());
                
                entityManager.persist(product);
            }
            
            // 手动刷新 - 移除 flush 调用，避免事务问题
            // entityManager.flush();
            System.out.println("批量插入了3个产品");
            
            // 统计查询
            Long productCount = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p", Long.class)
                .getSingleResult();
            System.out.println("当前产品总数: " + productCount);
        } catch (Exception e) {
            System.err.println("批量操作出现错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public List<Permission> createPermissions() {
        Permission[] permissionArray = {
            new Permission(null, "USER_READ", "查看用户", "查看用户信息的权限", "USER", true, null, null, null),
            new Permission(null, "USER_WRITE", "编辑用户", "编辑用户信息的权限", "USER", true, null, null, null),
            new Permission(null, "USER_DELETE", "删除用户", "删除用户的权限", "USER", true, null, null, null),
            new Permission(null, "ORDER_READ", "查看订单", "查看订单信息的权限", "ORDER", true, null, null, null),
            new Permission(null, "ORDER_WRITE", "编辑订单", "编辑订单信息的权限", "ORDER", true, null, null, null),
            new Permission(null, "PRODUCT_READ", "查看产品", "查看产品信息的权限", "PRODUCT", true, null, null, null),
            new Permission(null, "PRODUCT_WRITE", "编辑产品", "编辑产品信息的权限", "PRODUCT", true, null, null, null),
            new Permission(null, "ADMIN_ACCESS", "管理员访问", "管理员访问权限", "ADMIN", true, null, null, null)
        };
        
        for (Permission permission : permissionArray) {
            entityManager.persist(permission);
        }
        
        return Arrays.asList(permissionArray);
    }
    
    public List<Role> createRoles(List<Permission> permissions) {
        // 创建用户角色
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("普通用户角色");
        userRole.setEnabled(true);
        userRole.getPermissions().addAll(Arrays.asList(
            permissions.get(0), // USER_READ
            permissions.get(3), // ORDER_READ
            permissions.get(5)  // PRODUCT_READ
        ));
        entityManager.persist(userRole);
        
        // 创建管理员角色
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("管理员角色");
        adminRole.setEnabled(true);
        adminRole.getPermissions().addAll(permissions);
        entityManager.persist(adminRole);
        
        // 创建VIP角色
        Role vipRole = new Role();
        vipRole.setName("VIP");
        vipRole.setDescription("VIP用户角色");
        vipRole.setEnabled(true);
        vipRole.getPermissions().addAll(Arrays.asList(
            permissions.get(0), // USER_READ
            permissions.get(3), // ORDER_READ
            permissions.get(4), // ORDER_WRITE
            permissions.get(5)  // PRODUCT_READ
        ));
        entityManager.persist(vipRole);
        return Arrays.asList(userRole, adminRole, vipRole);
    }
    
    public List<Product> createProducts() {
        Product[] productArray = {
            createProduct("LAPTOP001", "笔记本电脑", "高性能笔记本电脑", "电子产品", new BigDecimal("5999.00"), new BigDecimal("4500.00"), 50, new BigDecimal("2.5")),
            createProduct("PHONE001", "智能手机", "最新款智能手机", "电子产品", new BigDecimal("3999.00"), new BigDecimal("3000.00"), 100, new BigDecimal("0.2")),
            createProduct("BOOK001", "编程书籍", "Java编程实战", "图书", new BigDecimal("89.00"), new BigDecimal("60.00"), 200, new BigDecimal("0.5")),
            createProduct("MOUSE001", "无线鼠标", "人体工学无线鼠标", "电子产品", new BigDecimal("199.00"), new BigDecimal("120.00"), 300, new BigDecimal("0.1")),
            createProduct("KEYBOARD001", "机械键盘", "RGB机械键盘", "电子产品", new BigDecimal("599.00"), new BigDecimal("400.00"), 80, new BigDecimal("1.2"))
        };
        
        for (Product product : productArray) {
            entityManager.persist(product);
        }
        
        return Arrays.asList(productArray);
    }
    
    private Product createProduct(String sku, String name, String description, String category, 
                                 BigDecimal price, BigDecimal cost, Integer stock, BigDecimal weight) {
        Product product = new Product();
        product.setSku(sku);
        product.setName(name);
        product.setDescription(description);
        product.setCategory(category);
        product.setPrice(price);
        product.setCost(cost);
        product.setStockQuantity(stock);
        product.setWeight(weight);
        product.setStatus(Product.ProductStatus.ACTIVE);
        return product;
    }
    
    public List<User> createUsers(List<Role> roles) {
        User[] userArray = {
            createUser("admin", "admin@example.com", "管理员", "13800000001", User.UserStatus.ACTIVE, Arrays.asList(roles.get(1))),
            createUser("john_doe", "john@example.com", "约翰·多伊", "13800000002", User.UserStatus.ACTIVE, Arrays.asList(roles.get(0))),
            createUser("jane_smith", "jane@example.com", "简·史密斯", "13800000003", User.UserStatus.ACTIVE, Arrays.asList(roles.get(0), roles.get(2))),
            createUser("bob_wilson", "bob@example.com", "鲍勃·威尔逊", "13800000004", User.UserStatus.INACTIVE, Arrays.asList(roles.get(0))),
            createUser("alice_brown", "alice@example.com", "爱丽丝·布朗", "13800000005", User.UserStatus.ACTIVE, Arrays.asList(roles.get(0)))
        };
        
        for (User user : userArray) {
            entityManager.persist(user);
            
            // 为每个用户创建用户档案
            createUserProfiles(user);
        }
        
        return Arrays.asList(userArray);
    }
    
    private User createUser(String username, String email, String fullName, String phone, 
                           User.UserStatus status, List<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123"); // 实际应用中应该加密
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setStatus(status);
        
        // 添加角色
        for (Role role : roles) {
            user.addRole(role);
        }
        
        return user;
    }
    
    public void createUserProfiles(User user) {
        // 创建偏好设置档案
        UserProfile preferenceProfile = new UserProfile();
        preferenceProfile.setProfileType("PREFERENCE");
        preferenceProfile.setProfileKey("theme");
        preferenceProfile.setProfileValue("light");
        preferenceProfile.setDescription("用户界面主题偏好");
        preferenceProfile.setUser(user);
        entityManager.persist(preferenceProfile);
        
        // 创建通知设置档案
        UserProfile notificationProfile = new UserProfile();
        notificationProfile.setProfileType("NOTIFICATION");
        notificationProfile.setProfileKey("email_notifications");
        notificationProfile.setProfileValue("true");
        notificationProfile.setDescription("邮件通知设置");
        notificationProfile.setUser(user);
        entityManager.persist(notificationProfile);
    }
    
    public void createOrders(List<User> users, List<Product> products) {
        // 为前3个用户创建订单
        for (int i = 0; i < Math.min(3, users.size()); i++) {
            User user = users.get(i);
            
            // 创建订单1
            com.example.demo.entity.Order order1 = new com.example.demo.entity.Order();
            order1.setOrderNumber("ORDER_" + (i + 1) + "_" + System.currentTimeMillis());
            order1.setTotalAmount(new BigDecimal("1299.00"));
            order1.setStatus(com.example.demo.entity.Order.OrderStatus.COMPLETED);
            order1.setShippingAddress("北京市朝阳区测试地址" + (i + 1) + "号");
            order1.setNotes("测试订单" + (i + 1));
            order1.setUser(user);
            entityManager.persist(order1);
            
            // 创建订单项
            createOrderItems(order1, products);
            
            // 创建订单2（如果是第一个用户）
            if (i == 0) {
                com.example.demo.entity.Order order2 = new com.example.demo.entity.Order();
                order2.setOrderNumber("ORDER_" + (i + 1) + "_2_" + System.currentTimeMillis());
                order2.setTotalAmount(new BigDecimal("599.00"));
                order2.setStatus(com.example.demo.entity.Order.OrderStatus.PROCESSING);
                order2.setShippingAddress("上海市浦东新区测试地址" + (i + 1) + "号");
                order2.setNotes("第二个测试订单");
                order2.setUser(user);
                entityManager.persist(order2);
                
                createOrderItems(order2, products.subList(0, 2));
            }
        }
    }
    
    public void createOrderItems(com.example.demo.entity.Order order, List<Product> products) {
        for (int i = 0; i < Math.min(2, products.size()); i++) {
            Product product = products.get(i);
            
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setQuantity(1 + i);
            item.setUnitPrice(product.getPrice());
            item.setDiscountAmount(new BigDecimal("10.00"));
            item.setOrder(order);
            
            entityManager.persist(item);
        }
    }
}