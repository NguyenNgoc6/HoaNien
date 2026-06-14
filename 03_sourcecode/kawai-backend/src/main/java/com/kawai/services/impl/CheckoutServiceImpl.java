package com.kawai.services.impl;

import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.services.interfaces.CheckoutService;
import com.kawai.services.interfaces.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private final FolioItemRepository folioItemRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;
    private final EmailService emailService;

    public CheckoutServiceImpl(FolioItemRepository folioItemRepository,
            RoomBookingDetailRepository roomBookingDetailRepository,
            EmailService emailService) {
        this.folioItemRepository = folioItemRepository;
        this.roomBookingDetailRepository = roomBookingDetailRepository;
        this.emailService = emailService;
    }

    @Override
    public void processCheckout(Long roomBookingDetailId) {
        throw new UnsupportedOperationException("Unimplemented method 'processCheckout'");
    }

    @Override
    public void settleFolio(Long roomBookingDetailId, BigDecimal amount, String paymentMethod) {
        throw new UnsupportedOperationException("Unimplemented method 'settleFolio'");
    }
}
