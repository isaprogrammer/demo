package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.entity.User.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 高级用户Repository - 演示Hibernate的各种查询功能
 */
@Repository
public interface AdvancedUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    // 1. 基本查询方法
    List<User> findByStatus(UserStatus status);
    
    List<User> findByUsernameContainingIgnoreCase(String username);
    
    List<User> findByEmailEndingWith(String domain);
    
    // 2. 复杂条件查询
    List<User> findByStatusAndCreatedAtBetween(UserStatus status, LocalDateTime start, LocalDateTime end);
    
    Page<User> findByStatusOrderByCreatedAtDesc(UserStatus status, Pageable pageable);
    
    // 3. 自定义JPQL查询
    @Query("SELECT u FROM User u WHERE u.status = :status AND SIZE(u.roles) > :roleCount")
    List<User> findUsersWithMultipleRoles(@Param("status") UserStatus status, @Param("roleCount") int roleCount);
    
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
    
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT DISTINCT o.user.id FROM Order o WHERE o.status = 'COMPLETED')")
    List<User> findUsersWithCompletedOrders();
    
    // 4. 原生SQL查询
    @Query(value = "SELECT * FROM users u WHERE u.created_at > DATE_SUB(NOW(), INTERVAL :days DAY)", nativeQuery = true)
    List<User> findRecentUsers(@Param("days") int days);
    
    @Query(value = "SELECT u.*, COUNT(o.id) as order_count FROM users u LEFT JOIN orders o ON u.id = o.user_id GROUP BY u.id HAVING order_count > :minOrders", nativeQuery = true)
    List<User> findUsersWithMinimumOrders(@Param("minOrders") int minOrders);
    
    // 5. 统计查询
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status")
    long countByStatus(@Param("status") UserStatus status);
    
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> getUserStatusStatistics();
    
    // 6. 更新查询
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :newStatus WHERE u.status = :oldStatus")
    int updateUserStatus(@Param("oldStatus") UserStatus oldStatus, @Param("newStatus") UserStatus newStatus);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.updatedAt = CURRENT_TIMESTAMP WHERE u.id = :userId")
    int touchUser(@Param("userId") Long userId);
    
    // 7. 删除查询
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.status = :status AND u.createdAt < :beforeDate")
    int deleteInactiveUsersBefore(@Param("status") UserStatus status, @Param("beforeDate") LocalDateTime beforeDate);
    
    // 8. EntityGraph查询 - 解决N+1问题
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndPermissions(@Param("id") Long id);
    
    @EntityGraph(attributePaths = {"userProfiles", "orders"})
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findByStatusWithProfilesAndOrders(@Param("status") UserStatus status);
    
    // 9. 投影查询
    @Query("SELECT u.id, u.username, u.email FROM User u WHERE u.status = :status")
    List<Object[]> findUserBasicInfo(@Param("status") UserStatus status);
    
    // 10. 命名查询（在实体类中定义）
    // 注意：这里使用命名查询，方法名对应实体类中的@NamedQuery
    @Query(name = "User.findByStatus")
    List<User> findUsersByStatusNamedQuery(@Param("status") UserStatus status);
    
    // 11. 存储过程调用
    @Query(value = "CALL GetUsersByAgeRange(:minAge, :maxAge)", nativeQuery = true)
    List<User> findUsersByAgeRange(@Param("minAge") int minAge, @Param("maxAge") int maxAge);
    
    // 12. 复杂的连接查询
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.roles r " +
           "LEFT JOIN FETCH r.permissions p " +
           "WHERE u.status = :status")
    List<User> findUsersWithRolesAndPermissions(@Param("status") UserStatus status);
    
    // 13. 条件查询
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "(:status IS NULL OR u.status = :status)")
    Page<User> findUsersByConditions(
        @Param("username") String username,
        @Param("email") String email,
        @Param("status") UserStatus status,
        Pageable pageable
    );
    
    // 14. 聚合查询
    @Query("SELECT DATE(u.createdAt), COUNT(u) FROM User u " +
           "WHERE u.createdAt >= :startDate " +
           "GROUP BY DATE(u.createdAt) " +
           "ORDER BY DATE(u.createdAt)")
    List<Object[]> getUserRegistrationStatistics(@Param("startDate") LocalDateTime startDate);
}