package com.hoanien.repositories;

import com.hoanien.models.RoomBookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomBookingDetailRepository extends JpaRepository<RoomBookingDetail, Long> {
}
