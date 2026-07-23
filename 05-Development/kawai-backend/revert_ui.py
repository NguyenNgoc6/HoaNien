import glob

html_files = glob.glob('d:/SWP391/su26-swp391-se2023-g2/05-Development/kawai-backend/src/main/resources/templates/**/*.html', recursive=True)

count = 0
for filepath in html_files:
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content
    # Remove onclick to sidebar-user
    new_content = new_content.replace('<div class="sidebar-user" onclick="window.location.href=\'/profile\'" style="cursor: pointer;">', '<div class="sidebar-user">')
    
    # Remove onclick to mgr-sidebar-user
    new_content = new_content.replace('<div class="mgr-sidebar-user" onclick="window.location.href=\'/profile\'" style="cursor: pointer;">', '<div class="mgr-sidebar-user">')
    
    # Remove onclick to adm-sidebar-user
    new_content = new_content.replace('<div class="adm-sidebar-user" onclick="window.location.href=\'/profile\'" style="cursor: pointer;">', '<div class="adm-sidebar-user">')

    if new_content != content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        count += 1
        print(f'Reverted {filepath}')

print(f'Total files reverted: {count}')
