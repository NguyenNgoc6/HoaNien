package com.kawai.exceptions;

/**
 * Ném ra khi phòng không còn trống trong khoảng ngày đã chọn.
 * HTTP status: 409 Conflict (BR-FO-01, UC10)
 */
public class RoomNotAvailableException extends BusinessException {

    public RoomNotAvailableException(String message) {
        super("ROOM_OVERLAP", message);
    }

    public RoomNotAvailableException(String message, Throwable cause) {
        super("ROOM_OVERLAP", message);
    }
}
