CREATE OR REPLACE VIEW recent_booking_info AS
SELECT
    tb.booking_id AS booking_id,
    c.full_name AS customer_name,
    c.email AS customer_email,
    c.phone AS customer_phone,
    COUNT(rb.room_booking_id) AS room_quantity,
    GROUP_CONCAT(r.room_name SEPARATOR ', ') AS room_names
FROM Tour_Bookings tb
JOIN Bookings b ON tb.booking_id = b.booking_id
JOIN Customers c ON b.customer_id = c.customer_id
LEFT JOIN Room_Bookings rb ON rb.booking_id = b.booking_id
LEFT JOIN Rooms r ON rb.room_id = r.room_id
GROUP BY tb.booking_id, c.full_name, c.email, c.phone;
