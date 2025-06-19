import { getSubsectionByKey } from './section-registry.js';
import { loadSection } from './section-loader.js';

export async function showSubSection(subsectionKey, fieldData) {
    const found = getSubsectionByKey(subsectionKey, fieldData);
    if (!found) {
        console.error('No se encontró la subsección:', subsectionKey);
        return;
    }
    
    document.querySelectorAll('.dashboard-section').forEach(section => {
        section.classList.remove('active');
        section.style.display = 'none';
    });

    const sectionToShow = document.getElementById(found.sectionId);
    if (!sectionToShow) {
        console.error('No se encontró el elemento con id:', found.sectionId);
        return;
    }

    if (fieldData) {
        window.currentField = fieldData;
        window.currentFieldName = fieldData?.name;
    }

    sectionToShow.style.display = '';
    setTimeout(() => sectionToShow.classList.add('active'), 10);

    const needsLoad = !sectionToShow.innerHTML.trim() || found.forceReload;
    if (needsLoad) {
        await loadSection(found.sectionId, found.html, found.js, found.init);
    } else if (found.init && typeof window[found.init] === 'function') {
        window[found.init]();
    }
}

export async function showFieldDetailsWithList(fieldData) {
    document.querySelectorAll('.dashboard-section').forEach(section => {
        section.classList.remove('active');
        section.style.display = 'none';
    });

    const listSection = document.getElementById('section-all-fields');
    if (listSection) {
        listSection.style.display = '';
        setTimeout(() => listSection.classList.add('active'), 10);
    }

    const detailsSection = document.getElementById('section-field-details');
    let fieldKey = null;
    if (fieldData && typeof fieldData.name === 'string') {
        fieldKey = `field-${fieldData.name.replaceAll(' ', '_')}`;
    }

    if (detailsSection && fieldKey) {
        detailsSection.style.display = '';
        setTimeout(() => detailsSection.classList.add('active'), 10);

        window.currentField = fieldData;
        window.currentFieldName = fieldData?.name;

        const found = getSubsectionByKey(fieldKey, fieldData);
        if (found) {
            const needsLoad = !detailsSection.innerHTML.trim() || found.forceReload;
            if (needsLoad) {
                await loadSection(found.sectionId, found.html, found.js, found.init);
            } else if (found.init && typeof window[found.init] === 'function') {
                window[found.init]();
            }
        }
    }

    if (fieldData) {
        window.currentField = fieldData;
        window.currentFieldName = fieldData?.name;
    }
}

window.showSubSection = showSubSection;
window.showFieldDetailsWithList = showFieldDetailsWithList;
