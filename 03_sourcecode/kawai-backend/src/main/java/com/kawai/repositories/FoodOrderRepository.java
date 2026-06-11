package com.kawai.repositories;

import com.kawai.models.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderRepository extends JpaRepository<FoodOrder, Long> {
    long countByOrderStatusIn(List<String> statuses);
    long countByOrderTypeAndOrderStatus(String orderType, String orderStatus);
}
