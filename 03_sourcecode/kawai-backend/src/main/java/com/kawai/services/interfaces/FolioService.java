package com.kawai.services.interfaces;

import com.kawai.models.FolioItem;
import java.util.List;

public interface FolioService {

    /**
     * Retrieve all FolioItem entries for a given booking.
     * @param bookingId the ID of the booking
     * @return list of FolioItem objects associated with the booking
     */
    List<FolioItem> getFolioItemsForBooking(Long bookingId);

    /**
     * Add a payment record to a booking's folio.
     * @param bookingId the booking ID
     * @param amount the amount paid
     * @param paymentMethod the payment method used
     * @param description the description of the payment
     */
    void addPayment(Long bookingId, java.math.BigDecimal amount, String paymentMethod, String description);
    /**
     * Calculate the total folio balance for a given booking.
     * @param bookingId the booking ID
     * @return the total balance
     */
    java.math.BigDecimal calculateTotalFolio(Long bookingId);
    /**
     * Run the night audit process to charge for occupied rooms.
     */
    void runNightAudit();

    /**
     * Increment the business date (usually called after night audit).
     */
    void incrementBusinessDate();

}
