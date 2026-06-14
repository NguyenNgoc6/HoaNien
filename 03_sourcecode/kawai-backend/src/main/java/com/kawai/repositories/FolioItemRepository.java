package com.kawai.repositories;

import com.kawai.models.FolioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho FolioItem entity.
 * Lưu vết các khoản phí (ăn uống, tour, ...) được ghi nợ vào Folio phòng.
 */
@Repository
public interface FolioItemRepository extends JpaRepository<FolioItem, Long> {

    /**
     * Lấy toàn bộ FolioItem thuộc một chi tiết đặt phòng cụ thể.
     * Dùng trong UC25.1 (kiểm tra Folio sạch trước Check-out) và
     * UC25.2 (tất toán từng khoản nợ).
     *
     * @param roomBookingDetailId ID của RoomBookingDetail
     * @return danh sách FolioItem, rỗng nếu không có khoản nào
     */
    List<FolioItem> findByRoomBookingDetailId(Long roomBookingDetailId);
}