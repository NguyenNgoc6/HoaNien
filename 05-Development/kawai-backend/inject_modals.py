import glob
import os

RECEPTIONIST_MODAL = """
<!-- RECEPTIONIST PROFILE MODAL -->
<div id="receptionistProfileModal" class="modal-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center;">
    <div class="modal-content" style="background: white; padding: 24px; border-radius: 8px; width: 400px; box-shadow: 0 4px 6px rgba(0,0,0,0.1);">
        <h2 style="margin-top: 0; border-bottom: 1px solid #eee; padding-bottom: 12px; color: #333; font-size: 1.5rem; font-weight: 600;">Thông tin Cá nhân</h2>
        <div style="display: flex; align-items: center; margin-top: 16px;">
            <div class="user-avatar" style="width: 64px; height: 64px; border-radius: 50%; background: #c19b6c; color: white; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px; font-weight: bold;" th:text="${staffName != null && staffName.length() >= 2 ? staffName.substring(0, 2).toUpperCase() : 'NV'}">NV</div>
            <div>
                <h3 style="margin: 0; font-size: 18px; color: #333;" th:text="${staffName != null ? staffName : 'Nhân viên'}">Nhân viên</h3>
                <p style="margin: 4px 0 0 0; color: #666; font-size: 14px;" th:text="${staffRole != null ? staffRole : 'Receptionist'}">Receptionist</p>
                <div style="margin-top: 8px;">
                    <a href="/profile" style="color: #c19b6c; text-decoration: none; font-size: 14px; display: inline-flex; align-items: center; gap: 4px;">
                        <i class="fa-solid fa-gear"></i> Cài đặt tài khoản
                    </a>
                </div>
            </div>
        </div>
        <div style="margin-top: 24px; text-align: right;">
            <button onclick="document.getElementById('receptionistProfileModal').style.display='none'" style="padding: 8px 16px; background: #c19b6c; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 500;">Đóng</button>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const avatars = document.querySelectorAll('.sidebar-user, .user-profile-btn');
        avatars.forEach(avatar => {
            avatar.style.cursor = 'pointer';
            avatar.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                document.getElementById('receptionistProfileModal').style.display = 'flex';
            });
        });
        
        const modal = document.getElementById('receptionistProfileModal');
        if(modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    modal.style.display = 'none';
                }
            });
        }
    });
</script>
</body>
"""

MANAGER_MODAL = """
<!-- MANAGER PROFILE MODAL -->
<div id="managerProfileModal" class="modal-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center;">
    <div class="modal-content" style="background: white; padding: 24px; border-radius: 8px; width: 400px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); font-family: 'Inter', sans-serif;">
        <h2 style="margin-top: 0; border-bottom: 1px solid #e5e7eb; padding-bottom: 12px; color: #111827; font-size: 1.25rem; font-weight: 600;">Thông tin Quản lý</h2>
        <div style="display: flex; align-items: center; margin-top: 16px;">
            <div style="width: 64px; height: 64px; border-radius: 50%; background: #4f46e5; color: white; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px; font-weight: bold;">MG</div>
            <div>
                <h3 style="margin: 0; font-size: 18px; color: #111827;">Manager</h3>
                <p style="margin: 4px 0 0 0; color: #6b7280; font-size: 14px;">Quản lý</p>
                <div style="margin-top: 8px;">
                    <a href="/profile" style="color: #4f46e5; text-decoration: none; font-size: 14px; font-weight: 500;">
                        Cài đặt tài khoản &rarr;
                    </a>
                </div>
            </div>
        </div>
        <div style="margin-top: 24px; text-align: right;">
            <button onclick="document.getElementById('managerProfileModal').style.display='none'" style="padding: 8px 16px; background: #4f46e5; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 500; transition: background 0.2s;">Đóng</button>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const avatars = document.querySelectorAll('.mgr-sidebar-user, .mgr-header-user, .mgr-header-user-box, .mgr-sidebar-footer');
        avatars.forEach(avatar => {
            avatar.style.cursor = 'pointer';
            avatar.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                document.getElementById('managerProfileModal').style.display = 'flex';
            });
        });
        
        const modal = document.getElementById('managerProfileModal');
        if(modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    modal.style.display = 'none';
                }
            });
        }
    });
</script>
</body>
"""

ADMIN_MODAL = """
<!-- ADMIN PROFILE MODAL -->
<div id="adminProfileModal" class="modal-overlay" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); z-index: 9999; justify-content: center; align-items: center;">
    <div class="modal-content" style="background: white; padding: 24px; border-radius: 8px; width: 400px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); font-family: 'Inter', sans-serif;">
        <h2 style="margin-top: 0; border-bottom: 1px solid #e5e7eb; padding-bottom: 12px; color: #111827; font-size: 1.25rem; font-weight: 600;">Thông tin Quản trị viên</h2>
        <div style="display: flex; align-items: center; margin-top: 16px;">
            <div style="width: 64px; height: 64px; border-radius: 50%; background: #111827; color: white; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px; font-weight: bold;">AD</div>
            <div>
                <h3 style="margin: 0; font-size: 18px; color: #111827;">Administrator</h3>
                <p style="margin: 4px 0 0 0; color: #6b7280; font-size: 14px;">Quản trị Hệ thống</p>
                <div style="margin-top: 8px;">
                    <a href="/profile" style="color: #374151; text-decoration: underline; font-size: 14px; font-weight: 500;">
                        Cài đặt tài khoản
                    </a>
                </div>
            </div>
        </div>
        <div style="margin-top: 24px; text-align: right;">
            <button onclick="document.getElementById('adminProfileModal').style.display='none'" style="padding: 8px 16px; background: #111827; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 500;">Đóng</button>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const avatars = document.querySelectorAll('.adm-sidebar-user, .adm-header-user-box, .adm-sidebar-footer');
        avatars.forEach(avatar => {
            avatar.style.cursor = 'pointer';
            avatar.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                document.getElementById('adminProfileModal').style.display = 'flex';
            });
        });
        
        const modal = document.getElementById('adminProfileModal');
        if(modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    modal.style.display = 'none';
                }
            });
        }
    });
</script>
</body>
"""

base_dir = 'd:/SWP391/su26-swp391-se2023-g2/05-Development/kawai-backend/src/main/resources/templates'

def inject_modal(folder, modal_html):
    files = glob.glob(f'{base_dir}/{folder}/**/*.html', recursive=True)
    count = 0
    for file in files:
        if 'fragments' in file:
            continue
        with open(file, 'r', encoding='utf-8') as f:
            content = f.read()
        if '</body>' in content and 'PROFILE MODAL' not in content:
            new_content = content.replace('</body>', modal_html)
            with open(file, 'w', encoding='utf-8') as f:
                f.write(new_content)
            count += 1
    return count

c1 = inject_modal('receptionist', RECEPTIONIST_MODAL)
c2 = inject_modal('manager', MANAGER_MODAL)
c3 = inject_modal('admin', ADMIN_MODAL)

print(f"Injected receptionist: {c1}, manager: {c2}, admin: {c3}")
