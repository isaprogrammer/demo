package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类 - 演示Hibernate各种高级特性
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_status", columnList = "status"),
    @Index(name = "idx_user_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"roles", "userProfiles", "orders"})
@ToString(exclude = {"roles", "userProfiles", "orders"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
    @NamedQuery(
        name = "User.findByStatus",
        query = "SELECT u FROM User u WHERE u.status = :status ORDER BY u.createdAt DESC"
    ),
    @NamedQuery(
        name = "User.findActiveUsers",
        query = "SELECT u FROM User u WHERE u.status = 'ACTIVE' ORDER BY u.username"
    ),
    @NamedQuery(
        name = "User.countByStatus",
        query = "SELECT COUNT(u) FROM User u WHERE u.status = :status"
    )
})
@FilterDef(name = "activeUsersFilter", parameters = @ParamDef(name = "status", type = String.class))
@Filter(name = "activeUsersFilter", condition = "status = :status")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "full_name", length = 100)
    private String fullName;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 多对多关联：用户-角色
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Role> roles = new HashSet<>();
    
    // 一对多关联：用户-用户档案（演示一对多关系）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<UserProfile> userProfiles = new HashSet<>();
    
    // 一对多关联：用户-订单（演示延迟加载）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Order> orders = new HashSet<>();
    
    // 便利方法
    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
        role.getUsers().remove(this);
    }
    
    public void addUserProfile(UserProfile profile) {
        userProfiles.add(profile);
        profile.setUser(this);
    }
    
    public void removeUserProfile(UserProfile profile) {
        userProfiles.remove(profile);
        profile.setUser(null);
    }
    
    public void addOrder(Order order) {
        orders.add(order);
        order.setUser(this);
    }
    
    public void removeOrder(Order order) {
        orders.remove(order);
        order.setUser(null);
    }
    
    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}