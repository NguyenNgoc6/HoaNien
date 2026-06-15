package com.hoanien.repositories;

import com.hoanien.models.FoodOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOrderDetailRepository extends JpaRepository<FoodOrderDetail, Long> {
}
