package com.kawai.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "Bookings")
@Inheritance(strategy = InheritanceType.JOINED)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    @Column(name = "booking_status", nullable = false)
    private String bookingStatus = "Pending";
    @Column(name = "booking_source", nullable = false)
    private String bookingSource = "Direct_Web";
    @Column(nullable = false)
    private Integer version = 1;

    /**
     * Thời điểm HOLD hết hạn (chỉ set khi bookingStatus = 'HOLD').
     * Null đối với booking CONFIRMED/CANCELLED.
     * Scheduler dùng field này để phát hiện HOLD quá TTL.
     */
    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long v) {
        this.id = v;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer v) {
        this.customer = v;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate v) {
        this.bookingDate = v;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal v) {
        this.totalPrice = v;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String v) {
        this.bookingStatus = v;
    }

    public String getBookingSource() {
        return bookingSource;
    }

    public void setBookingSource(String v) {
        this.bookingSource = v;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer v) {
        this.version = v;
    }

    public LocalDateTime getHoldExpiresAt() {
        return holdExpiresAt;
    }

    public void setHoldExpiresAt(LocalDateTime holdExpiresAt) {
        this.holdExpiresAt = holdExpiresAt;
    }

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
