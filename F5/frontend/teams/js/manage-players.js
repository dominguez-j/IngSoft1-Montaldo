import { TEAMS_API_URL } from '../../common/js/constants.js';
import { fetchTeams, renderTeams } from '../js/name-teams.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

let currentTeam = null;
let players = [];

async function deletePlayer(userMail) {   
    try {
        const response = await fetchWithAuth(`${TEAMS_API_URL}/${currentTeam}/members/${userMail}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const teams = await fetchTeams(true);
            const selectedTeam = teams.find(team => team.teamName === currentTeam);
            if (selectedTeam) {
                players = selectedTeam.members || [];
                await renderPlayers(players);
            }
            Alert.success('Jugador eliminado correctamente');
        } else {
            Alert.error('Error al eliminar jugador');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al eliminar jugador');
    }
};

async function renderPlayers(players) {
    const playersList = document.getElementById('playersListContainer');
    if (!playersList) return;
    
    playersList.innerHTML = '';
    
    if (!Array.isArray(players) || players.length === 0) {
        playersList.innerHTML = '<div class="no-data">No hay jugadores en este equipo</div>';
        return;
    }

    players.forEach(player => {
        const playerCard = document.createElement('div');
        playerCard.className = 'player-card card';
        playerCard.innerHTML = `
            <div class="player-card-content card-content">
                <div class="player-info card-info">
                    <div class="detail-group">
                        <span class="detail-label">Nombre:</span>
                        <span class="detail-value">${player.name}</span>
                    </div>
                    <div class="detail-group">
                        <span class="detail-label">Apellido:</span>
                        <span class="detail-value">${player.surname}</span>
                    </div>
                    <div class="detail-group">
                        <span class="detail-label">Email:</span>
                        <span class="detail-value">${player.email}</span>
                    </div>
                </div>
                <div class="player-card-actions card-actions">
                    <button class="btn btn-delete delete-btn" onclick="deletePlayer('${player.email}')">
                        <i class="fas fa-trash" style="color: var(--black);"></i> Eliminar
                    </button>
                </div>
            </div>
        `;
        playersList.appendChild(playerCard);
    });
}

async function initManagePlayers() {
    const teamSelect = document.getElementById('teamName');
    const addPlayerBtn = document.getElementById('addPlayerBtn');
    const playerMailInput = document.getElementById('playerMail');
    
    fetchTeams(false).then(async teams => {
        await renderTeams(teams, document.getElementById('teamName'));
    });

    teamSelect.addEventListener('change', async (e) => {
        currentTeam = e.target.value;
        const teams = await fetchTeams(true);
        const selectedTeam = teams.find(team => team.teamName === currentTeam);
        if (selectedTeam) {
            players = selectedTeam.members || [];
            await renderPlayers(players);
        }
    });

    addPlayerBtn.addEventListener('click', async () => {
        const userMail = playerMailInput.value;
        if (!userMail || !currentTeam) return;

        addPlayerBtn.disabled = true;
        addPlayerBtn.innerHTML = 'Agregando jugador...';

        try {
            const response = await fetchWithAuth(`${TEAMS_API_URL}/${currentTeam}/members`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: userMail
            });

            if (response.ok) {
                const teams = await fetchTeams(true);
                const selectedTeam = teams.find(team => team.teamName === currentTeam);
                if (selectedTeam) {
                    players = selectedTeam.members || [];
                    await renderPlayers(players);
                }
                playerMailInput.value = '';
                Alert.success('Jugador agregado correctamente');
            } else {
                Alert.error('Error al agregar jugador o ya está en el equipo');
            }
        } catch (error) {
            Alert.error(error.message || 'Error al agregar jugador');
        } finally {
            addPlayerBtn.disabled = false;
            addPlayerBtn.innerHTML = 'Agregar Jugador';
        }
    });  
}

window.initManagePlayers = initManagePlayers;
window.deletePlayer = deletePlayer;