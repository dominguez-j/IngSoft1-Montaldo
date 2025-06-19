import { FIELDS_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

let currentPage = 0;
let totalPages = 0;
const pageSize = 5;
let selectedSlot = null;
let confirmedSlot = null;

function formatDate(dateStr) {
    const [year, month, day] = dateStr.split('-').map(Number);
    const date = new Date(year, month - 1, day);
    return date.toLocaleDateString('es-AR', { 
        weekday: 'long', 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
    });
}

function formatTime(timeStr) {
    return timeStr.substring(0, 5);
}

function freeSlotTemplate(slot) {
    const isSelected = selectedSlot?.slotNumber === slot.slotNumber && selectedSlot?.date === slot.date;
    const isConfirmed = confirmedSlot?.slotNumber === slot.slotNumber && confirmedSlot?.date === slot.date;
    
    return `
        <div class="card free-slot ${isSelected || isConfirmed ? 'selected' : ''}" 
             data-slot-number="${slot.slotNumber}" 
             data-date="${slot.date}">
            <div class="card-content">
                <div class="slot-date">${formatDate(slot.date)}</div>
                <div class="slot-time">${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}</div>
                ${isSelected && !isConfirmed ? `
                    <div class="slot-actions">
                        <button class="btn btn-confirm">
                            Confirmar horario
                        </button>
                    </div>
                ` : ''}
            </div>
        </div>
    `;
}

function renderFreeSlots(slots, totalElements) {
    const container = document.getElementById('freeSlotsListContainer');

    if (confirmedSlot) {
        const confirmedSlotData = slots.find(slot => 
            slot.slotNumber === confirmedSlot.slotNumber && 
            slot.date === confirmedSlot.date
        );
        
        if (confirmedSlotData) {
            container.innerHTML = `
                <h3>Horario seleccionado</h3>
                <div class="free-slots-grid">
                    ${freeSlotTemplate(confirmedSlotData)}
                </div>
            `;
            return;
        }
    }

    if (!slots || slots.length === 0) {
        container.innerHTML = `
            <div class="no-data">No hay horarios disponibles</div>
            <div class="pagination">
                <button class="btn-page" id="prevPage" disabled>Anterior</button>
                <span class="page-info">Página 1 de 1</span>
                <button class="btn-page" id="nextPage" disabled>Siguiente</button>
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <h3>Horarios disponibles</h3>
        <div class="free-slots-grid">
            ${slots.map(slot => freeSlotTemplate(slot)).join('')}
        </div>
        <div class="pagination">
            <button class="btn-page" id="prevPage" ${currentPage === 0 ? 'disabled' : ''}>Anterior</button>
            <span class="page-info">Página ${currentPage + 1} de ${totalPages}</span>
            <button class="btn-page" id="nextPage" ${currentPage >= totalPages - 1 ? 'disabled' : ''}>Siguiente</button>
        </div>
    `;

    if (!confirmedSlot) {
        container.querySelectorAll('.free-slot').forEach(card => {
            card.addEventListener('click', () => {
                container.querySelectorAll('.free-slot').forEach(c => c.classList.remove('selected'));
                card.classList.add('selected');
                selectedSlot = {
                    slotNumber: parseInt(card.dataset.slotNumber),
                    date: card.dataset.date
                };
                renderFreeSlots(slots, totalElements);
            });
        });

        container.querySelectorAll('.btn-confirm').forEach(button => {
            button.addEventListener('click', async (e) => {
                e.stopPropagation();
                confirmedSlot = selectedSlot;
                if (window.reserveSlot) {
                    await window.reserveSlot(window.currentFieldName, selectedSlot);
                }
                renderFreeSlots(slots, totalElements);
            });
        });
    }

    const prevButton = container.querySelector('#prevPage');
    const nextButton = container.querySelector('#nextPage');

    if (prevButton) {
        prevButton.addEventListener('click', () => {
            if (currentPage > 0) {
                currentPage--;
                fetchFreeSlots(window.currentFieldName, 7);
            }
        });
    }
    if (nextButton) {
        nextButton.addEventListener('click', () => {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fetchFreeSlots(window.currentFieldName, 7);
            }
        });
    }
}

export async function fetchFreeSlots(fieldName, numberOfDays = 7) {
    try {
        window.currentFieldName = fieldName;
        const freeSlotsListContainer = document.getElementById('freeSlotsListContainer');
        if (freeSlotsListContainer) {
            freeSlotsListContainer.innerHTML = '<div class="loading">Cargando turnos disponibles...</div>';
        }

        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize
        });

        const response = await fetchWithAuth(`${FIELDS_API_URL}/${fieldName}/free-slots/${numberOfDays}?${params.toString()}`);
        if (response.ok) {
            const data = await response.json();
            totalPages = Math.max(data.totalPages, 1);
            renderFreeSlots(data.content, data.totalElements);
            return data.content;
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener los horarios disponibles.');
    }
    return [];
}

export function clearSlotsState() {
    currentPage = 0;
    totalPages = 0;
    selectedSlot = null;
    confirmedSlot = null;   
    const container = document.getElementById('freeSlotsListContainer');
    if (container) {
        container.innerHTML = '';
    }
}

export function setConfirmedSlot(slot) {
    confirmedSlot = slot;
}

export { confirmedSlot }; 