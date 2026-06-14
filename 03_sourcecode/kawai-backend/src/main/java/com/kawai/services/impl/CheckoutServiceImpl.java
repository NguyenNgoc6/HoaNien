package com.kawai.services.impl;

import com.kawai.models.FolioItem;
import com.kawai.models.RoomBookingDetail;
import com.kawai.repositories.FolioItemRepository;
import com.kawai.repositories.RoomBookingDetailRepository;
import com.kawai.services.interfaces.CheckoutService;
import com.kawai.services.interfaces.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Triển khai nghiệp vụ Check-out & Tất toán Folio — Module 5 (MOD5).
 *
 * <p>Business Rules tuân thủ:
 * <ul>
 *   <li>BR-FIN-01: Tuyệt đối không cho Check-out nếu Folio Balance ≠ 0 → lỗi FOLIO-001.</li>
 *   <li>BR-FIN-06: Ghi nhận từng khoản thanh toán; mỗi {@link FolioItem} phải được
 *       đánh dấu {@code isSettledSeparately = true} trước khi kết thúc giao dịch.</li>
 *   <li>BR-FO-09: Sau Check-out thành công, phòng tự động chuyển trạng thái → {@code DIRTY}
 *       để kích hoạt quy trình dọn phòng Housekeeping.</li>
 * </ul>
 *
 * <p>Use Cases được phục vụ:
 * <ul>
 *   <li>UC25.1 — Xử lý thanh toán Check-out cuối cùng ({@link #processCheckout(Long)})</li>
 *   <li>UC25.2 — Tự động kích hoạt e-Invoice ({@link #settleFolio(Long, BigDecimal, String)})</li>
 * </ul>
 *
 * @author Ngo Thi Ngoc Lan (MOD5)
 */
@Service
public class CheckoutServiceImpl implements CheckoutService {

    /**
     * Trạng thái phòng sau khi khách Check-out thành công.
     * Theo BR-FO-09: phòng phải chuyển DIRTY để Housekeeping tiến hành dọn phòng.
     */
    private static final String ROOM_STATUS_DIRTY = "DIRTY";

    /**
     * Mã lỗi FOLIO-001 (BR-FIN-01): Không cho Check-out khi còn nợ chưa tất toán.
     * Giữ dưới dạng constant để dễ dàng tra cứu trong log và message error.
     */
    private static final String ERROR_FOLIO_UNPAID = "FOLIO-001";

    private final FolioItemRepository folioItemRepository;
    private final RoomBookingDetailRepository roomBookingDetailRepository;
    private final EmailService emailService;

    /**
     * Constructor injection — Spring tự động tiêm phụ thuộc.
     *
     * @param folioItemRepository         repository truy vấn khoản nợ Folio
     * @param roomBookingDetailRepository repository truy vấn chi tiết đặt phòng
     * @param emailService                service gửi e-Invoice tự động (UC25.2)
     */
    public CheckoutServiceImpl(FolioItemRepository folioItemRepository,
                               RoomBookingDetailRepository roomBookingDetailRepository,
                               EmailService emailService) {
        this.folioItemRepository = folioItemRepository;
        this.roomBookingDetailRepository = roomBookingDetailRepository;
        this.emailService = emailService;
    }

    // =========================================================================
    // UC25.1 — Xử lý thanh toán Check-out cuối cùng
    // =========================================================================

    /**
     * Xử lý quy trình Check-out cho một chi tiết đặt phòng (UC25.1).
     *
     * <p>Luồng xử lý:
     * <ol>
     *   <li>Tìm {@link RoomBookingDetail} — nếu không tồn tại ném exception.</li>
     *   <li>Tải toàn bộ {@link FolioItem} của phòng và kiểm tra bằng
     *       {@link #hasUnpaidFolioItems(List)}.</li>
     *   <li>Nếu còn nợ → ném FOLIO-001 (BR-FIN-01), giữ nguyên trạng thái phòng.</li>
     *   <li>Nếu sạch → gọi {@link #markRoomDirtyAndSave(RoomBookingDetail)} (BR-FO-09).</li>
     * </ol>
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng cần Check-out
     * @throws RuntimeException mã FOLIO-001 nếu còn khoản nợ chưa tất toán (BR-FIN-01)
     * @throws RuntimeException nếu không tìm thấy RoomBookingDetail theo ID
     */
    @Override
    public void processCheckout(Long roomBookingDetailId) {
        RoomBookingDetail detail = findRoomBookingDetailOrThrow(roomBookingDetailId);
        List<FolioItem> folioItems = folioItemRepository.findByRoomBookingDetailId(roomBookingDetailId);

        // BR-FIN-01: Chặn Check-out nếu còn bất kỳ khoản nợ nào chưa tất toán
        if (hasUnpaidFolioItems(folioItems)) {
            throw new RuntimeException(
                ERROR_FOLIO_UNPAID + ": Hóa đơn chưa thanh toán hết — " +
                "không thể thực hiện Check-out. Vui lòng tất toán toàn bộ Folio trước khi rời đi."
            );
        }

        // BR-FO-09: Folio sạch → chuyển phòng DIRTY để Housekeeping dọn phòng
        markRoomDirtyAndSave(detail);
    }

    // =========================================================================
    // UC25.2 — Tất toán Folio & Kích hoạt e-Invoice tự động
    // =========================================================================

    /**
     * Tất toán toàn bộ khoản nợ Folio và gửi e-Invoice qua email (UC25.1 + UC25.2).
     *
     * <p>Luồng xử lý:
     * <ol>
     *   <li>Tải danh sách {@link FolioItem} của phòng.</li>
     *   <li>Duyệt và đánh dấu từng khoản chưa tất toán bằng
     *       {@link #settleItem(FolioItem)} (BR-FIN-06).</li>
     *   <li>Sau khi toàn bộ khoản settled → kích hoạt gửi e-Invoice
     *       qua {@link EmailService#sendInvoiceEmail(Long, BigDecimal)} (UC25.2).</li>
     * </ol>
     *
     * @param roomBookingDetailId ID của chi tiết đặt phòng cần tất toán
     * @param amount              tổng số tiền thanh toán (BigDecimal, BR-FIN-05)
     * @param paymentMethod       hình thức thanh toán: CASH / CREDIT_CARD / TRANSFER
     */
    @Override
    public void settleFolio(Long roomBookingDetailId, BigDecimal amount, String paymentMethod) {
        List<FolioItem> folioItems = folioItemRepository.findByRoomBookingDetailId(roomBookingDetailId);

        // BR-FIN-06: Đánh dấu từng khoản đã thanh toán và lưu DB
        folioItems.stream()
                  .filter(item -> !Boolean.TRUE.equals(item.getIsSettledSeparately()))
                  .forEach(this::settleItem);

        // Tự tính tổng tiền từ DB (BR-FIN-05) thay vì tin vào amount do caller truyền vào
        BigDecimal calculatedTotal = calculateTotalFolioAmount(folioItems);

        // UC25.2: Sau khi tất toán xong → tự động gửi e-Invoice (SendGrid)
        emailService.sendInvoiceEmail(roomBookingDetailId, calculatedTotal);
    }

    // =========================================================================
    // Private helpers — tái sử dụng, không trùng lặp (Clean Code)
    // =========================================================================

    /**
     * Tìm {@link RoomBookingDetail} theo ID hoặc ném exception nếu không tồn tại.
     *
     * @param roomBookingDetailId ID cần tra cứu
     * @return {@link RoomBookingDetail} tương ứng
     * @throws RuntimeException nếu không tìm thấy
     */
    private RoomBookingDetail findRoomBookingDetailOrThrow(Long roomBookingDetailId) {
        return roomBookingDetailRepository.findById(roomBookingDetailId)
                .orElseThrow(() -> new RuntimeException(
                        "RoomBookingDetail không tìm thấy: id=" + roomBookingDetailId));
    }

    /**
     * Kiểm tra xem danh sách FolioItem có khoản nào chưa tất toán không.
     *
     * <p>Một khoản được xem là "chưa tất toán" khi {@code isSettledSeparately = false}.
     *
     * @param folioItems danh sách FolioItem cần kiểm tra
     * @return {@code true} nếu còn ít nhất 1 khoản chưa tất toán
     */
    private boolean hasUnpaidFolioItems(List<FolioItem> folioItems) {
        return folioItems.stream()
                         .anyMatch(item -> !Boolean.TRUE.equals(item.getIsSettledSeparately()));
    }

    /**
     * Đánh dấu một FolioItem là đã tất toán và lưu vào DB (BR-FIN-06).
     *
     * @param item FolioItem cần đánh dấu settled
     */
    private void settleItem(FolioItem item) {
        item.setIsSettledSeparately(true);
        folioItemRepository.save(item);
    }

    /**
     * Chuyển trạng thái phòng sang DIRTY và lưu DB (BR-FO-09).
     * Được gọi sau khi xác nhận Folio sạch trong {@link #processCheckout(Long)}.
     *
     * @param detail chi tiết đặt phòng cần cập nhật trạng thái
     */
    private void markRoomDirtyAndSave(RoomBookingDetail detail) {
        detail.setDetailStatus(ROOM_STATUS_DIRTY);
        roomBookingDetailRepository.save(detail);
    }

    /**
     * Tính tổng tiền của toàn bộ FolioItem trong một lần lưu trú (BR-FIN-05).
     *
     * <p>Cộng dồn {@code amount} của tất cả các FolioItem (bất kể đã settled hay chưa)
     * bằng BigDecimal với RoundingMode.HALF_UP (BR-FIN-05).
     * Đây là con số được gửi trong e-Invoice — lấy từ DB thay vì tin vào input của caller.
     *
     * @param folioItems danh sách FolioItem cần cộng tổng
     * @return tổng tiền BigDecimal, {@code BigDecimal.ZERO} nếu danh sách rỗng
     */
    private BigDecimal calculateTotalFolioAmount(List<FolioItem> folioItems) {
        return folioItems.stream()
                         .map(FolioItem::getAmount)
                         .reduce(BigDecimal.ZERO, BigDecimal::add)
                         .setScale(0, RoundingMode.HALF_UP);
    }
}
