package com.kawai.controllers.api;

import com.kawai.models.ExportHistory;
import com.kawai.repositories.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/export")
@PreAuthorize("hasAnyAuthority('OP_ANALYTICS', 'ROLE_ADMIN', 'ROLE_MANAGER')")
public class ExportApiController {

    private final ExportHistoryRepository exportHistoryRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final TourBookingRepository tourBookingRepository;

    public ExportApiController(ExportHistoryRepository exportHistoryRepository,
                               RoomBookingRepository roomBookingRepository,
                               FoodOrderRepository foodOrderRepository,
                               TourBookingRepository tourBookingRepository) {
        this.exportHistoryRepository = exportHistoryRepository;
        this.roomBookingRepository = roomBookingRepository;
        this.foodOrderRepository = foodOrderRepository;
        this.tourBookingRepository = tourBookingRepository;
    }

    @GetMapping
    public ResponseEntity<Resource> exportReport(
            @RequestParam("type") String type,
            @RequestParam("from") String fromDate,
            @RequestParam("to") String toDate,
            @RequestParam("format") String format) {

        LocalDate startDate = LocalDate.parse(fromDate);
        LocalDate endDate = LocalDate.parse(toDate);

        // Sinh dữ liệu CSV mẫu (hoặc thật tùy loại)
        StringBuilder csvBuilder = new StringBuilder();
        // BOM để Excel đọc được tiếng Việt UTF-8
        csvBuilder.append('\ufeff');

        String reportName = "";

        if ("revenue".equals(type)) {
            reportName = "Doanh thu tổng hợp (" + fromDate + " - " + toDate + ")";
            csvBuilder.append("Loại hình,Doanh thu (VNĐ)\n");

            BigDecimal roomTotal = roomBookingRepository.revenueBetween(startDate, endDate);
            BigDecimal fnbTotal = foodOrderRepository.revenueBetween(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());
            BigDecimal tourTotal = tourBookingRepository.revenueBetween(startDate, endDate);

            if (roomTotal == null) roomTotal = BigDecimal.ZERO;
            if (fnbTotal == null) fnbTotal = BigDecimal.ZERO;
            if (tourTotal == null) tourTotal = BigDecimal.ZERO;

            csvBuilder.append("Lưu trú & Phòng,").append(roomTotal).append("\n");
            csvBuilder.append("Dịch vụ ăn uống (F&B),").append(fnbTotal).append("\n");
            csvBuilder.append("Dịch vụ Tour,").append(tourTotal).append("\n");
            csvBuilder.append("Tổng doanh thu,").append(roomTotal.add(fnbTotal).add(tourTotal)).append("\n");

        } else if ("room".equals(type)) {
            reportName = "Báo cáo phòng (" + fromDate + " - " + toDate + ")";
            csvBuilder.append("Dữ liệu báo cáo phòng đang được cập nhật...\n");
        } else if ("fnb".equals(type)) {
            reportName = "Báo cáo F&B (" + fromDate + " - " + toDate + ")";
            csvBuilder.append("Dữ liệu báo cáo F&B đang được cập nhật...\n");
        } else if ("tour".equals(type)) {
            reportName = "Báo cáo Tour (" + fromDate + " - " + toDate + ")";
            csvBuilder.append("Dữ liệu báo cáo Tour đang được cập nhật...\n");
        } else {
            reportName = "Báo cáo " + type + " (" + fromDate + " - " + toDate + ")";
            csvBuilder.append("Dữ liệu đang được cập nhật...\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        
        String fileSizeStr = String.format("%.2f KB", csvBytes.length / 1024.0);

        // Lưu lịch sử xuất báo cáo
        ExportHistory history = new ExportHistory();
        history.setReportName(reportName);
        history.setFormat("CSV");
        history.setExportedAt(LocalDateTime.now());
        history.setExportedBy("Manager"); // Thực tế nên lấy từ SecurityContext
        history.setFileSize(fileSizeStr);
        exportHistoryRepository.save(history);

        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        String filename = "baocao_" + type + "_" + System.currentTimeMillis() + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }
}
