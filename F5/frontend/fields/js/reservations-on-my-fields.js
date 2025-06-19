import { FIELDS_API_URL } from '../../common/js/constants.js';
import { zonesCABA } from '../../common/js/zones.js';
import { grounds } from '../js/grounds.js';
import { Alert, fetchWithAuth, updatePagination } from '../../common/js/utils.js';

let reservationsData = [];

async function fetchReservations() {
    try {
        const reservationsListContainer = document.getElementById('reservationsListContainer');
        if (reservationsListContainer) {
            reservationsListContainer.innerHTML = '<div class="loading">Cargando reservas...</div>';
        }

        const response = await fetchWithAuth(`${FIELDS_API_URL}/my-blocked-slots`);
        if (response.ok) {
            const data = await response.json();
            reservationsData = data || [];
            await renderReservations(reservationsData);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener las reservas.');
    }
}

function reservationViewTemplate(reservation) {
    const formatDate = (dateStr) => {   
        const [year, month, day] = dateStr.split('-');
        return `${day}/${month}/${year}`;
    };

    const formatTime = (timeStr) => {
        return timeStr.substring(0, 5);
    };

    return `
    <div class="reservation-card-content card-content">
        <div class="reservation-info card-info">
            <h3 class="reservation-field" style="color: var(--secondary-color);">${reservation.fieldName}</h3>
            <div class="detail-group">
                <span class="detail-label">Fecha:</span>
                <span class="detail-value">${formatDate(reservation.date)}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Horario:</span>
                <span class="detail-value">${formatTime(reservation.startTime)} - ${formatTime(reservation.endTime)}</span>
            </div>
            <div class="detail-group">
                <span class="detail-label">Motivo:</span>
                <span class="detail-value">${reservation.reason}</span>
            </div>
        </div>
        <div class="reservation-card-actions card-actions">
            <button class="btn btn-delete delete-reservation-btn" data-id="${reservation.id}">
                <i class="fas fa-trash" style="color: var(--black);"></i> Eliminar
            </button>
        </div>
    </div>
    `;
}

async function renderReservations(reservations) {
    const reservationsListContainer = document.getElementById('reservationsListContainer');
    if (!reservationsListContainer) return;
    
    reservationsListContainer.innerHTML = '';

    if (reservations.length === 0) {
        reservationsListContainer.innerHTML = '<div class="no-data">No hay reservas</div>';
        return;
    }

    for (const reservation of reservations) {
        const reservationCard = document.createElement('div');
        reservationCard.className = 'reservation-card card';
        reservationCard.innerHTML = reservationViewTemplate(reservation);
        reservationsListContainer.appendChild(reservationCard);
    }

    reservationsListContainer.querySelectorAll('.delete-reservation-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const reservationId = btn.getAttribute('data-id');
            if (confirm('¿Seguro que deseas eliminar esta reserva?')) {
                await deleteReservation(reservationId);
            }
        });
    });
}

async function deleteReservation(reservationId) {
    try {
        const response = await fetchWithAuth(`${FIELDS_API_URL}/blocked-slots/${reservationId}`, {
            method: 'DELETE'
        });
        
        if (response.ok) {
            reservationsData = reservationsData.filter(r => r.id !== parseInt(reservationId));
            await renderReservations(reservationsData);
            Alert.success('¡Reserva eliminada con éxito!');
        } else {
            Alert.error('No se pudo eliminar la reserva.');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al eliminar la reserva');
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

function initReservationsOnMyFields() {
    fetchReservations();

    const searchInput = document.getElementById('searchReservationInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = reservationsData.filter(r => r.fieldName.toLowerCase().includes(value));
            await renderReservations(filtered);
        });
    }
}

window.initReservationsOnMyFields = initReservationsOnMyFields;
