package com.kawai.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Room_Categories")
public class RoomCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;
    @Column(name = "category_name", nullable = false)
    private String categoryName;
    @Column(name = "base_price", nullable = false)
    private BigDecimal basePrice;
    @Column(nullable = false)
    private Integer capacity;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        this.id = v;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String v) {
        this.categoryName = v;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal v) {
        this.basePrice = v;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer v) {
        this.capacity = v;
    }
}