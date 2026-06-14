**TEST-DRIVEN DEVELOPMENT SPECIFICATION TEMPLATE** 

**Mẫu Đặc tả Kiểm thử Hướng Phát triển** 

**Document ID:** FPT\-EDU\-TDD\-TEMPLATE\-001 

**Version:** 1.0 

**Date:** YYYY-MM-DD 

**Status:** Draft In Review | Approved 

**Standard:** ISO/IEC/IEEE 29119-3:2021 Software Testing Part 3: Test Documentation 

**Author**: \[Tên \[Vai trò\] 

**Reviewed by:** \[ \] \[Tên\] \- Pending 

**DPO Sign\-off:** \[ \] Pending 

**Approved by:** \[ \] Pending   
\- 

**Classification:** Internal Confidential 

**References**: 

04\_testing/SOFTWARE\_TEST\_PLAN.md (FPT\-EDU\-STP\-001 v2.0) \- Master Test Plan 01\_Requirements/SRS.md \- Functional requirements 

03\_implement/\[TECH\-SPEC\-ID\]\_\[FeatureName\].md \- Technical Specification 02\_Design/ADR/ADR\-0XX \- Architecture Decision Records liên quan 

\[Điều luật\] —Legal basis (Luật 91/2025, NĐ 356/2025) 

**Quy ước TDD**: Tài liệu này mô tả test cases TRƯỚC khi viết production code. 

Thứ tự bắt buộc: viết test (.spec.ts) — chạy — xác nhận FAIL 

refactor 

Không mark test là nếu npm test chưa xanh. 

Test data dùng tenant fpt\-edu (QA). Không dùng PII thật.   
→   
implement → PASS 

**CHANGELOG** 

**| Policy 4.4 — Immutable History**: Không bao giờ xóa thông tin cũ.   
**Ngày**   
**Người thực hiện** 

YYYY\-MM\-DD   
\[Tên\] 

**MỤC LỤC** 

1\. Thông tin Module 

2\. Logic Issues Resolved 

3\. Test Design Specification (TDS) 

4\. Test Case Specification 

5\. Red-Green-Refactor Tracker 

6\. Entry / Exit Criteria 

7\. Rollback Plan 

**1\. Thông tin Module** 

**Field**   
**Nội dung thay đổi** 

Khởi tạo tài liệu — TDD spec cho \[Feature 

**Value** 

**Feature / Gap ID**   
GAP\-XXXX 

\[Tên module \- Bounded Context\]   
**Module** 

**Spec gốc** 

**Priority** 

**Sprint** 

**Milestone** 

**Data Classification** 

**Compliance Scope** 

**Upstream Dependencies** 

**Downstream Consumers**   
\[FPT\-EDU\-TECH\-SPEC\-XXX\-**NNN\]** 

PO/ P1/ P2 

S\[N\] (YYYY\-MM\-DD → YYYY\-MM\-DD) 

M3 Alpha   
2026-07-11 

Sensitive\-**PII** / **PII /** Internal 

Luật 91/2025 Điều X / **NĐ** 356/2025 Điều X 

\[Service/Module phụ thuộc\] 

\[Service/Module tiêu thụ kết quả\]   
**2\. Logic Issues Resolved** 

**Bắt buộc điền trước khi viết test**. 

Liệt kê mọi sai lệch giữa spec thiết kế và schema/policy/codebase thực tế. Test cases sẽ encode hành vi **đã sửa**, không phải hành vi trong spec gốc. 

**\#**   
**Spec gốc (sai / thiếu**)   
**Thực tế (schema / policy)**   
**Fix áp dụng trong** 

**test** 

*(đoạn code / logic trong*   
L1 

*spec)*   
*(field name/enum / CLAUDE.md policy)*   
*(hành vi đúng cần test)* 

L2 

**3\. Test Design Specification (TDS)** 

**TDS\-01 — Scope / Phạm vi** 

| Mô tả phạm vi của TDD spec này: component nào được kiểm thử, layer nào được bao phủ. 

\[Module\] bao gồm các layer: 

Domain (pure **logic** no deps) 

Application / Use Cases (mock Prisma inline) 

Services (mock Prisma inline) 

Controller (mock use cases) 

Integration (Testcontainers PostgreSQL **\+** Redis) 

**TDS\-02 — Test Basis / Cơ sở Kiểm thử** 

| Điều kiện kiểm thử được derive từ các nguồn sau: 

**Source** 

SRS.md UC-XX 

**ADR**\-OXX   
**Items Derived** 

*(hành vi người dùng / business rule)* 

*(architecture constraint)* 

BR\-XXXX\-001 

Luật 91/2025 Điều X   
*(business rule số hiệu*) 

*(yêu cầu pháp lý)*   
**Source**   
**Items Derived** 

NĐ 356/2025 Điều X   
*(yêu cầu nghị định)* 

\[TECH\-SPEC\-ID\] §X   
*(algorithm / logic từ spec kỹ thuật)* 

**TDS\-03 \- Test Conditions and Coverage Items** 

| Mỗi condition map sang ≥ 1 test case cụ 

**Condition ID**   
**Test Condition** 

TC-COND-001   
thê. 

**Coverage Item** 

TC\-COND\-002   
*(điều kiện nghiệp vụ) (method/class*/*API endpoint)*   
**Test Cases** 

\[MODULE\]-TC\-001 

**TDS\-04 — Test Techniques / Kỹ thuật Kiểm thử** 

**Technique (ISO 29119-4)**   
**Applied To**   
**Rationale** 

Equivalence Partitioning   
*(input domain)*   
*(lý do)* 

Boundary Value Analysis   
*(boundary case)*   
*(lý do)* 

State Transition Testing   
(*FSM/status enum)*   
*(lý do)* 

Error Guessing   
*(security/attack vectors*)   
*(lý do)* 

**TDS-05\-Test Data Requirements** 

**Fixture ID**   
**Туре**   
**Value / Logic**   
**Mục đích** 

FX\-001   
DB seed   
{ id, status: 'APPROVED',   
}   
Happy path 

FX\-002   
DB seed   
{ id, status: 'SUPERSEDED' }   
Version reject 

FX\-003   
env   
HMAC\_SECRET\=test\-secret\-32chars   
HMAC computation 

FX\-004   
JWT   
{sub: 'sub\-001', role: 'DPO' }   
Auth context 

**4\. Test Case Specification** 

**TC ID format:** \[MODULE\]-TC-\[NNN\] 

**Severity:** CRITICAL/HIGH/MEDIUM / LOW (theo CVSS)   
**Status**: Not written/ Written\-failing/ Passing 

**\[MODULE\]-TC\-001 — \[Tên test case ngắn gọn\]**   
— 

**Severity:** CRITICAL | HIGH | MEDIUM | LOW 

**CWE:** CWE\-XXX – \[Tên CWE nếu là security test\] 

**Legal:** \[Luật / Nghị định / ADR áp dụng nếu có\] 

**Feature Under Test:** \[ClassName.methodName() / API endpoint / React component\] **Test File:** \[ đường dẫn file .spec.ts hoặc .test.tsx\] 

**TDD Phase**: RED — chưa implement 

**Condition Ref:** TC\-COND\-001 

**Preconditions:** 

(*trạng* thái *DB* / *service / env cần có trước khi chạy test*) 

*(fixture ID cần thiết: FX\-001, FX\-003*, ...) 

**Test Steps:** 

1\. *(Arrange: chuẩn bị mock* / *seed data)* 

2\. *(Act: gọi method/API endpoint)* 3\. *(Assert*: *kiểm tra kết quả)* 

**Expected Result (PASS — hành vi đúng)**: 

*(kết quả trả về / DB state / exception message)* 

**Expected Result (FAIL — dấu hiệu lỗi)**: 

•   
*(điều gì xảy ra nếu implementation sai*) 

**Current Status:**   
Not written 

**Implementation Note:** *(ghi chú cho developer khi implement để pass test này)* 

**\[MODULE\]-TC\-002\-...** 

*(Lặp lại block trên cho mỗi test case)*   
**SECURITY TEST CASES** 

| Test cases kiểm tra attack vectors — điền thêm field OWASP và CWE.   
· 

**\[MODULE\]-TC**\-**0XX- \[Tên attack vector\]** 

**Severity:** CRITICAL 

**OWASP**: ABX:2921 \- \[Category\] 

**CWE:** CWE-XXX \- \[Tên\] 

**Legal**: \[Compliance requirement bị **vi** phạm nếu test FAIL\] 

**Feature Under Test:** \[Endpoint / Guard / Service\] 

**Test File:** \[file\] 

**TDD Phase:** 

**Preconditions:**   
RED 

(*trạng thái cho phép tấn công)* 

**Test Steps (Attack Simulation)**: 

1\. *(Chuẩn bị điều kiện tấn công)* 

2\. *(Thực hiện tấn công)* 

3\. *(Kiểm tra response/DB state)* 

**Expected Result (PASS \= hệ thống an toàn)**: 

403 Forbidden hoặc exception cụ thể 

**Expected Result (FAIL \= lỗ hổng tồn tại)**: 

•   
*(mô tả hành vi nguy hiểm nếu guard không hoạt động)* 

**Current Status**: Not written 

**INTEGRATION TEST CASES** 

| Dùng Testcontainers (PostgresqlContainer \+ RedisContainer). Timeout: 120s.   
**\[MODULE\]-TC\-INT\-001 — \[Tên scenario end\-to\-end\]** 

**Severity:** HIGH 

**Feature Under Test:** Full flow: \[bước đầu \+ bước cuối\] 

**Test File:** apps/core\-api/test/modules/\[module\]/\[feature\].integration\-spec.ts 

**TDD Phase:**   
RED 

**Condition Ref:** TC-COND\-XXX 

**Preconditions:** 

PostgreSQL container running (Testcontainers auto\-start) 

prisma db push \--skip\-generate applied 

• Seed: *(fixtures cần thiết*) 

**Test Steps:** 

1\. *(seed minimal data)* 

2\. *(call API step 1\)* 

3\. *(call API step 2*) 

4\. *(assert DB state)* 

**Expected Result (PASS)**: 

*(DB assertion: count, field values)* 

•   
*(API response shape)* 

**Expected Result (FAIL**): 

•   
*(dấu hiệu lỗi integration)* 

**DB Assertion:** 

const record \= await prisma.client. \[model**\].** findUnique({ where: { id } }); 

expect (record).not.toBeNull(); 

expect(record.status).toBe('\[EXPECTED\_STATUS\]'); 

**Current Status**: Not written   
**5\. Red-Green-Refactor Tracker** 

**TC ID**   
**Test File**   
**RED** 

**confirmed**   
**GREEN** 

**(commit)**   
**REFACTOR** 

**note** 

*(extract method*,   
\[MODULE\]\-TC\-001   
\[path\].spec.ts:line   
\[ \]   
\[hash\]   
*typing, etc.)* 

**6\. Entry / Exit Criteria** 

**Entry Criteria (Điều kiện bắt đầu)** 

D Spec kỹ thuật \[TECH\-SPEC\-ID\] đã được review và approve D Logic Issues (Section 2\) đã được confirm với Principal Architect D Prisma schema migration cho feature này đã được approved D Test fixtures (Section 3 TDS\-05\) đã được chuẩn bị 

**Exit Criteria (Điều kiện kết thúc — DoD)** 

npm test — tất cả unit tests xanh (không có skip) 

npm run test: integration   
―   
tất cả integration tests xanh 

Test coverage ≥ 80% lines cho các file mới tạo 

Không có any type trong production code liên quan 

subjectId không xuất hiện plaintext trong logs *(tiêu chí nghiệp vụ cụ thể của feature này)* 

**Suspension Criteria (Điều kiện tạm dừng)** 

• Blocker dependency chua sẵn sàng (migration, external service) 

• Phát hiện lỗi kiến trúc mới cần Principal Architect review 

• Cl pipeline bị broken bởi thay đổi khác   
**7\. Rollback Plan** 

\# *Revert migration (dev only KHÔNG chạy trên production)* npx prisma migrate reset 

\# *Revert implementation files* 

git checkout   
——   
**\[**path/to/changed/files\] 

*\# Gap vẫn OPEN giữ nguyên entry trong PHASE\_GAP\_ANALYSIS.md* 