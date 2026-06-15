package com.kawai.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payment_Transactions")
@Getter
@Setter
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "booking_id", nullable = false)
    @ManyToOne(optional = false)
    private Booking booking;

    @JoinColumn(name = "invoice_id", nullable = false)
    @ManyToOne(optional = false)
    private ConsolidatedInvoice invoice;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    private String transactionRef; // internal

    private String vnpTxnRef; // gửi sang VNPAY

    private String vnpTransactionNo; // trả về từ VNPAY
    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;
}
