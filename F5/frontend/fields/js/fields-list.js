import { FIELDS_API_URL } from '../../common/js/constants.js';
import { zonesCABA } from '../../common/js/zones.js';
import { grounds } from '../js/grounds.js';
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
            owned: true,
            page: currentPage,
            size: pageSize
        });

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

function fieldEditTemplate(field) {
    return `
    <div class="field-card-content card-content">
        <div class="field-info card-info">
            <div class="detail-group">
                <span class="detail-label">Nombre:</span>
                <input type="text" class="edit-name" value="${field.name}" placeholder="Nombre">
            </div>
            <div class="detail-group">
                <span class="detail-label">Tipo de suelo:</span>
                <select class="edit-groundType">
                    ${grounds.map(g => `<option value="${g.value}" ${field.groundType === g.value ? 'selected' : ''}>${g.label}</option>`).join('')}
                </select>
            </div>
            <div class="detail-group">
                <span class="detail-label">Techo:</span>
                <select class="edit-hasRoof">
                    <option value="true" ${field.hasRoof ? 'selected' : ''}>Sí</option>
                    <option value="false" ${!field.hasRoof ? 'selected' : ''}>No</option>
                </select>
            </div>
            <div class="detail-group">
                <span class="detail-label">Iluminación:</span>
                <select class="edit-hasIllumination">
                    <option value="true" ${field.hasIllumination ? 'selected' : ''}>Sí</option>
                    <option value="false" ${!field.hasIllumination ? 'selected' : ''}>No</option>
                </select>
            </div>
            <div class="detail-group">
                <span class="detail-label">Zona:</span>
                <select class="edit-zone">
                    ${zonesCABA.map(z => `<option value="${z.value}" ${field.zone === z.value ? 'selected' : ''}>${z.label}</option>`).join('')}
                </select>
            </div>
            <div class="detail-group">
                <span class="detail-label">Dirección:</span>
                <input type="text" class="edit-address" value="${field.address}" placeholder="Dirección">
            </div>
        </div>
            <div class="field-card-actions card-actions">
                <button class="btn btn-edit save-btn" data-name="${field.name}">
                    <i class="fas fa-save" style="color: var(--black);"></i> Guardar
                </button>
                <button class="btn btn-delete cancel-btn" data-name="${field.name}">
                    <i class="fas fa-times" style="color: var(--black);"></i> Cancelar
                </button>
            </div>
    </div>
    `;
}

function fieldViewTemplate(field) {
    return `
    <div class="field-card-content card-content">
        <div class="field-info card-info">
            <h3 class="field-name" style="color: var(--secondary-color);">${field.name}</h3>
            <div class="detail-group">
                <span class="detail-label">Tipo:</span>
                <span class="detail-value">
                    ${grounds.find(g => g.value === field.groundType)?.label || field.groundType}
                </span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Techo:</span>
                <span class="detail-value">${field.hasRoof ? 'Sí' : 'No'}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Iluminación:</span>
                <span class="detail-value">${field.hasIllumination ? 'Sí' : 'No'}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Zona:</span>
                <span class="detail-value">
                    ${zonesCABA.find(z => z.value === field.zone)?.label || field.zone}
                </span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Dirección:</span>
                <span class="detail-value">${field.address}</span>
            </div>
        </div>
        <div class="field-card-actions card-actions">
            <button class="btn btn-edit edit-btn" data-name="${field.name}">
                <i class="fas fa-edit" style="color: var(--black);"></i> Editar
            </button>
            <button class="btn btn-delete delete-btn" data-name="${field.name}">
                <i class="fas fa-trash" style="color: var(--black);"></i> Eliminar
            </button>
        </div>
    </div>
    `;
}

async function renderFields(fields) {
    const fieldsListContainer = document.getElementById('fieldsListContainer');
    fieldsListContainer.innerHTML = '';

    if (fields.length === 0) {
        fieldsListContainer.innerHTML = '<div class="no-data">No hay canchas registradas</div>';
        return;
    }

    for (const field of fields) {
        const fieldCard = document.createElement('div');
        fieldCard.className = 'field-card card';
        fieldCard.innerHTML = field.isEditing ? fieldEditTemplate(field) : fieldViewTemplate(field);
        fieldsListContainer.appendChild(fieldCard);
    }

    fieldsListContainer.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            fieldsData = fieldsData.map(f => f.name === name ? { ...f, isEditing: true } : { ...f, isEditing: false });
            await renderFields(fieldsData);
        });
    });

    fieldsListContainer.querySelectorAll('.save-btn').forEach(async btn => {
        btn.addEventListener('click', async () => {
            const oldName = btn.getAttribute('data-name');
            const card = btn.closest('.field-card');
            const name = card.querySelector('.edit-name');
            const groundType = card.querySelector('.edit-groundType');
            const hasRoof = card.querySelector('.edit-hasRoof');
            const hasIllumination = card.querySelector('.edit-hasIllumination');
            const zone = card.querySelector('.edit-zone');
            const address = card.querySelector('.edit-address');

            const updatedField = {
                name: name.value,
                enabled: 'true',
                groundType: groundType.value,
                hasRoof: hasRoof.value === 'true',
                hasIllumination: hasIllumination.value === 'true',
                zone: zone.value,
                address: address.value
            };

            try {
                const response = await fetchWithAuth(`${FIELDS_API_URL}/${encodeURIComponent(oldName)}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedField)
                });
                if (response.ok) {
                    fieldsData = fieldsData.map(f => f.name === oldName ? { ...updatedField, isEditing: false } : f);
                    await renderFields(fieldsData);
                    Alert.success('¡Cancha actualizada con éxito!');
                } else {
                    Alert.error('No se pudo actualizar la cancha.');
                }
            } catch (error) {
                Alert.error(error.message || 'Error al actualizar la cancha');
            }
        });
    });

    fieldsListContainer.querySelectorAll('.cancel-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            fieldsData = fieldsData.map(f => f.name === name ? { ...f, isEditing: false } : f);
            await renderFields(fieldsData);
        });
    });

    fieldsListContainer.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            if (confirm('¿Seguro que deseas eliminar esta cancha?')) {
                await deleteField(name);
            }
        });
    });
}

async function deleteField(name) {
    try {
        const response = await fetchWithAuth(`${FIELDS_API_URL}/${encodeURIComponent(name)}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            fieldsData = fieldsData.filter(f => f.name !== name);
            totalPages = Math.max(totalPages - 1, 1);
            await renderFields(fieldsData);
            updatePagination(currentPage, totalPages);
            Alert.success('¡Cancha eliminada con éxito!');
        } else {
            Alert.error('No se pudo eliminar la cancha.');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al eliminar la cancha');
    }
}

window.goToPage = function(page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        fetchFields();
    }
};

function initFieldsList() {
    fetchFields();

    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');

    if (prevButton) {
        prevButton.addEventListener('click', () => window.goToPage(currentPage - 1));
    }
    if (nextButton) {
        nextButton.addEventListener('click', () => window.goToPage(currentPage + 1));
    }

    const searchInput = document.getElementById('searchFieldInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = fieldsData.filter(f => f.name.toLowerCase().includes(value));
            await renderFields(filtered);
        });
    }
}

window.initFieldsList = initFieldsList;
