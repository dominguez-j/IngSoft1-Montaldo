function renderSidebar() {
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) return;
    sidebar.innerHTML = '<ul></ul>';
    const ul = sidebar.querySelector('ul');
    window.dashboardSections.forEach(section => {
        const li = document.createElement('li');
        li.textContent = section.label;
        li.setAttribute('data-section', section.section);
        ul.appendChild(li);
    });
}

function renderSubSidebar(sectionKey) {
    const subSidebar = document.querySelector('.sub-sidebar');
    if (!subSidebar) return;
    subSidebar.innerHTML = '<ul></ul>';
    const ul = subSidebar.querySelector('ul');
    const section = window.dashboardSections.find(s => s.section === sectionKey);
    if (!section || !section.subsections || section.subsections.length === 0) {
        subSidebar.style.display = 'none';
        return;
    }
    section.subsections.forEach(sub => {
        const li = document.createElement('li');
        li.textContent = sub.label;
        li.setAttribute('data-subsection', sub.key);
        ul.appendChild(li);
    });
    subSidebar.style.display = '';
}

document.addEventListener('DOMContentLoaded', () => {
    const main = document.querySelector('.dashboard-main');
    const welcomeContainer = document.querySelector('.dashboard-welcome-container');
    const dashboardContent = document.querySelector('.dashboard-content');
    const subSidebarMenu = document.querySelector('.sub-sidebar');
    const sidebar = document.querySelector('.sidebar');

    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '../../auth/html/login.html';
        });
    }

    const profileBtn = document.getElementById('profileBtn');
    const profilePopup = document.getElementById('profilePopup');

    profileBtn.addEventListener('click', function(e) {
        e.preventDefault();
        profilePopup.style.display = profilePopup.style.display === 'none' ? 'block' : 'none';
    });

    document.addEventListener('click', function(e) {
        if (!profilePopup.contains(e.target) && !profileBtn.contains(e.target)) {
            profilePopup.style.display = 'none';
        }
    });

    renderSidebar();

    if (subSidebarMenu) subSidebarMenu.style.display = 'none';
    if (dashboardContent) dashboardContent.style.display = 'none';
    if (welcomeContainer) welcomeContainer.style.display = '';

    if (sidebar) {
        sidebar.addEventListener('click', (e) => {
            if (e.target.tagName === 'LI') {
                document.querySelectorAll('.dashboard-section').forEach(section => {
                    section.classList.remove('active');
                    section.style.display = 'none';
                });
                const sectionKey = e.target.getAttribute('data-section');
                const section = window.dashboardSections.find(s => s.section === sectionKey);
                
                if (!section.subsections || section.subsections.length === 0) {
                    if (dashboardContent) dashboardContent.style.display = '';
                    if (welcomeContainer) welcomeContainer.style.display = 'none';
                    if (subSidebarMenu) subSidebarMenu.style.display = 'none';
                    
                    const sectionToShow = document.getElementById(section.sectionId);
                    if (sectionToShow) {
                        sectionToShow.style.display = '';
                        setTimeout(() => sectionToShow.classList.add('active'), 10);
                        
                        if (!sectionToShow.innerHTML.trim() || section.forceReload) {
                            loadSection(section.sectionId, section.html, section.js, section.init);
                        } else if (section.init && window[section.init]) {
                            window[section.init]();
                        }
                    }
                } else {
                    renderSubSidebar(sectionKey);
                    if (dashboardContent) dashboardContent.style.display = '';
                    if (welcomeContainer) welcomeContainer.style.display = 'none';
                    if (subSidebarMenu) subSidebarMenu.style.display = '';
                }
            }
        });
    }

    if (subSidebarMenu) {
        subSidebarMenu.addEventListener('click', (e) => {
            if (e.target.tagName === 'LI') {
                const subKey = e.target.getAttribute('data-subsection');
                if (window.showSubSection) {
                    window.showSubSection(subKey);
                }
            }
        });
    }
    if (main && main.classList.contains('dashboard-welcome')) {
        if (subSidebarMenu) subSidebarMenu.style.display = 'none';
        if (dashboardContent) dashboardContent.style.display = 'none';
        if (welcomeContainer) welcomeContainer.style.display = '';
        if (sidebar) sidebar.style.display = '';
    }
}); 