package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.AdvancedUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * 高级Hibernate服务类 - 演示各种Hibernate高级特性
 */
@Service
@Transactional
public class AdvancedHibernateService {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private AdvancedUserRepository userRepository;
    
    // 1. 缓存操作演示
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserWithCache(Long id) {
        return userRepository.findById(id);
    }
    
    @CachePut(value = "users", key = "#user.id")
    public User updateUserWithCache(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void evictUserFromCache(Long id) {
        // 缓存清除
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearAllUserCache() {
        // 清除所有用户缓存
    }
    
    // 2. 事务管理演示
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public User createUserWithTransaction(User user) {
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 创建默认角色
        Role defaultRole = new Role();
        defaultRole.setName("USER");
        defaultRole.setDescription("Default user role");
        entityManager.persist(defaultRole);
        
        // 关联用户和角色
        savedUser.addRole(defaultRole);
        
        return userRepository.save(savedUser);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createAuditLog(String action, Long userId) {
        // 在新事务中创建审计日志
        // 即使主事务回滚，审计日志也会保存
    }
    
    // 3. 批处理操作
    @Transactional
    public void batchCreateUsers(List<User> users) {
        int batchSize = 20;
        for (int i = 0; i < users.size(); i++) {
            entityManager.persist(users.get(i));
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }
    
    @Transactional
    public int batchUpdateUserStatus(User.UserStatus oldStatus, User.UserStatus newStatus) {
        return userRepository.updateUserStatus(oldStatus, newStatus);
    }
    
    // 4. Criteria API查询
    public List<User> findUsersByCriteria(String username, String email, User.UserStatus status) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (username != null && !username.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }
        
        if (email != null && !email.isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }
        
        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(root.get("createdAt")));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    // 5. 复杂的连接查询
    public List<User> findUsersWithRoleCount() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);
        Join<User, Role> roleJoin = userRoot.join("roles", JoinType.LEFT);
        
        query.select(userRoot)
             .groupBy(userRoot.get("id"))
             .having(cb.greaterThan(cb.count(roleJoin), 1L))
             .orderBy(cb.desc(cb.count(roleJoin)));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    // 6. 原生SQL查询
    @SuppressWarnings("unchecked")
    public List<Object[]> getUserStatistics() {
        String sql = "SELECT u.status, COUNT(*) as user_count, " +
                    "AVG(DATEDIFF(NOW(), u.created_at)) as avg_days_since_registration " +
                    "FROM users u GROUP BY u.status";
        
        Query query = entityManager.createNativeQuery(sql);
        return query.getResultList();
    }
    
    // 7. Hibernate Session直接操作
    public void demonstrateHibernateSession() {
        Session session = entityManager.unwrap(Session.class);
        
        // 启用过滤器
        session.enableFilter("activeUsersFilter").setParameter("status", "ACTIVE");
        
        // 执行查询
        List<User> activeUsers = session.createQuery("FROM User", User.class).getResultList();
        
        // 禁用过滤器
        session.disableFilter("activeUsersFilter");
    }
    
    // 8. 分页查询
    public Page<User> findUsersWithPagination(int page, int size, String sortBy, String sortDir) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userRepository.findAll(pageable);
    }
    
    // 9. Specification查询
    public Page<User> findUsersWithSpecification(String username, String email, User.UserStatus status, Pageable pageable) {
        Specification<User> spec = Specification.where(null);
        
        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%"));
        }
        
        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, cb) -> 
                cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        
        return userRepository.findAll(spec, pageable);
    }
    
    // 10. 懒加载演示
    @Transactional(readOnly = true)
    public User getUserWithLazyLoading(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 触发懒加载
            user.getRoles().size();
            user.getUserProfiles().size();
            user.getOrders().size();
            return user;
        }
        return null;
    }
    
    // 11. 二级缓存演示
    @Cacheable(value = "userRoles", key = "#userId")
    public List<Role> getUserRoles(Long userId) {
        Optional<User> userOpt = userRepository.findByIdWithRolesAndPermissions(userId);
        return userOpt.map(user -> new ArrayList<>(user.getRoles())).orElse(new ArrayList<>());
    }
    
    // 12. 乐观锁演示
    @Transactional
    public User updateUserWithOptimisticLock(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setEmail(newEmail);
        // 如果版本号不匹配，会抛出OptimisticLockException
        return userRepository.save(user);
    }
    
    // 13. 统计查询
    public Object[] getUserActivityStatistics() {
        String jpql = "SELECT " +
                     "COUNT(u), " +
                     "COUNT(CASE WHEN u.status = 'ACTIVE' THEN 1 END), " +
                     "COUNT(CASE WHEN u.createdAt > :lastMonth THEN 1 END) " +
                     "FROM User u";
        
        TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        query.setParameter("lastMonth", LocalDateTime.now().minusMonths(1));
        
        return query.getSingleResult();
    }
    
    // 14. 复杂业务逻辑演示
    @Transactional
    public void processUserOrders(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // 计算用户总订单金额
        BigDecimal totalAmount = user.getOrders().stream()
            .filter(order -> order.getStatus() == com.example.demo.entity.Order.OrderStatus.COMPLETED)
            .map(com.example.demo.entity.Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 根据订单金额更新用户等级
        if (totalAmount.compareTo(new BigDecimal("1000")) >= 0) {
            // 添加VIP角色
            Role vipRole = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.name = 'VIP'", Role.class)
                .getSingleResult();
            user.addRole(vipRole);
        }
        
        userRepository.save(user);
    }
}