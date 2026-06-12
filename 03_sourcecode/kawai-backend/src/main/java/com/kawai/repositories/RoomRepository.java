package com.kawai.repositories;

import com.kawai.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    java.util.Optional<Room> findByRoomNumber(String roomNumber);

    @Query("SELECT r.roomStatus, COUNT(r) FROM Room r GROUP BY r.roomStatus")
    List<Object[]> countByStatus();
}