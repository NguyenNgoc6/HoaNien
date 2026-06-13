/**
 * master-data.js — UI interaction logic for Master Data Management.
 *
 * Dữ liệu bảng (rows, columns) được render sẵn bởi Thymeleaf từ AdminController.
 * File này chỉ xử lý:
 *   - Tab switching (server-side navigation)
 *   - Sidebar submenu toggle
 *   - Client-side search (filter DOM rows)
 *   - Modal open / close
 *   - Edit: đọc data từ data-entity attribute do Thymeleaf sinh ra
 *   - Delete: gọi DELETE API, xóa DOM row khi backend xác nhận
 *   - Add new: mở form rỗng theo active tab
 *   - Image upload preview
 *   - Lucide icon re-init
 *
 * Business logic (role rules, permission, CRUD, validation) do Spring Boot backend xử lý.
 */

// ─────────────────────────────────────────────────────────────────────────────
// State
// ─────────────────────────────────────────────────────────────────────────────

/** Active tab name — read from Thymeleaf data attribute on <body>. */
const activeTab = document.body.dataset.activeTab || "Room Categories";

let editingRowId = null;
let deletingRowId = null;

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
    ["entity-modal", "delete-modal"].forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener("click", (e) => {
                if (e.target === el) closeModal(id);
            });
        }
    });

    // Delete confirm button
    const confirmDeleteBtn = document.getElementById("btn-confirm-delete");
    if (confirmDeleteBtn) confirmDeleteBtn.addEventListener("click", handleConfirmDelete);

    // Permissions button removed — access control is fully role-based (Spring Security)

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
    const arrow = document.getElementById("master-data-arrow");
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
// Wire table action buttons (edit / delete)
// ─────────────────────────────────────────────────────────────────────────────

function wireTableButtons() {
    document.querySelectorAll(".btn-edit").forEach(btn => {
        btn.addEventListener("click", () => openEditModal(btn.dataset.id));
    });
    document.querySelectorAll(".btn-delete").forEach(btn => {
        btn.addEventListener("click", () => openDeleteConfirm(btn.dataset.id));
    });
    // .btn-permissions removed: access control is now fully role-based.
    // Each role can only access the login page and pages assigned to that role.
}

// ─────────────────────────────────────────────────────────────────────────────
// Add / Edit modal
// ─────────────────────────────────────────────────────────────────────────────

function openAddModal() {
    editingRowId = null;
    const title = document.getElementById("modal-title");
    if (title) title.innerText = `Thêm mới ${activeTab}`;

    const form = document.getElementById("entity-form");
    if (form) {
        form.reset();
        form.dataset.action = '';
        form.dataset.method = 'POST';
    }

    // Khi thêm mới: ẩn các account type không được phép tạo (backend đánh dấu qua data-allow-create="false")
    if (activeTab === "Account Management") {
        const typeSelect = document.getElementById('account-type-select');
        if (typeSelect) {
            let firstAllowed = null;
            Array.from(typeSelect.options).forEach(opt => {
                const allowed = opt.dataset.allowCreate !== 'false';
                opt.style.display = allowed ? '' : 'none';
                opt.disabled = !allowed;
                if (allowed && !firstAllowed) firstAllowed = opt;
            });
            if (firstAllowed) {
                typeSelect.value = firstAllowed.value;
            }
        }
    }
    initAccountFormToggles();
    openModal("entity-modal");
}

function openEditModal(id) {
    editingRowId = id;
    const title = document.getElementById("modal-title");
    if (title) title.innerText = `Chỉnh sửa ${activeTab}`;

    const tr = document.querySelector(`tr[data-id="${id}"]`);
    if (!tr) return;

    const entityJson = tr.getAttribute('data-entity');
    const entityData = entityJson ? JSON.parse(entityJson) : {};

    const form = document.getElementById("entity-form");
    if (form) {
        form.reset();

        if (activeTab === "Account Management") {
            const typeGroup = document.getElementById('account-type-group');
            if (typeGroup) typeGroup.style.display = ''; // Khôi phục hiển thị nhóm Loại tài khoản
            const typeSelect = document.getElementById('account-type-select');
            if (typeSelect) {
                // Khôi phục tất cả options và styling (openAddModal có thể đã ẩn chúng)
                Array.from(typeSelect.options).forEach(opt => {
                    opt.style.display = '';
                    opt.disabled = false;
                });
                typeSelect.style.pointerEvents = 'auto'; // Cho phép đổi loại tài khoản khi edit
                typeSelect.style.opacity = '1';
            }
        }

        // Set tất cả giá trị form TRƯỚC khi gọi initAccountFormToggles()
        // để updateVisibility() đọc đúng giá trị type đã được chọn
        for (const [key, value] of Object.entries(entityData)) {
            const input = form.elements[key];
            if (input) {
                if (input.type === "checkbox") {
                    input.checked = (value === "true" || value === true);
                } else {
                    input.value = value;
                }
            }
        }
        if (activeTab === "Account Management") {
            form.dataset.action = `/admin/api/v1/accounts/${id}`;
            form.dataset.method = "PUT";
        }
    }

    // Gọi SAU khi đã set values để updateVisibility() lọc role đúng loại tài khoản
    initAccountFormToggles();
    openModal("entity-modal");
}

function handleFormSubmit(event) {
    event.preventDefault();
    const isEdit = editingRowId !== null;
    const form = event.target;

    // Disable submit button
    const submitBtn = form.querySelector('button[type="submit"]');
    if (submitBtn) {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Đang xử lý...';
    }

    // Generic form submission logic
    let endpoint = form.dataset.action || '';
    let method = form.dataset.method || 'POST';

    // Fallback for Account Management tab if data-action wasn't explicitly set
    if (activeTab === "Account Management" && !endpoint) {
        endpoint = isEdit ? `/admin/api/v1/accounts/${editingRowId}` : '/admin/api/v1/accounts';
    }

    if (endpoint) {
        const formData = new FormData(form);
        const payload = Object.fromEntries(formData.entries());
        // Fix checkbox booleans
        form.querySelectorAll('input[type="checkbox"]').forEach(cb => {
            payload[cb.name] = cb.checked;
        });

        fetch(endpoint, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
            .then(res => {
                if (!res.ok) throw new Error("Lỗi API: " + res.status);
                return res.json();
            })
            .then(data => {
                closeModal("entity-modal");
                window.location.reload();
            })
            .catch(err => {
                console.error("API Error:", err);
                if (typeof showToast === "function") showToast("Lỗi khi lưu: " + err.message, "error");

                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Lưu';
                }
            });
        return;
    }

    // Default flow if no endpoint
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
    if (!deletingRowId) return;

    const deleteBtn = document.getElementById("btn-confirm-delete");
    if (deleteBtn) { deleteBtn.disabled = true; deleteBtn.textContent = "Đang xóa..."; }

    // TODO: Thay endpoint theo từng tab khi backend sẵn sàng
    // Hiện tại Account Management là tab duy nhất có Delete API
    const endpoint = activeTab === "Account Management"
        ? `/admin/api/v1/accounts/${deletingRowId}`
        : null;

    if (endpoint) {
        fetch(endpoint, { method: "DELETE" })
            .then(res => {
                if (!res.ok) return res.text().then(t => { throw new Error(t || res.status); });
                // Backend xác nhận → mới xóa DOM
                const tr = document.querySelector(`tr[data-id="${deletingRowId}"]`);
                if (tr) tr.remove();
                handleSearch(document.getElementById("search-input")?.value || "");
                if (typeof showToast === "function") showToast("Đã xóa bản ghi!", "success");
            })
            .catch(err => {
                console.error("Delete error:", err);
                if (typeof showToast === "function") showToast("Lỗi xóa: " + err.message, "error");
            })
            .finally(() => {
                closeModal("delete-modal");
                deletingRowId = null;
                if (deleteBtn) { deleteBtn.disabled = false; deleteBtn.textContent = "Xác nhận xóa"; }
            });
    } else {
        // Chưa có API cho tab này — chỉ đóng modal
        closeModal("delete-modal");
        deletingRowId = null;
        if (deleteBtn) { deleteBtn.disabled = false; deleteBtn.textContent = "Xác nhận xóa"; }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Form field loading — UI schema (not business data)
// ─────────────────────────────────────────────────────────────────────────────

// Forms are server-side rendered by Thymeleaf.
// Backend quyết định role nào là staff (class="role-staff") và guest (class="role-guest").
// JS chỉ đọc các class này để show/hide — không hardcode tên role hay keyword.

// WeakMap lưu listener reference để tránh đăng ký lặp
const _typeSelectListeners = new WeakMap();

function initAccountFormToggles() {
    if (activeTab !== "Account Management") return;
    const typeSelect = document.getElementById('account-type-select');
    const roleSelect = document.getElementById('account-role-select');
    const empFields = document.getElementById('account-employee-fields');
    if (!empFields) return;

    function updateVisibility() {
        // Backend quyết định category qua data-account-category trên option
        // Fallback: nếu không có attribute, dùng class role-staff/role-guest
        const selectedOpt = typeSelect ? typeSelect.options[typeSelect.selectedIndex] : null;
        const isEmp = !typeSelect || (selectedOpt?.dataset.accountCategory ?? 'EMPLOYEE') === 'EMPLOYEE';

        // Hiện/ẩn employee-specific fields (username, salary) — UI chỉ đọc class từ HTML
        empFields.style.display = isEmp ? "flex" : "none";
        empFields.querySelectorAll('input').forEach(input => {
            input.required = isEmp;
        });

        // Password bắt buộc khi thêm mới, không bắt buộc khi chỉnh sửa
        const form = document.getElementById("entity-form");
        if (form) {
            const pwdInput = form.querySelector('input[name="password"]');
            if (pwdInput) pwdInput.required = !editingRowId;
        }

        // Filter role options dựa hoàn toàn vào class do backend inject (role-staff / role-guest)
        if (roleSelect) {
            Array.from(roleSelect.options).forEach(opt => {
                const isGuestRole = opt.classList.contains('role-guest');
                const isStaffRole = opt.classList.contains('role-staff');

                if (isEmp) {
                    opt.style.display = isGuestRole ? "none" : "";
                    opt.disabled = isGuestRole;
                } else {
                    opt.style.display = isStaffRole ? "none" : "";
                    opt.disabled = isStaffRole;
                }
            });

            // Nếu option đang chọn bị ẩn → chuyển sang option đầu tiên hợp lệ
            // Nhưng KHÔNG ghi đè nếu value hiện tại vẫn hợp lệ (trường hợp edit đã set role đúng)
            const currentOpt = roleSelect.options[roleSelect.selectedIndex];
            if (currentOpt && (currentOpt.style.display === "none" || currentOpt.disabled)) {
                const firstValid = Array.from(roleSelect.options)
                    .find(o => o.style.display !== "none" && !o.disabled);
                if (firstValid) roleSelect.value = firstValid.value;
            }
        }
    }

    // Fix event listener leak: dùng WeakMap để track và remove đúng reference
    if (typeSelect) {
        const existing = _typeSelectListeners.get(typeSelect);
        if (existing) typeSelect.removeEventListener("change", existing);
        typeSelect.addEventListener("change", updateVisibility);
        _typeSelectListeners.set(typeSelect, updateVisibility);
    }
    updateVisibility();
}




// ─────────────────────────────────────────────────────────────────────────────
// ACCOUNT TOGGLE STATUS — Optimistic UI + PATCH API
// Endpoint: PATCH /admin/api/v1/accounts/{id}/status
// ─────────────────────────────────────────────────────────────────────────────

document.addEventListener("click", (e) => {
    const btn = e.target.closest(".btn-toggle");
    if (!btn) return;

    const id = btn.dataset.id;
    const current = btn.dataset.value === "true";
    const next = !current;
    applyToggleIcon(btn, next);
    const td = btn.closest("td[data-key]");
    if (td) td.dataset.value = String(next);
    fetch(`/admin/api/v1/accounts/${id}/status`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: next })
    })
        .then(res => {
            if (!res.ok) throw new Error("API Error: " + res.status);
            return res;
        })
        .catch(err => {
            console.error("Toggle status error:", err);
            // Rollback UI về trạng thái cũ nếu API thất bại
            applyToggleIcon(btn, current);
            if (td) td.dataset.value = String(current);
            if (typeof showToast === "function") showToast("Lỗi cập nhật trạng thái", "error");
        });
});

function applyToggleIcon(btn, isActive) {
    btn.dataset.value = String(isActive);
    const lucideName = isActive ? "toggle-right" : "toggle-left";
    const color = isActive ? "#C9A96E" : "#8B7355";
    btn.innerHTML = `<i data-lucide="${lucideName}" style="color:${color}"></i>`;
    if (typeof lucide !== "undefined") lucide.createIcons({ root: btn });
}

// ─────────────────────────────────────────────────────────────────────────────
// Image Upload Handlers
// ─────────────────────────────────────────────────────────────────────────────

function handleSingleImageUpload(input) {
    const file = input.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            const preview = input.parentElement.querySelector('.image-preview');
            const placeholder = input.parentElement.querySelector('.upload-placeholder');
            if (preview) {
                preview.src = e.target.result;
                preview.style.display = 'block';
            }
            if (placeholder) {
                placeholder.style.display = 'none';
            }
        };
        reader.readAsDataURL(file);
    }
}

function handleGalleryUpload(input) {
    if (!input.files || input.files.length === 0) return;

    const container = input.closest('.gallery-upload-container');
    const previewList = container.querySelector('.gallery-preview-list');
    const hiddenContainer = container.querySelector('.hidden-inputs-container');

    // Tạo một input mới để thay thế input hiện tại
    const newInput = document.createElement('input');
    newInput.type = 'file';
    newInput.name = 'galleryImages';
    newInput.accept = 'image/*';
    newInput.multiple = true;
    newInput.style.display = 'none';
    newInput.onchange = function () { handleGalleryUpload(this) };

    // Đổi tên biến input cũ để tiện quản lý (mỗi input ẩn sẽ có 1 id riêng)
    const inputId = 'gallery_input_' + Date.now();
    input.id = inputId;

    // Chuyển input cũ sang vùng ẩn
    hiddenContainer.appendChild(input);
    // Gắn input mới vào nút Thêm ảnh
    container.querySelector('.gallery-upload-btn').appendChild(newInput);

    const files = Array.from(input.files);

    files.forEach((file, index) => {
        const reader = new FileReader();
        reader.onload = function (e) {
            const item = document.createElement('div');
            item.className = 'gallery-item';
            item.innerHTML = `
                <img src="${e.target.result}" alt="preview">
                <button type="button" class="remove-btn" onclick="removeGalleryItem(this)" title="Xóa ảnh này">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                </button>
            `;
            previewList.appendChild(item);
        };
        reader.readAsDataURL(file);
    });
}

function removeGalleryItem(btn) {
    btn.parentElement.remove();
}
