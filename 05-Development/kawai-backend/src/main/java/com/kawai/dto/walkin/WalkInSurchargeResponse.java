package com.kawai.dto.walkin;

import java.math.BigDecimal;

/**
 * DTO trả về kết quả tính phí phụ thu dựa trên số khách trong luồng Walk-in Check-in.
 */
public class WalkInSurchargeResponse {

    private BigDecimal surchargeAmount;
    private int adultsCount;
    private int childrenCount;
    private String message;

    public WalkInSurchargeResponse() {
    }

    public WalkInSurchargeResponse(BigDecimal surchargeAmount, int adultsCount, int childrenCount, String message) {
        this.surchargeAmount = surchargeAmount;
        this.adultsCount = adultsCount;
        this.childrenCount = childrenCount;
        this.message = message;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

    public int getAdultsCount() {
        return adultsCount;
    }

    public void setAdultsCount(int adultsCount) {
        this.adultsCount = adultsCount;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
