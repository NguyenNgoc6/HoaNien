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

    @Column(columnDefinition = "TEXT")
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "base_adults", nullable = false)
    private Integer baseAdults = 2;

    @Column(name = "base_children", nullable = false)
    private Integer baseChildren = 0;

    @Column(name = "max_adults", nullable = false)
    private Integer maxAdults = 2;

    @Column(name = "max_children", nullable = false)
    private Integer maxChildren = 1;

    @Column(name = "extra_adult_surcharge", nullable = false)
    private BigDecimal extraAdultSurcharge = BigDecimal.ZERO;

    @Column(name = "extra_child_surcharge", nullable = false)
    private BigDecimal extraChildSurcharge = BigDecimal.ZERO;

    public Integer getBaseAdults() {
        return baseAdults;
    }

    public void setBaseAdults(Integer baseAdults) {
        this.baseAdults = baseAdults;
    }

    public Integer getBaseChildren() {
        return baseChildren;
    }

    public void setBaseChildren(Integer baseChildren) {
        this.baseChildren = baseChildren;
    }

    public Integer getMaxAdults() {
        return maxAdults;
    }

    public void setMaxAdults(Integer maxAdults) {
        this.maxAdults = maxAdults;
    }

    public Integer getMaxChildren() {
        return maxChildren;
    }

    public void setMaxChildren(Integer maxChildren) {
        this.maxChildren = maxChildren;
    }

    public BigDecimal getExtraAdultSurcharge() {
        return extraAdultSurcharge;
    }

    public void setExtraAdultSurcharge(BigDecimal extraAdultSurcharge) {
        this.extraAdultSurcharge = extraAdultSurcharge;
    }

    public BigDecimal getExtraChildSurcharge() {
        return extraChildSurcharge;
    }

    public void setExtraChildSurcharge(BigDecimal extraChildSurcharge) {
        this.extraChildSurcharge = extraChildSurcharge;
    }
}