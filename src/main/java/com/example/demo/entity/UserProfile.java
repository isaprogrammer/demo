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

/**
 * 用户档案实体类 - 演示一对多关联关系
 */
@Entity
@Table(name = "user_profiles", indexes = {
    @Index(name = "idx_profile_type", columnList = "profile_type"),
    @Index(name = "idx_profile_user_id", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user"})
@ToString(exclude = {"user"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "profile_type", nullable = false, length = 50)
    private String profileType;
    
    @Column(name = "profile_key", nullable = false, length = 100)
    private String profileKey;
    
    @Column(name = "profile_value", length = 500)
    private String profileValue;
    
    @Column(name = "description", length = 200)
    private String description;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 多对一关联：档案-用户
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}