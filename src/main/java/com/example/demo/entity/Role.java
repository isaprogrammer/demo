package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类 - 演示多对多关联关系和二级缓存
 */
@Entity
@Table(name = "roles", indexes = {
    @Index(name = "idx_role_name", columnList = "name"),
    @Index(name = "idx_role_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"users", "permissions"})
@ToString(exclude = {"users", "permissions"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
    @NamedQuery(
        name = "Role.findByStatus",
        query = "SELECT r FROM Role r WHERE r.status = :status ORDER BY r.name"
    ),
    @NamedQuery(
        name = "Role.findActiveRoles",
        query = "SELECT r FROM Role r WHERE r.status = 'ACTIVE' AND r.enabled = true"
    )
})
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RoleStatus status = RoleStatus.ACTIVE;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @Column(name = "priority")
    private Integer priority = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 多对多关联：角色-用户
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnore
    private Set<User> users = new HashSet<>();
    
    // 多对多关联：角色-权限
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"),
        indexes = {
            @Index(name = "idx_role_permission_role", columnList = "role_id"),
            @Index(name = "idx_role_permission_permission", columnList = "permission_id")
        }
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Permission> permissions = new HashSet<>();
    
    // 便利方法
    public void addUser(User user) {
        users.add(user);
        user.getRoles().add(this);
    }
    
    public void removeUser(User user) {
        users.remove(user);
        user.getRoles().remove(this);
    }
    
    public void addPermission(Permission permission) {
        permissions.add(permission);
        permission.getRoles().add(this);
    }
    
    public void removePermission(Permission permission) {
        permissions.remove(permission);
        permission.getRoles().remove(this);
    }
    
    public enum RoleStatus {
        ACTIVE, INACTIVE, DEPRECATED
    }
}