import { BLOCKED_SLOTS_API_URL, OPEN_MATCHES_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

let matchesData = [];

async function fetchMyBlockedSlots() {
    try {
        const response = await fetchWithAuth(`${BLOCKED_SLOTS_API_URL}/history?isCurrent=true`);
        if (!response.ok) {
            throw new Error('Error al obtener las reservas de partidos');
        }
        const data = await response.json();
        return data.content;
    } catch (error) {
        Alert.error(error.message || 'Error al obtener las reservas de partidos');
        return [];
    }
}

async function fetchOpenMatchByBlockedSlot(blockedSlotId) {
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/blocked-slot/${blockedSlotId}`);
        if (!response.ok) {
            throw new Error('Error al obtener el partido abierto');
        }
        return await response.json();
    } catch (error) {
        Alert.error(error.message || 'Error al obtener el partido abierto');
        return null;
    }
}

async function confirmMatch(matchId) {
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/${matchId}`, {
            method: 'PUT'
        });

        if (response.ok) {
            Alert.success('Partido confirmado exitosamente');
            loadMatches();
        } else {
            const errorData = await response.json();
            Alert.error(errorData.message || 'Error al confirmar el partido');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al confirmar el partido');
    }
}

function toggleManualOrganization(matchId) {
    const playersListDiv = document.getElementById(`players-list-${matchId}`);
    const saveButton = document.getElementById(`save-teams-${matchId}`);
    const autoOptionsDiv = document.getElementById(`auto-options-${matchId}`);
    const autoBtn = document.getElementById(`auto-btn-${matchId}`);
    const manualBtn = document.getElementById(`manual-btn-${matchId}`);
    
    if (playersListDiv) {
        const isVisible = playersListDiv.style.display !== 'none';
        playersListDiv.style.display = isVisible ? 'none' : 'block';
        saveButton.style.display = isVisible ? 'none' : 'block';
        
        if (!isVisible) {
            autoOptionsDiv.style.display = 'none';
            manualBtn.classList.remove('btn-secondary');
            manualBtn.classList.add('btn-primary');
            autoBtn.classList.remove('btn-primary');
            autoBtn.classList.add('btn-secondary');
        } else {
            manualBtn.classList.remove('btn-primary');
            manualBtn.classList.add('btn-secondary');
        }
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

    const playersArray = match.players?.content || [];
    
    const playersList = playersArray.map(player => `
        <div class="player-item d-flex align-items-center justify-content-between py-1" data-player-email="${player.email}">
            <span>${player.name}</span>
            <div class="btn-group btn-group-sm ms-2">
                <button class="btn btn-sm btn-secondary team-a-btn" style="padding: 0.25rem 0.5rem;">A</button>
                <button class="btn btn-sm btn-secondary team-b-btn" style="padding: 0.25rem 0.5rem;">B</button>
            </div>
        </div>
    `).join('');

    const playersCount = playersArray.length;
    const canConfirm = !match.confirmed && playersCount >= match.minPlayers && playersCount % 2 === 0;
    
    const currentTeams = match.teamAssignmentsDTO ? `
        <div class="current-teams mt-3">
            <h4>Equipos Actuales</h4>
            <div class="team">
                <h5>Equipo A</h5>
                <ul>
                    ${match.teamAssignmentsDTO.teamA.map(player => `
                        <li>${player.name}</li>
                    `).join('')}
                </ul>
            </div>
            <div class="team">
                <h5>Equipo B</h5>
                <ul>
                    ${match.teamAssignmentsDTO.teamB.map(player => `
                        <li>${player.name}</li>
                    `).join('')}
                </ul>
            </div>
        </div>
    ` : '';

    return `
        <div class="match-card-content card-content">
            <div class="match-info card-info">
                <h3 class="match-field" style="color: var(--secondary-color)">Cancha: ${match.fieldName}</h3>
                <p class="match-date">Fecha: ${formatDate(match.blockedSlotDTO.date)}</p>
                <p class="match-time">Horario: ${formatTime(match.blockedSlotDTO.startTime)} - ${formatTime(match.blockedSlotDTO.endTime)}</p>
                <p class="match-status">Estado: ${match.confirmed ? 'Confirmado' : 'Pendiente'}</p>
                <p class="match-players">Jugadores: ${playersCount} / ${match.maxPlayers}</p>
                <p class="match-min-players">Mínimo necesario: ${match.minPlayers}</p>
                
                ${currentTeams}

                ${!match.confirmed ? `
                    <div class="confirmation-controls">
                        ${canConfirm ? 
                            `<button class="btn btn-primary" onclick="confirmMatch('${match.id}')">Confirmar Partido</button>` 
                            : `<div class="alert alert-warning">
                                Se necesitan al menos ${match.minPlayers} jugadores y un número par para confirmar el partido
                               </div>`
                        }
                    </div>
                ` : `
                    <div class="organization-controls">
                        <h4>Organización de Equipos</h4>
                        <div class="d-flex gap-2">
                            <button id="auto-btn-${match.id}" class="btn btn-secondary" onclick="toggleAutoOptions('${match.id}')">Automático</button>
                            <button id="manual-btn-${match.id}" class="btn btn-secondary" onclick="toggleManualOrganization('${match.id}')">Manual</button>
                        </div>
                        <div id="auto-options-${match.id}" class="mt-2" style="display: none;">
                            <button class="btn btn-secondary btn-sm me-2" onclick="organizeTeamsAuto('${match.id}', 'RANDOM')">Aleatorio</button>
                            <button class="btn btn-secondary btn-sm" onclick="organizeTeamsAuto('${match.id}', 'BY_AGE')">Por Edad</button>
                        </div>
                    </div>

                    <div id="players-list-${match.id}" class="players-list" style="display: none;">
                        <h4>Jugadores</h4>
                        ${playersList}
                    </div>

                    <button id="save-teams-${match.id}" class="btn btn-primary mt-3" style="display: none;" onclick="saveTeams('${match.id}')">
                        Guardar Equipos
                    </button>
                `}
            </div>
        </div>
    `;
}

async function renderOpenMatches(matches) {
    const openMatchesListContainer = document.getElementById('openMatchesListContainer');
    if (!openMatchesListContainer) return;
    
    openMatchesListContainer.innerHTML = '';

    if (matches.length === 0) {
        openMatchesListContainer.innerHTML = '<div class="no-data">No hay partidos abiertos para organizar</div>';
        return;
    }

    for (const match of matches) {
        const matchCard = document.createElement('div');
        matchCard.className = 'match-card card';
        matchCard.innerHTML = openMatchViewTemplate(match);
        openMatchesListContainer.appendChild(matchCard);
    }
}

async function organizeTeamsAuto(matchId, strategy) {
    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/${matchId}/organize-teams?strategy=${strategy}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            Alert.success('Equipos organizados automáticamente');
            loadMatches();
        } else {
            Alert.error('Error al organizar los equipos automáticamente');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al organizar los equipos');
    }
}

async function saveTeams(matchId) {
    const assignments = [];
    const playerElements = document.querySelectorAll(`#players-list-${matchId} .player-item`);
    
    playerElements.forEach(element => {
        const playerEmail = element.dataset.playerEmail;
        const teamABtn = element.querySelector('.team-a-btn');
        const teamBBtn = element.querySelector('.team-b-btn');
        
        if (teamABtn.classList.contains('btn-primary')) {
            assignments.push({
                email: playerEmail,
                team: 'A'
            });
        } else if (teamBBtn.classList.contains('btn-primary')) {
            assignments.push({
                email: playerEmail,
                team: 'B'
            });
        }
    });

    if (assignments.length === 0) {
        Alert.error('Debes asignar al menos un jugador a un equipo');
        return;
    }

    try {
        const response = await fetchWithAuth(`${OPEN_MATCHES_API_URL}/${matchId}/organize-teams/manual`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                assignments: assignments
            })
        });

        if (response.ok) {
            Alert.success('Equipos guardados correctamente');
            loadMatches();
        } else {
            const errorText = await response.text();
            console.error('Error response text:', errorText);
            try {
                const errorData = JSON.parse(errorText);
                Alert.error(errorData.message || 'Error al guardar los equipos');
            } catch (e) {
                Alert.error('Error al guardar los equipos: ' + errorText);
            }
        }
    } catch (error) {
        console.error('Error completo:', error);
        Alert.error(error.message || 'Error al guardar los equipos');
    }
}

async function loadMatches() {
    const blockedSlots = await fetchMyBlockedSlots();
    const matchPromises = blockedSlots.map(slot => fetchOpenMatchByBlockedSlot(slot.id));
    const matches = await Promise.all(matchPromises);
    
    matchesData = matches.filter(match => match !== null);
    
    const openMatchesListContainer = document.getElementById('openMatchesListContainer');
    if (openMatchesListContainer) {
        openMatchesListContainer.innerHTML = '<div class="loading">Cargando partidos abiertos...</div>';
    }
    
    await renderOpenMatches(matchesData);
}

function initOrganizeTeamsMatches() {   
    loadMatches();

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

    document.addEventListener('click', (e) => {
        if (e.target.classList.contains('team-a-btn') || e.target.classList.contains('team-b-btn')) {
            const playerItem = e.target.closest('.player-item');
            const teamABtn = playerItem.querySelector('.team-a-btn');
            const teamBBtn = playerItem.querySelector('.team-b-btn');

            if (e.target.classList.contains('team-a-btn')) {
                teamABtn.classList.toggle('btn-primary');
                teamABtn.classList.toggle('btn-secondary');
                teamBBtn.classList.remove('btn-primary');
                teamBBtn.classList.add('btn-secondary');
            } else {
                teamBBtn.classList.toggle('btn-primary');
                teamBBtn.classList.toggle('btn-secondary');
                teamABtn.classList.remove('btn-primary');
                teamABtn.classList.add('btn-secondary');
            }
        }
    });
}

function toggleAutoOptions(matchId) {
    const autoOptionsDiv = document.getElementById(`auto-options-${matchId}`);
    const playersListDiv = document.getElementById(`players-list-${matchId}`);
    const saveButton = document.getElementById(`save-teams-${matchId}`);
    const autoBtn = document.getElementById(`auto-btn-${matchId}`);
    const manualBtn = document.getElementById(`manual-btn-${matchId}`);
    
    if (autoOptionsDiv) {
        const isVisible = autoOptionsDiv.style.display !== 'none';
        autoOptionsDiv.style.display = isVisible ? 'none' : 'block';
        
        if (!isVisible) {
            playersListDiv.style.display = 'none';
            saveButton.style.display = 'none';
            autoBtn.classList.remove('btn-secondary');
            autoBtn.classList.add('btn-primary');
            manualBtn.classList.remove('btn-primary');
            manualBtn.classList.add('btn-secondary');
        } else {
            autoBtn.classList.remove('btn-primary');
            autoBtn.classList.add('btn-secondary');
        }
    }
}

window.initOrganizeTeamsMatches = initOrganizeTeamsMatches;
window.organizeTeamsAuto = organizeTeamsAuto;
window.saveTeams = saveTeams;
window.confirmMatch = confirmMatch;
window.toggleManualOrganization = toggleManualOrganization;
window.toggleAutoOptions = toggleAutoOptions;
