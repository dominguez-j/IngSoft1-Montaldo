export function hideSectionsExcept(sectionIdToShow) {
    document.querySelectorAll('.dashboard-section').forEach(section => {
        if (section.id !== sectionIdToShow) {
            section.classList.remove('active');
            section.style.display = 'none';
        }
    });

    document.querySelectorAll('.dashboard-subsection').forEach(sub => {
        if (sub.id !== sectionIdToShow) {
            sub.classList.remove('active');
            setTimeout(() => { sub.style.display = 'none'; }, 250);
        }
    });
}

export function showSection(sectionId) {
    const el = document.getElementById(sectionId);
    if (!el) return;

    el.style.display = '';
    setTimeout(() => el.classList.add('active'), 10);
}
