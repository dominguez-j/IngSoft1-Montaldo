import { FIELDS_API_URL } from '../../common/js/constants.js';
import { zonesCABA, populateZoneSelect } from '../../common/js/zones.js';
import { grounds, populateGroundSelect } from '../js/grounds.js';
import { Alert, fetchWithAuth, updatePagination } from '../../common/js/utils.js';

let fieldsData = [];
let currentPage = 0;
let totalPages = 0;
const pageSize = 5;

async function fetchFields() {
    try { 
        const fieldsListContainer = document.getElementById('fieldsListContainer');
        if (fieldsListContainer) {
            fieldsListContainer.innerHTML = '<div class="loading">Cargando canchas...</div>';
        }

        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize
        });

        const filters = getFilters();
        if (filters.groundType) params.append('groundType', filters.groundType);
        if (filters.zone) params.append('zone', filters.zone);
        if (filters.hasRoof) params.append('hasRoof', filters.hasRoof);
        if (filters.hasIllumination) params.append('hasIllumination', filters.hasIllumination);

        const response = await fetchWithAuth(`${FIELDS_API_URL}?${params.toString()}`);
        if (response.ok) {
            const data = await response.json();
            fieldsData = data.content || [];
            totalPages = Math.max(data.totalPages, 1);
            await renderFields(fieldsData);
            updatePagination(currentPage, totalPages);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener las canchas.');
    }
}
    
async function renderFields(fields) {
    const container = document.getElementById('fieldsListContainer');
    if (!container) return;

    if (fields.length === 0) {
        container.innerHTML = renderNoFieldsMessage();
        return;
    }

    container.innerHTML = fields.map(field => renderFieldCard(field)).join('');

    container.querySelectorAll('.field-card').forEach(card => {
        card.addEventListener('click', () => {
            const fieldData = JSON.parse(card.dataset.field);
            showFieldDetailsWithList(fieldData);
        });
    });
}

function renderNoFieldsMessage() {
    return `<div>No hay canchas para mostrar.</div>`;
}

function renderFieldCard(field) {
    const zoneLabel = zonesCABA.find(z => z.value === field.zone)?.label || field.zone;
    const groundLabel = grounds.find(g => g.value === field.groundType)?.label || field.groundType;

    return `
        <div class="field-card card" data-field='${JSON.stringify(field)}'>
            <div class="field-card-content card-content">
                <div class="field-info card-info">
                    <h3 class="field-name" style="color: var(--secondary-color);">${field.name}</h3>
                    <div class="field-details">
                        ${renderFieldDetail("Tipo", groundLabel)}
                        ${renderFieldDetail("Zona", zoneLabel)}
                    </div>
                </div>
            </div>
        </div>
    `;
}

function renderFieldDetail(label, value) {
    return `
        <div class="detail-group">
            <span class="detail-label">${label}:</span>
            <span class="detail-value">${value}</span>
        </div>
    `;
}

function getFilters() {
    return {
        groundType: document.getElementById('filterGround')?.value || '',
        zone: document.getElementById('filterZone')?.value || '',
        hasRoof: document.getElementById('filterRoof')?.value || '',
        hasIllumination: document.getElementById('filterIllumination')?.value || '',
    };
}

window.goToPage = function (page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        fetchFields();
    }
};

function initAllFields() {
    window.currentField = null;
    window.currentFieldName = null;
    
    const detailsSection = document.getElementById('section-field-details');
    if (detailsSection) {
        detailsSection.style.display = 'none';
        detailsSection.classList.remove('active');
    }

    populateGroundSelect('filterGround', 'Todos');
    populateZoneSelect('filterZone', 'Todos');

    const filterIds = ['filterGround', 'filterZone', 'filterRoof', 'filterIllumination'];
    filterIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('change', () => {
                currentPage = 0;
                fetchFields();
            });
        }
    });

    document.getElementById('prevPage')?.addEventListener('click', () => window.goToPage(currentPage - 1));
    document.getElementById('nextPage')?.addEventListener('click', () => window.goToPage(currentPage + 1));

    document.getElementById('searchFieldInput')?.addEventListener('input', async (e) => {
        const term = e.target.value.toLowerCase();
        const filtered = fieldsData.filter(f => f.name.toLowerCase().includes(term));
        await renderFields(filtered);
    });

    fetchFields();
}

window.initAllFields = initAllFields;
