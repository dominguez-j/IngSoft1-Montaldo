import { OPEN_MATCHES_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, updatePagination } from '../../common/js/utils.js';
import { populateFieldNameSelect } from '../../matches/js/fields.js';

let matchesData = [];
let currentPage = 0;
let totalPages = 0;
const pageSize = 5;

function getFilters() {
    return {
        fieldName: document.getElementById('filterName')?.value || '',
        date: document.getElementById('filterDate')?.value || '',
    };
}

async function fetchOpenMatches() {
    const openMatchesListContainer = document.getElementById('openMatchesListContainer');
    if (openMatchesListContainer) {
        openMatchesListContainer.innerHTML = '<div class="loading">Cargando partidos abiertos...</div>';
    }

    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
    });

    const filters = getFilters();
    if (filters.fieldName) params.append('fieldName', filters.fieldName);
    if (filters.date) params.append('date', filters.date);
    
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}?${params.toString()}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        if (response.ok) {
            const data = await response.json();
            matchesData = data.content || [];
            totalPages = Math.max(data.totalPages, 1);
            await renderOpenMatches(matchesData);
            updatePagination(currentPage, totalPages);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener los partidos abiertos.');
    }
}

async function renderOpenMatches(matches) {
    const openMatchesListContainer = document.getElementById('openMatchesListContainer');
    openMatchesListContainer.innerHTML = '';

    if (matches.length === 0) {
        openMatchesListContainer.innerHTML = '<div class="no-data">No hay partidos abiertos</div>';
        return;
    }

    for (const match of matches) {
        const matchCard = document.createElement('div');
        matchCard.className = 'match-card card';
        matchCard.innerHTML = openMatchViewTemplate(match);
        openMatchesListContainer.appendChild(matchCard);
    }
}

function openMatchViewTemplate(match) {
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
                <h3 class="match-field" style="color: var(--secondary-color)">Cancha: ${match.fieldName}</h3>
                <p class="match-date">Fecha: ${formatDate(match.blockedSlotDTO.date)}</p>
                <p class="match-time">Horario: ${formatTime(match.blockedSlotDTO.startTime)} - ${formatTime(match.blockedSlotDTO.endTime)}</p>
                <p class="match-status">Estado: ${match.confirmed ? 'Confirmado' : 'Pendiente'}</p>
                <p class="match-players">Jugadores: ${match.players.size} / ${match.maxPlayers}</p>
                <button type="submit" class="btn btn-primary" onclick="joinMatch('${match.id}')">Unirme al partido</button>
            </div>
        </div>
    `;
}

async function joinMatch(matchId) {
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/${matchId}/join`, {
            method: 'PUT'
        });

        if (response.ok) {
            Alert.success('Te has unido al partido correctamente');
            fetchOpenMatches();
        } else {
            Alert.error('Error al unirse al partido o ya estás en el partido');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al unirse al partido');
    }
};

window.goToPage = function(page) {
    if (page >= 0 && page < totalPages) {
        currentPage = page;
        fetchOpenMatches();
    }
};

function initOpenMatches() {   
    
    populateFieldNameSelect('filterName', 'Todos');

    const filterIds = ['filterName', 'filterDate'];
    filterIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener('change', () => {
                currentPage = 0;
                fetchOpenMatches();
            });
        }
    });

    document.getElementById('prevPage')?.addEventListener('click', () => window.goToPage(currentPage - 1));
    document.getElementById('nextPage')?.addEventListener('click', () => window.goToPage(currentPage + 1));

    const searchInput = document.getElementById('searchOpenMatchInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = matchesData.filter(m => 
                m.fieldName.toLowerCase().includes(value) ||
                m.blockedSlotDTO.date.toLowerCase().includes(value) ||
                m.blockedSlotDTO.startTime.toLowerCase().includes(value) ||
                m.blockedSlotDTO.endTime.toLowerCase().includes(value)
            );
            await renderOpenMatches(filtered);
        });
    }

    fetchOpenMatches();
}

window.initOpenMatches = initOpenMatches;
window.joinMatch = joinMatch;