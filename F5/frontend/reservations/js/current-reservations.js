import { BLOCKED_SLOTS_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, updatePagination } from '../../common/js/utils.js';

let reservationsData = [];
let currentPage = 0;
let totalPages = 0;
const pageSize = 5;

async function fetchReservations() {
    try {
        const reservationsListContainer = document.getElementById('reservationsListContainer');
        if (reservationsListContainer) {
            reservationsListContainer.innerHTML = '<div class="loading">Cargando reservas actuales...</div>';
        }

        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize,
            isCurrent: true
        });

        const response = await fetchWithAuth(`${BLOCKED_SLOTS_API_URL}/history?${params.toString()}`);
        if (response.ok) {
            const data = await response.json();
            reservationsData = data.content || [];
            totalPages = Math.max(data.totalPages, 1);
            await renderReservations(reservationsData);
            updatePagination(currentPage, totalPages);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener las reservas.');
    }
}

async function renderReservations(reservations) {
    const reservationsListContainer = document.getElementById('currentReservationsListContainer');
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
                <h3 class="reservation-name" style="color: var(--secondary-color)">${reservation.fieldName}</h3>
                <p class="reservation-date">Fecha: ${formatDate(reservation.date)}</p>
                <p class="reservation-time">Horario: ${formatTime(reservation.startTime)} - ${formatTime(reservation.endTime)}</p>
                <p class="reservation-owner">Reservado por: ${reservation.ownerName}</p>
                <p class="reservation-reason">Motivo: ${reservation.reason}</p>
            </div>
        </div>
    `;
}

window.goToPage = function(page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        fetchCurrentReservations();
    }
};

function initCurrentReservations() {    
    fetchReservations();

    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');

    if (prevButton) {
        prevButton.addEventListener('click', () => window.goToPage(currentPage - 1));
    }
    if (nextButton) {
        nextButton.addEventListener('click', () => window.goToPage(currentPage + 1));
    }

    const searchInput = document.getElementById('searchCurrentReservationInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = reservationsData.filter(r => 
                r.fieldName.toLowerCase().includes(value) ||
                r.date.toLowerCase().includes(value) ||
                r.startTime.toLowerCase().includes(value) ||
                r.endTime.toLowerCase().includes(value) ||
                r.ownerName.toLowerCase().includes(value) ||
                r.reason.toLowerCase().includes(value)
            );
            await renderReservations(filtered);
        });
    }
}

window.initCurrentReservations = initCurrentReservations;