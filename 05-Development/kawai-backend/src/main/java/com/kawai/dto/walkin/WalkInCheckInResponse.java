package com.kawai.dto.walkin;

/**
 * DTO trả về kết quả Walk-in Check-in thành công (UC-14).
 */
public class WalkInCheckInResponse {

    private Long bookingId;
    private String roomNumber;
    private String bookingStatus;
    private Long customerId;
    private boolean newCustomer;
    private int accompaniedGuestCount;

    // ── Getters & Setters ────────────────────────────────────────────────────
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long v) { this.bookingId = v; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String v) { this.roomNumber = v; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String v) { this.bookingStatus = v; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long v) { this.customerId = v; }

    public boolean isNewCustomer() { return newCustomer; }
    public void setNewCustomer(boolean v) { this.newCustomer = v; }

    public int getAccompaniedGuestCount() { return accompaniedGuestCount; }
    public void setAccompaniedGuestCount(int v) { this.accompaniedGuestCount = v; }

    private String newAccountUsername;
    private String newAccountPassword;

    public String getNewAccountUsername() { return newAccountUsername; }
    public void setNewAccountUsername(String v) { this.newAccountUsername = v; }

    public String getNewAccountPassword() { return newAccountPassword; }
    public void setNewAccountPassword(String v) { this.newAccountPassword = v; }
}
