package com.kawai.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Kết quả trả về khi tìm kiếm phòng trống (UC09).
 */
public class RoomSearchResponseDTO {

    private Long roomId;
    private String roomNumber;
    private String categoryName;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String description;
    private String beds;
    private String size;
    private String view;
    private List<String> amenities;
    private Integer baseAdults;
    private Integer baseChildren;
    private Integer maxAdults;
    private Integer maxChildren;
    private BigDecimal extraAdultSurcharge;
    private BigDecimal extraChildSurcharge;

    public RoomSearchResponseDTO() {
    }

    // Getters / Setters
    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long v) {
        this.roomId = v;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String v) {
        this.roomNumber = v;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String v) {
        this.categoryName = v;
    }

    public BigDecimal getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(BigDecimal v) {
        this.pricePerNight = v;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer v) {
        this.capacity = v;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate v) {
        this.checkInDate = v;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate v) {
        this.checkOutDate = v;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String v) {
        this.description = v;
    }

    public String getBeds() {
        return beds;
    }

    public void setBeds(String v) {
        this.beds = v;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String v) {
        this.size = v;
    }

    public String getView() {
        return view;
    }

    public void setView(String v) {
        this.view = v;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> v) {
        this.amenities = v;
    }

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