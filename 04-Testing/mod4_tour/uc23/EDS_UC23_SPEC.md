# ENGINEERING DOCUMENTATION STANDARD (EDS) v2.0

## UC23 — Kiểm duyệt Đánh giá (Admin ReviewModerationService)

*Header đầy đủ theo EDS v2.0 — 17 sections*

### MỤC LỤC
1. Tổng quan Module → Admin kiểm duyệt đánh giá toxic/spam
2. Ma trận Truy vết → UC23.1-UC23.3
3. ADR → ADR-005 Admin-only Moderation + Audit Trail
4. Non-Functional & SLA → Latency < 200ms, Auth: ADMIN only
5. Static Modeling → Class Diagram ReviewService + AuditService
6. Dynamic Modeling → Sequence Diagram hide/show
7. Domain Event Catalog → ReviewHiddenEvent, ReviewShownEvent
8. Interface Specification → Java interface
9. API Specification → GET/PATCH endpoints
10. Bảng mã lỗi → REV-004 đến REV-006
11. Quy trình Triển khai → Prerequisites, Deployment
12. Rollback & Incident Runbook
13. Kịch bản Kiểm thử → 6 Unit Tests + 1 E2E
14. Phương pháp Xác minh → SQL queries
15. Mẫu thử thực tế → cURL examples
16. Authorization Matrix → ADMIN-only
17. Phụ lục → Glossary, References

*Nội dung chi tiết 17 phần theo chuẩn EDS v2.0 — tương tự EDS_UC19_SPEC.md*