package com.kawai.repositories;

import com.kawai.models.ConsolidatedInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsolidatedInvoiceRepository extends JpaRepository<ConsolidatedInvoice, Long> {
    java.util.Optional<ConsolidatedInvoice> findByBooking_Id(Long bookingId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM ConsolidatedInvoice i WHERE i.invoiceStatus = 'Error' OR i.invoiceStatus = 'Failed'")
    long countErrorInvoices();
}
