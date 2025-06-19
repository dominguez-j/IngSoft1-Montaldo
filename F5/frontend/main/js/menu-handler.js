export function setupMenuHandlers({sidebarItems, sections, main, welcomeContainer, dashboardContent}) {
    let sidebarAnimated = false;

    sidebarItems.forEach(item => {
        item.addEventListener('click', () => {
            const sidebar = document.querySelector('.sidebar');

            Object.values(menuConfig).forEach(cfg => {
                const subSidebar = document.getElementById(cfg.subSidebarId);
                if (subSidebar) {
                    subSidebar.style.display = 'none';
                    subSidebar.querySelectorAll('li').forEach(li => li.classList.remove('active'));
                }
            });

            const section = item.getAttribute('data-section');
            if (menuConfig[section]) {
                const subSidebar = document.getElementById(menuConfig[section].subSidebarId);
                if (subSidebar) subSidebar.style.display = '';
            }

            if (!sidebarAnimated && main && main.classList.contains('dashboard-welcome')) {
                sidebarAnimated = true;
                if (main) main.classList.remove('dashboard-welcome');
                if (welcomeContainer) welcomeContainer.style.display = 'none';
                if (sidebar) {
                    sidebar.classList.remove('sidebar-slide-in');
                    void sidebar.offsetWidth;
                    sidebar.classList.add('sidebar-slide-in');
                    setTimeout(() => {
                        sidebar.classList.remove('sidebar-slide-in');
                    }, 300);
                }
                if (dashboardContent) {
                    dashboardContent.style.display = '';
                    dashboardContent.classList.remove('dashboard-content-slide-in');
                    void dashboardContent.offsetWidth;
                    dashboardContent.classList.add('dashboard-content-slide-in');
                    setTimeout(() => {
                        dashboardContent.classList.remove('dashboard-content-slide-in');
                    }, 300);
                }
                sidebarItems.forEach(li => li.classList.remove('active'));
                item.classList.add('active');
                sections.forEach(section => section.style.display = 'none');
            } else {
                sidebarItems.forEach(li => li.classList.remove('active'));
                item.classList.add('active');
                sections.forEach(section => section.style.display = 'none');
                if (main) main.classList.remove('dashboard-welcome');
                if (welcomeContainer) welcomeContainer.style.display = 'none';
                if (sidebar) sidebar.classList.remove('sidebar-slide-in');
                if (document.querySelector('.sidebar')) document.querySelector('.sidebar').style.display = '';
                if (dashboardContent) dashboardContent.style.display = '';
            }
        });
    });

    Object.entries(menuConfig).forEach(([section, cfg]) => {
        const subSidebar = document.getElementById(cfg.subSidebarId);
        if (subSidebar) {
            subSidebar.querySelectorAll('li').forEach(item => {
                item.addEventListener('click', () => {
                    subSidebar.querySelectorAll('li').forEach(li => li.classList.remove('active'));
                    item.classList.add('active');
                    if (window.showSubSection) {
                        window.showSubSection(item.getAttribute('data-subsection'));
                    }
                });
            });
        }
    });
} 