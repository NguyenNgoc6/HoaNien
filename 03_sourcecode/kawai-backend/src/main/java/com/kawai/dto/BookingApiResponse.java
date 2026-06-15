package com.kawai.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 2: * DTO chuẩn hóa phản hồi từ API Booking (UC10).
 * 3:
 */
public class BookingApiResponse {

    private String status;
    private String bookingStatus; // Actual booking status: CONFIRMED, PENDING
    private Long bookingId;
    private BigDecimal depositAmount;
    private BigDecimal discountedPrice;  // Tổng chính thức từ backend (sau phụ thu + khuyến mãi)
    private LocalDateTime cancellationDeadline;
    private String message;
    private String paymentUrl;

    public BookingApiResponse() {
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public BookingApiResponse(String status, Long bookingId, BigDecimal depositAmount,
            LocalDateTime cancellationDeadline, String message) {
        this.status = status;
        this.bookingId = bookingId;
        this.depositAmount = depositAmount;
        this.cancellationDeadline = cancellationDeadline;
        this.message = message;
    }

    public BookingApiResponse(String status, String bookingStatus, Long bookingId,
            BigDecimal depositAmount, LocalDateTime cancellationDeadline, String message) {
        this.status = status;
        this.bookingStatus = bookingStatus;
        this.bookingId = bookingId;
        this.depositAmount = depositAmount;
        this.cancellationDeadline = cancellationDeadline;
        this.message = message;
    }

    // Getters / Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public LocalDateTime getCancellationDeadline() {
        return cancellationDeadline;
    }

    public void setCancellationDeadline(LocalDateTime cancellationDeadline) {
        this.cancellationDeadline = cancellationDeadline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
}
