import { OPEN_MATCHES_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, updatePagination } from '../../common/js/utils.js';

let matchesData = [];
let currentPage = 0;
let totalPages = 0;
const pageSize = 5;

async function leaveMatch(matchId) {
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/${matchId}/leave`, {
            method: 'DELETE'
        });

        if (response.ok) {
            Alert.success('Te has salido del partido correctamente');
            fetchMatches();
        } else {
            Alert.error('Error al salir del partido');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al salir del partido.');
    }
}

async function fetchMatches() {
    try {
        const matchesListContainer = document.getElementById('currentMatchesListContainer');
        if (matchesListContainer) {
            matchesListContainer.innerHTML = '<div class="loading">Cargando partidos actuales...</div>';
        }

        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize,
            isCurrent: true
        });

        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/participations?${params.toString()}`);
        if (response.ok) {
            const data = await response.json();
            matchesData = data.content || [];
            totalPages = Math.max(data.totalPages, 1);
            await renderMatches(matchesData);
            updatePagination(currentPage, totalPages);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener los partidos actuales.');
    }
}

async function renderMatches(matches) {
    const matchesListContainer = document.getElementById('currentMatchesListContainer');
    matchesListContainer.innerHTML = '';

    if (matches.length === 0) {
        matchesListContainer.innerHTML = '<div class="no-data">No hay partidos actuales</div>';
        return;
    }

    for (const match of matches) {
        const matchCard = document.createElement('div');
        matchCard.className = 'match-card card';
        matchCard.innerHTML = matchViewTemplate(match);
        matchesListContainer.appendChild(matchCard);
    }
}

function matchViewTemplate(match) {
    const formatDate = (dateStr) => {
        const [year, month, day] = dateStr.split('-');
        return `${day}/${month}/${year}`;
    };

    const formatTime = (timeStr) => {
        return timeStr.substring(0, 5);
    };

    return `
        <div class="match-card-content card-content">
            <div class="match-info card-info">
                <h3 class="match-field-name" style="color: var(--secondary-color)">${match.fieldName}</h3>
                <p class="match-date">Fecha: ${formatDate(match.blockedSlotDTO.date)}</p>
                <p class="match-time">Horario: ${formatTime(match.blockedSlotDTO.startTime)} - ${formatTime(match.blockedSlotDTO.endTime)}</p>
                <p class="match-owner">Reservado por: ${match.owner.name}</p>
                <p class="match-status">Estado: ${match.confirmed ? 'Confirmado' : 'Pendiente'}</p>
                <p class="match-reason">Motivo: ${match.blockedSlotDTO.reason}</p>
                <div class="match-card-actions card-actions">
                    <button class="btn btn-delete delete-btn" onclick="leaveMatch('${match.id}')">
                        <i class="fas fa-trash" style="color: var(--black);"></i> Salir del partido
                    </button>
                </div>
            </div>
        </div>
    `;
}

window.goToPage = function(page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        fetchCurrentMatches();
    }
};

function initCurrentMatches() {    
    fetchMatches();

    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');

    if (prevButton) {
        prevButton.addEventListener('click', () => window.goToPage(currentPage - 1));
    }
    if (nextButton) {
        nextButton.addEventListener('click', () => window.goToPage(currentPage + 1));
    }

    const searchInput = document.getElementById('searchCurrentMatchInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = matchesData.filter(m => 
                m.fieldName.toLowerCase().includes(value) ||
                m.blockedSlotDTO.date.toLowerCase().includes(value) ||
                m.blockedSlotDTO.startTime.toLowerCase().includes(value) ||
                m.blockedSlotDTO.endTime.toLowerCase().includes(value) ||
                m.owner.name.toLowerCase().includes(value) ||
                m.blockedSlotDTO.reason.toLowerCase().includes(value)
            );
            await renderMatches(filtered);
        });
    }
}

window.initCurrentMatches = initCurrentMatches;
window.leaveMatch = leaveMatch;