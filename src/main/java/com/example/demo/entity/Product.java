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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 产品实体类 - 演示更多Hibernate特性
 */
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"orderItems"})
@ToString(exclude = {"orderItems"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NamedQueries({
    @NamedQuery(
        name = "Product.findByCategory",
        query = "SELECT p FROM Product p WHERE p.category = :category AND p.status = 'ACTIVE'"
    ),
    @NamedQuery(
        name = "Product.findByPriceRange",
        query = "SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice"
    )
})
@SQLDelete(sql = "UPDATE products SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;
    
    @Column(name = "name", nullable = false, length = 200)
    private String name;
    
    @Column(name = "description", length = 1000)
    private String description;
    
    @Column(name = "category", nullable = false, length = 100)
    private String category;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
    
    @Column(name = "weight", precision = 8, scale = 3)
    private BigDecimal weight;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.ACTIVE;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 计算字段 - 演示@Formula注解
    @Formula("price - cost")
    private BigDecimal profit;
    
    // 一对多关联：产品-订单项
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Set<OrderItem> orderItems = new HashSet<>();
    
    // 产品状态枚举
    public enum ProductStatus {
        ACTIVE,
        INACTIVE,
        DISCONTINUED,
        OUT_OF_STOCK
    }
    
    // 便利方法
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public void reduceStock(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
        } else {
            throw new IllegalArgumentException("Insufficient stock");
        }
    }
    
    public void addStock(int quantity) {
        stockQuantity += quantity;
    }
}