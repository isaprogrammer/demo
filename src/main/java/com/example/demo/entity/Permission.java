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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 权限实体类 - 演示多对多关联关系和二级缓存
 */
@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_permission_code", columnList = "code"),
    @Index(name = "idx_permission_module", columnList = "module")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
    @NamedQuery(
        name = "Permission.findByModule",
        query = "SELECT p FROM Permission p WHERE p.module = :module ORDER BY p.name"
    ),
    @NamedQuery(
        name = "Permission.findActivePermissions",
        query = "SELECT p FROM Permission p WHERE p.enabled = true ORDER BY p.module, p.name"
    )
})
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "module", nullable = false, length = 50)
    private String module;
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 多对多关联：权限-角色
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<Role> roles = new HashSet<>();
    
    // 便利方法
    public void addRole(Role role) {
        roles.add(role);
        role.getPermissions().add(this);
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
        role.getPermissions().remove(this);
    }
}