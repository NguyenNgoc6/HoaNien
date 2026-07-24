/**
 * master-data.js — UI interaction logic for Master Data Management.
 *
 * Dữ liệu bảng (rows, columns) được render sẵn bởi Thymeleaf từ AdminController.
 * File này chỉ xử lý:
 *   - Tab switching (server-side navigation)
 *   - Sidebar submenu toggle
 *   - Client-side search (filter tr elements trong DOM)
 *   - Modal open / close
 *   - Edit: đọc data từ DOM attributes (data-key, data-value) do Thymeleaf sinh ra
 *   - Delete: xác nhận, sau đó reload page (khi có API thật)
 *   - Add new: mở form rỗng theo active tab
 *   - Add / Edit form field generation (UI schema — không phải business data)
 *   - Account permissions modal
 *   - Lucide icon re-init
 */

// ─────────────────────────────────────────────────────────────────────────────
// State
// ─────────────────────────────────────────────────────────────────────────────

/** Active tab name — read from Thymeleaf data attribute on <body>. */
const activeTab = document.body.dataset.activeTab || "Room Categories";

let editingRowId = null;
let deletingRowId = null;
let permissionsUserId = null;

// ─────────────────────────────────────────────────────────────────────────────
// Init
// ─────────────────────────────────────────────────────────────────────────────

document.addEventListener("DOMContentLoaded", () => {
    // Sidebar toggle
    const toggleBtn = document.getElementById("master-data-toggle");
    if (toggleBtn) toggleBtn.addEventListener("click", handleSidebarToggle);

    // Search
    const searchInput = document.getElementById("search-input");
    if (searchInput) searchInput.addEventListener("input", (e) => handleSearch(e.target.value));

    // Add new button
    const addBtn = document.getElementById("btn-add-new");
    if (addBtn) addBtn.addEventListener("click", openAddModal);

    // Modal close buttons (all buttons with .btn-close-modal)
    document.querySelectorAll(".btn-close-modal").forEach(btn => {
        btn.addEventListener("click", () => closeModal(btn.dataset.modal));
    });

    // Close modal on backdrop click
    ["entity-modal", "delete-modal", "permissions-modal"].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.addEventListener("click", () => closeModal(id));
    });

    // Delete confirm button
    const confirmDeleteBtn = document.getElementById("btn-confirm-delete");
    if (confirmDeleteBtn) confirmDeleteBtn.addEventListener("click", handleConfirmDelete);

    // Save permissions button
    const savePermsBtn = document.getElementById("btn-save-permissions");
    if (savePermsBtn) savePermsBtn.addEventListener("click", handleSavePermissions);

    // Entity form submit
    const entityForm = document.getElementById("entity-form");
    if (entityForm) entityForm.addEventListener("submit", handleFormSubmit);

    // Table action buttons (edit, delete, permissions)
    wireTableButtons();

    // Init icons
    if (typeof lucide !== "undefined") lucide.createIcons();
});

// ─────────────────────────────────────────────────────────────────────────────
// Sidebar submenu
// ─────────────────────────────────────────────────────────────────────────────

function handleSidebarToggle(event) {
    event.preventDefault();
    const submenu = document.getElementById("master-data-submenu");
    const arrow   = document.getElementById("master-data-arrow");
    if (!submenu || !arrow) return;
    const isOpen = submenu.classList.toggle("open");
    arrow.classList.toggle("open", isOpen);
    arrow.innerHTML = `<i data-lucide="${isOpen ? "chevron-down" : "chevron-right"}"></i>`;
    if (typeof lucide !== "undefined") lucide.createIcons();
}

// ─────────────────────────────────────────────────────────────────────────────
// Client-side search (filter on Thymeleaf-rendered rows)
// ─────────────────────────────────────────────────────────────────────────────

function handleSearch(query) {
    const q = query.toLowerCase().trim();
    const totalEl = document.getElementById("pagination-info");
    let visible = 0;
    let total = 0;

    document.querySelectorAll("tbody tr[data-id]").forEach(tr => {
        total++;
        const match = !q || tr.textContent.toLowerCase().includes(q);
        tr.style.display = match ? "" : "none";
        if (match) visible++;
    });

    if (totalEl) {
        totalEl.textContent = `Hiển thị ${visible} / ${total} bản ghi`;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Modal helpers
// ─────────────────────────────────────────────────────────────────────────────

function openModal(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.classList.remove("hidden");
    el.style.display = "flex";
    document.body.style.overflow = "hidden";
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (!el) return;
    el.style.display = "none";
    el.classList.add("hidden");
    document.body.style.overflow = "unset";
}

// ─────────────────────────────────────────────────────────────────────────────
// Wire table action buttons (edit / delete / permissions)
// ─────────────────────────────────────────────────────────────────────────────

function wireTableButtons() {
    document.querySelectorAll(".btn-edit").forEach(btn => {
        btn.addEventListener("click", () => openEditModal(btn.dataset.id));
    });
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", () => openDeleteConfirm(btn.dataset.id));
    });
    document.querySelectorAll(".btn-permissions").forEach(btn => {
        btn.addEventListener("click", () => openPermissionsModal(btn.dataset.id, btn.dataset.name, btn.dataset.role));
    });
}

// ─────────────────────────────────────────────────────────────────────────────
// Add / Edit modal
// ─────────────────────────────────────────────────────────────────────────────

function openAddModal() {
    editingRowId = null;
    const title = document.getElementById("modal-title");
    if (title) title.innerText = `Thêm mới ${activeTab}`;
    
    const container = document.getElementById("modal-form-fields");
    // If container is empty, it means we are using JS to build forms
    // If it has children, it means it's hardcoded HTML, so we just reset the form
    if (container && container.children.length === 0) {
        loadFormFields(null);
    } else {
        document.getElementById("entity-form").reset();
    }
    
    openModal("entity-modal");
}

function openEditModal(id) {
    editingRowId = id;
    const title = document.getElementById("modal-title");
    if (title) title.innerText = `Chỉnh sửa ${activeTab}`;

    // Read row data from DOM (Thymeleaf renders data-key and data-value on each td)
    const tr = document.querySelector(`tr[data-id="${id}"]`);
    const entityData = {};
    if (tr) {
        tr.querySelectorAll("td[data-key]").forEach(td => {
            entityData[td.dataset.key] = td.dataset.value;
        });
    }

    const container = document.getElementById("modal-form-fields");
    if (container && container.children.length === 0) {
        loadFormFields(entityData);
    } else {
        const form = document.getElementById("entity-form");
        form.reset();
        for (const [key, value] of Object.entries(entityData)) {
            const input = form.elements[key];
            if (input) {
                if (input.type === "checkbox") {
                    input.checked = (value === "true");
                } else {
                    input.value = value;
                }
            }
        }
    }
    
    openModal("entity-modal");
}

function handleFormSubmit(event) {
    event.preventDefault();
    // TODO: POST to /admin/api/master-data when backend is ready.
    // For now: close modal and show toast.
    const isEdit = editingRowId !== null;

    closeModal("entity-modal");
    editingRowId = null;

    if (typeof showToast === "function") {
        showToast(isEdit ? "Cập nhật thành công!" : "Thêm mới thành công!", "success");
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Delete modal
// ─────────────────────────────────────────────────────────────────────────────

function openDeleteConfirm(id) {
    deletingRowId = id;
    openModal("delete-modal");
}

function handleConfirmDelete() {
    // TODO: DELETE /admin/api/master-data/${deletingRowId} when backend is ready.
    // For now: hide the row from DOM and close.
    if (deletingRowId) {
        const tr = document.querySelector(`tr[data-id="${deletingRowId}"]`);
        if (tr) tr.remove();
        handleSearch(document.getElementById("search-input")?.value || "");
    }
    closeModal("delete-modal");
    deletingRowId = null;
    if (typeof showToast === "function") showToast("Đã xóa bản ghi!", "success");
}

// ─────────────────────────────────────────────────────────────────────────────
// Form field loading — UI schema (not business data)
// ─────────────────────────────────────────────────────────────────────────────

function loadFormFields(entity) {
    const container = document.getElementById("modal-form-fields");
    if (!container) return;
    container.innerHTML = "";

    const inputStyle = "adm-form-input";
    const selectStyle = "adm-form-select";
    const labelStyle = "adm-form-label";
    const wrap = "adm-form-group";

    function text(label, name, value = "", placeholder = "", required = true, type = "text") {
        const div = document.createElement("div");
        div.className = wrap;
        div.innerHTML = `
            <label class="${labelStyle}">${label}${required ? ' <span style="color:#B45309">*</span>' : ""}</label>
            <input type="${type}" name="${name}" value="${value ?? ""}" placeholder="${placeholder}"
                   class="${inputStyle}" ${required ? "required" : ""} />
        `;
        return div;
    }

    // Select input helper
    function select(label, name, options, selected = "") {
        const div = document.createElement("div");
        div.className = wrap;
        const optHtml = options.map(o => `<option value="${o.value}" ${o.value === selected ? "selected" : ""}>${o.label}</option>`).join("");
        div.innerHTML = `
            <label class="${labelStyle}">${label}</label>
            <select name="${name}" class="${selectStyle}">${optHtml}</select>
        `;
        return div;
    }

    function grid2(...children) {
        const div = document.createElement("div");
        div.className = "grid-2";
        children.forEach(c => div.appendChild(c));
        return div;
    }

    // ── Dropdown options — injected by Thymeleaf from AdminController.buildFormOptions() ──
    // Sửa dữ liệu tại AdminController.java → buildFormOptions() thay vì sửa ở đây.
    const _opts = window.KAWAI_FORM_OPTIONS || {};
    const toOpt = arr => (arr || []).map(v => ({ value: v, label: v }));

    const STATUS_ACTIVE = [{ value: "Active", label: "Active" }, { value: "Inactive", label: "Inactive" }];
    const STATUS_AVAIL = [{ value: "Available", label: "Available" }, { value: "Unavailable", label: "Unavailable" }];
    const STATUS_ACTIVE_EXPIRED = [{ value: "Active", label: "Active" }, { value: "Inactive", label: "Inactive" }, { value: "Expired", label: "Expired" }];

    const ROOM_CATS = toOpt(_opts.roomCategories);
    const ROOM_STATUS = toOpt(_opts.roomStatuses);
    const MENU_CATS = toOpt(_opts.menuCategories);
    const TOUR_CATS = toOpt(_opts.tourCategories);
    const PROMO_SCOPE = toOpt(_opts.promoScopes);
    const SEASON_TYPES = toOpt(_opts.seasonTypes);
    const STAFF_ROLES = toOpt(_opts.staffRoles);
    const GUEST_ROLES = toOpt(_opts.guestRoles);

    switch (activeTab) {

        case "Room Categories":
            container.appendChild(text("Tên hạng phòng", "name", entity?.name, "Ví dụ: Deluxe River View"));
            container.appendChild(text("Số phòng", "rooms", entity?.rooms, "8", true, "number"));
            container.appendChild(text("Giá cơ bản / đêm", "price", entity?.price, "2.800.000 VNĐ"));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE, entity?.status || "Active"));
            break;

        case "Rooms":
            container.appendChild(text("Tên phòng", "name", entity?.name, "Phòng 101"));
            container.appendChild(select("Hạng phòng", "category", ROOM_CATS, entity?.category || "Standard Room"));
            container.appendChild(select("Trạng thái", "status", ROOM_STATUS, entity?.status || "Vacant"));
            break;

        case "Restaurant Menu":
            container.appendChild(text("Tên món", "name", entity?.name, "Tom Yum Soup"));
            container.appendChild(select("Danh mục", "category", MENU_CATS, entity?.category || "Súp"));
            container.appendChild(text("Đơn giá", "price", entity?.price, "185.000 VNĐ"));
            container.appendChild(select("Trạng thái", "status", STATUS_AVAIL, entity?.status || "Available"));
            break;

        case "Menu Categories":
            container.appendChild(text("Tên danh mục", "name", entity?.name, "Súp"));
            container.appendChild(text("Số món", "items", entity?.items, "4", false, "number"));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE, entity?.status || "Active"));
            break;

        case "Tour Categories":
            container.appendChild(text("Tên danh mục tour", "name", entity?.name, "Du thuyền sông"));
            container.appendChild(text("Số tour", "tours", entity?.tours, "3", false, "number"));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE, entity?.status || "Active"));
            break;

        case "Tours":
            container.appendChild(text("Tên tour", "name", entity?.name, "Du thuyền hoàng hôn"));
            container.appendChild(select("Danh mục", "category", TOUR_CATS, entity?.category || "Du thuyền sông"));
            container.appendChild(text("Thời gian", "duration", entity?.duration, "2 tiếng"));
            container.appendChild(text("Giá / người", "price", entity?.price, "450.000 VNĐ"));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE, entity?.status || "Active"));
            break;

        case "Promotions": {
            container.appendChild(text("Mã giảm giá", "code", entity?.code, "SUMMER2026"));
            container.appendChild(grid2(
                select("Loại", "type", [{ value: "Phần trăm", label: "Phần trăm" }, { value: "Số tiền", label: "Số tiền" }], entity?.type || "Phần trăm"),
                text("Giá trị", "value", entity?.value, "15%")
            ));
            container.appendChild(text("Đơn tối thiểu", "minOrder", entity?.minOrder, "500.000 VNĐ", false));
            container.appendChild(select("Áp dụng cho", "scope", PROMO_SCOPE, entity?.scope || "Toàn bộ"));
            container.appendChild(grid2(
                text("Lượt dùng", "uses", entity?.uses, "0 / 100", false),
                text("Hết hạn", "expires", entity?.expires, "30/06/2026")
            ));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE_EXPIRED, entity?.status || "Active"));
            break;
        }

        case "Account Management": {
            container.appendChild(text("Tên tài khoản", "name", entity?.name, "Nguyễn Văn A"));
            // STAFF_ROLES và GUEST_ROLES từ AdminController.buildFormOptions()
            const typeDiv = select("Loại tài khoản", "type",
                [{ value: "Nhân viên", label: "Nhân viên" }, { value: "Khách", label: "Khách" }],
                entity?.type || "Nhân viên");
            const roleDiv = select("Vai trò", "role",
                entity?.type === "Khách" ? GUEST_ROLES : STAFF_ROLES,
                entity?.role || "Receptionist");
            container.appendChild(grid2(typeDiv, roleDiv));
            typeDiv.querySelector("select").addEventListener("change", (e) => {
                const roleSelect = roleDiv.querySelector("select");
                roleSelect.innerHTML = (e.target.value === "Nhân viên" ? STAFF_ROLES : GUEST_ROLES)
                    .map(o => `<option value="${o.value}">${o.label}</option>`).join("");
            });
            container.appendChild(text("Email", "email", entity?.email, "email@kawai.vn", true, "email"));
            const checkDiv = document.createElement("div");
            const isActive = entity ? (entity.status === "true" || entity.status === true) : true;
            checkDiv.innerHTML = `
                <label class="adm-form-label" style="display:flex;align-items:center;gap:10px;cursor:pointer;margin-top:8px;text-transform:none">
                    <input type="checkbox" name="status" ${isActive ? "checked" : ""} style="width:16px;height:16px;cursor:pointer" />
                    <span>Tài khoản được kích hoạt</span>
                </label>
            `;
            container.appendChild(checkDiv);
            break;
        }

        case "Pricing Management":
            container.appendChild(select("Hạng phòng", "roomCategory", ROOM_CATS, entity?.roomCategory || "Deluxe River View"));
            container.appendChild(select("Loại mùa", "seasonType", SEASON_TYPES, entity?.seasonType || "Thường ngày"));
            container.appendChild(grid2(
                text("Ngày bắt đầu", "startDate", entity?.startDate, "", true, "date"),
                text("Ngày kết thúc", "endDate", entity?.endDate, "", true, "date")
            ));
            container.appendChild(text("Giá / đêm", "price", entity?.price, "2.800.000 VNĐ"));
            container.appendChild(select("Trạng thái", "status", STATUS_ACTIVE, entity?.status || "Active"));
            break;
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Permissions modal
// ─────────────────────────────────────────────────────────────────────────────

const PERMISSION_LABELS = {
    dashboard: "Dashboard Panel",
    masterData: "Master Data Management",
    auditLog: "Audit Log Monitor",
    reviews: "Review Management",
    booking: "Booking Management",
    fnb: "F&B Management",
    housekeeping: "Housekeeping",
    maintenance: "Maintenance Panel"
};

let localPermissions = {};

function openPermissionsModal(id, name, role) {
    permissionsUserId = id;

    const titleEl = document.getElementById("permissions-modal-title");
    const descEl = document.getElementById("permissions-desc");
    if (titleEl) titleEl.innerText = `Cấp quyền - ${name}`;
    if (descEl) descEl.innerHTML = `Quản lý quyền truy cập cho nhân viên <strong>${name}</strong> (Vai trò: ${role})`;

    // Default permissions based on role — computed from role value (server logic simulated client-side)
    localPermissions = {
        dashboard: true,
        masterData: role === "Admin" || role === "Manager",
        auditLog: role === "Admin" || role === "Manager",
        reviews: true,
        booking: role !== "Housekeeping",
        fnb: role === "F&B" || role === "Admin" || role === "Manager",
        housekeeping: role === "Housekeeping" || role === "Admin" || role === "Manager",
        maintenance: role === "Admin" || role === "Manager"
    };

    renderPermissionsList();
    openModal("permissions-modal");
}

function renderPermissionsList() {
    const container = document.getElementById("permissions-list");
    if (!container) return;
    container.innerHTML = "";

    Object.entries(localPermissions).forEach(([key, value]) => {
        const label = document.createElement("label");
        label.className = "flex items-center gap-3.5 px-4 py-3 rounded-lg bg-[#2C2A1E]/4 cursor-pointer hover:bg-primary/8 transition-colors";
        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.checked = value;
        checkbox.className = "w-4 h-4 cursor-pointer";
        checkbox.addEventListener("change", () => { localPermissions[key] = checkbox.checked; });
        const span = document.createElement("span");
        span.className = "font-sans text-[14px] text-[#2C2A1E] flex-1";
        span.innerText = PERMISSION_LABELS[key] || key;
        label.appendChild(checkbox);
        label.appendChild(span);
        container.appendChild(label);
    });
}

function handleSavePermissions() {
    // TODO: POST permissions to backend when API ready
    closeModal("permissions-modal");
    permissionsUserId = null;
    if (typeof showToast === "function") showToast("Lưu phân quyền thành công!", "success");
}
// ─────────────────────────────────────────────────────────────────────────────
// ACCOUNT TOGGLE STATUS
// Cập nhật UI ngay (optimistic). Khi backend sẵn sàng:
//   1. Uncomment phần fetch() bên dưới
//   2. Xóa phần "// Cập nhật UI" phía trên fetch (để tránh double-update)
// ─────────────────────────────────────────────────────────────────────────────

document.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-toggle");
    if (!btn) return;

    const id      = btn.dataset.id;
    const current = btn.dataset.value === "true";
    const next    = !current;

    // ── Cập nhật UI ngay (không cần API) ─────────────────────────────────────
    btn.dataset.value = String(next);

    const icon = btn.querySelector("i");
    if (icon) {
        icon.setAttribute("data-lucide", next ? "toggle-right" : "toggle-left");
        icon.className = `w-[22px] h-[22px] ${next ? "text-[#C9A96E]" : "text-[#8B7355]"}`;
        if (typeof lucide !== "undefined") lucide.createIcons();
    }

    // Đồng bộ data-value trên <td> để edit modal đọc đúng
    const td = btn.closest("td[data-key]");
    if (td) td.dataset.value = String(next);

    // ── TODO: Gọi API khi backend /admin/account/{id}/toggle-status sẵn sàng ─
    // fetch(`/admin/account/${id}/toggle-status`, {
    //     method: "PUT",
    //     headers: { "Content-Type": "application/json" },
    //     body: JSON.stringify({ active: next })
    // }).then(res => {
    //     if (!res.ok) {
    //         // Rollback UI nếu API thất bại
    //         btn.dataset.value = String(current);
    //         if (icon) {
    //             icon.setAttribute("data-lucide", current ? "toggle-right" : "toggle-left");
    //             icon.className = `w-[22px] h-[22px] ${current ? "text-[#C9A96E]" : "text-[#8B7355]"}`;
    //             if (typeof lucide !== "undefined") lucide.createIcons();
    //         }
    //         console.error("Toggle failed — rolling back");
    //     }
    // }).catch(err => console.error("Toggle error:", err));
});
