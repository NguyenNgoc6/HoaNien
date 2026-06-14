package com.kawai.services.interfaces;

import com.kawai.dto.RoomDashboardDTO;
import com.kawai.dto.RoomSearchRequestDTO;
import com.kawai.dto.RoomSearchResponseDTO;

import java.util.List;

/**
 * RoomService — UC09, UC11: Quản lý phòng & Front Desk Dashboard
 */
public interface RoomService {

    /**
     * Tìm kiếm phòng trống trong khoảng ngày chỉ định (UC09).
     * 
     * @param request thông tin tìm kiếm (checkIn, checkOut, filters)
     * @return Danh sách phòng trống, rỗng nếu không có
     */
    List<RoomSearchResponseDTO> searchAvailableRooms(RoomSearchRequestDTO request);

    /**
     * Lấy danh sách tất cả phòng kèm trạng thái hiện tại cho Front Desk Dashboard (UC11).
     * Trả về danh sách rỗng (không null) nếu hệ thống chưa có phòng.
     *
     * @return Danh sách phòng với trạng thái thời gian thực
     */
    List<RoomDashboardDTO> getRoomDashboard();
}
