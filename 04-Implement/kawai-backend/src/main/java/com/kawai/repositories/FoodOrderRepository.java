package com.kawai.repositories;

import com.kawai.models.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {

    @Query("SELECT fo.orderStatus, COUNT(fo) FROM FoodOrder fo GROUP BY fo.orderStatus")
    List<Object[]> countByStatus();

    @Query("SELECT fo.paymentType, COUNT(fo) FROM FoodOrder fo GROUP BY fo.paymentType")
    List<Object[]> countByPaymentType();

    @Query("SELECT fo.orderType, COUNT(fo) FROM FoodOrder fo GROUP BY fo.orderType")
    List<Object[]> countByOrderType();

    @Query("SELECT COALESCE(SUM(d.priceAtOrder * d.quantity), 0) FROM FoodOrderDetail d WHERE d.foodOrder.orderStatus = 'Completed'")
    BigDecimal totalCompletedRevenue();

    List<FoodOrder> findByBooking_Customer(com.kawai.models.Customer customer);

    @Query("SELECT DISTINCT fo FROM FoodOrder fo LEFT JOIN FETCH fo.details d LEFT JOIN FETCH d.menuItem WHERE fo.roomBookingDetail.id = :bookingDetailId ORDER BY fo.id DESC")
    List<FoodOrder> findByBookingDetailWithDetails(@org.springframework.data.repository.query.Param("bookingDetailId") Long bookingDetailId);
}