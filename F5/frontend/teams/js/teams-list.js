import { TEAMS_API_URL } from '../../common/js/constants.js';
import { ranks } from './ranks.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

let teamsData = [];

function teamEditTemplate(team) {
    return `
        <div class="team-card-content card-content">
            <div class="team-info card-info">
                <div class="detail-group">
                    <span class="detail-label">Nombre:</span>
                    <input type="text" class="edit-name" value="${team.teamName}" />
                </div>
                <div class="color-input-group">
                    <span class="detail-label">Color primario:</span>
                    <input type="color" class="edit-primaryColor" value="${team.primaryColor}" />
                </div>
                <div class="color-input-group">
                    <span class="detail-label">Color secundario:</span>
                    <input type="color" class="edit-secondaryColor" value="${team.subColor}" />
                </div>
                <div class="detail-group">
                    <span class="detail-label">Rango:</span>
                    <select class="edit-rank">
                        ${ranks.map(r => `<option value="${r.value}" ${team.ranking === r.value ? 'selected' : ''}>${r.label}</option>`).join('')}
                    </select>
                </div>
            </div>
            <div class="team-card-actions card-actions">
                <button class="btn btn-edit save-btn" data-name="${team.teamName}">
                    <i class="fas fa-save" style="color: var(--black);"></i> Guardar
                </button>
                <button class="btn btn-delete cancel-btn" data-name="${team.teamName}">
                    <i class="fas fa-times" style="color: var(--black);"></i> Cancelar
                </button>
            </div>
        </div>
    `;
}

function teamViewTemplate(team) {
    return `
        <div class="team-card-content card-content">
            <div class="team-info card-info">
                <h3 class="team-name" style="color: var(--secondary-color);">${team.teamName}</h3>
                <div class="color-input-group">
                    <span class="detail-label">Color primario:</span>
                    <span class="color-preview" style="background:${team.primaryColor};"></span>
                </div>
                <div class="color-input-group">
                    <span class="detail-label">Color secundario:</span>
                    <span class="color-preview" style="background:${team.subColor};"></span>
                </div>
                <div class="detail-group">
                    <span class="detail-label">Rango:</span>
                    <span class="detail-value">${ranks.find(r => r.value === team.ranking)?.label || team.ranking}</span>
                </div>
            </div>
            <div class="team-card-actions card-actions">
                <button class="btn btn-edit edit-btn" data-name="${team.teamName}">
                    <i class="fas fa-edit" style="color: var(--black);"></i> Editar
                </button>
                <button class="btn btn-delete delete-btn" data-name="${team.teamName}">
                    <i class="fas fa-trash" style="color: var(--black);"></i> Eliminar
                </button>
            </div>
        </div>
    `;
}

async function fetchTeams() {
    try {
        const teamsListContainer = document.getElementById('teamsListContainer');
        if (teamsListContainer) {
            teamsListContainer.innerHTML = '<div class="loading">Cargando equipos...</div>';
        }

        const response = await fetchWithAuth(TEAMS_API_URL);
        if (response.ok) {
            const data = await response.json();
            teamsData = data.content || [];
            await renderTeams(teamsData);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al obtener los equipos.');
    }
}

async function renderTeams(teams) {
    const teamsContainer = document.getElementById('teamsListContainer');
    teamsContainer.innerHTML = '';

    if (teams.length === 0) {
        teamsContainer.innerHTML = '<div class="no-data">No hay equipos registrados</div>';
        return;
    }

    teams.forEach(team => {
        const teamCard = document.createElement('div');
        teamCard.className = 'team-card card';
        teamCard.innerHTML = team.isEditing ? teamEditTemplate(team) : teamViewTemplate(team);
        teamsContainer.appendChild(teamCard);
    });

    teamsContainer.querySelectorAll('.edit-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            teamsData = teamsData.map(t => t.teamName === name ? { ...t, isEditing: true } : { ...t, isEditing: false });
            await renderTeams(teamsData);
        });
    });

    teamsContainer.querySelectorAll('.save-btn').forEach(async btn => {
        btn.addEventListener('click', async () => {
            const oldName = btn.getAttribute('data-name');
            const card = btn.closest('.team-card');
            const nameInput = card.querySelector('.edit-name');
            const primaryColorInput = card.querySelector('.edit-primaryColor');
            const secondaryColorInput = card.querySelector('.edit-secondaryColor');
            const rankSelect = card.querySelector('.edit-rank');

            const updatedTeam = {
                teamName: nameInput.value,
                primaryColor: primaryColorInput.value,
                subColor: secondaryColorInput.value,
                ranking: rankSelect.value
            };

            try {
                const response = await fetchWithAuth(`${TEAMS_API_URL}/${encodeURIComponent(oldName)}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(updatedTeam)
                });
                if (response.ok) {
                    teamsData = teamsData.map(t => t.teamName === oldName ? { ...updatedTeam, isEditing: false } : t);
                    await renderTeams(teamsData);
                    Alert.success('¡Equipo actualizado con éxito!');
                } else {
                    Alert.error('No se pudo actualizar el equipo.');
                }
            } catch (error) {
                Alert.error(error.message || 'Error al actualizar el equipo');
            }
        });
    });

    teamsContainer.querySelectorAll('.cancel-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            teamsData = teamsData.map(t => t.teamName === name ? { ...t, isEditing: false } : t);
            await renderTeams(teamsData);
        });
    });

    teamsContainer.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', async () => {
            const name = btn.getAttribute('data-name');
            if (confirm('¿Seguro que deseas eliminar este equipo?')) {
                await deleteTeam(name);
            }
        });
    });

    setTimeout(() => {
        document.querySelectorAll('.edit-primaryColor').forEach(input => {
            input.addEventListener('input', function() {
                const preview = input.parentNode.querySelector('.edit-primary-preview');
                if (preview) preview.style.background = input.value;
            });
        });
        document.querySelectorAll('.edit-secondaryColor').forEach(input => {
            input.addEventListener('input', function() {
                const preview = input.parentNode.querySelector('.edit-secondary-preview');
                if (preview) preview.style.background = input.value;
            });
        });
    }, 0);
}

async function deleteTeam(teamName) {
    try {
        const response = await fetchWithAuth(`${TEAMS_API_URL}/${encodeURIComponent(teamName)}`, {
            method: 'DELETE'
        });
        if (response.ok) {
            teamsData = teamsData.filter(t => t.teamName !== teamName);
            await renderTeams(teamsData);
            Alert.success('¡Equipo eliminado con éxito!');
        } else {
            Alert.error('No se pudo eliminar el equipo.');
        }
    } catch (error) {
        Alert.error(error.message || 'Error al eliminar el equipo');
    }
}

function initTeamsList() {
    fetchTeams();
    const searchInput = document.getElementById('searchTeamInput');
    if (searchInput) {
        searchInput.addEventListener('input', async (e) => {
            const value = e.target.value.toLowerCase();
            const filtered = teamsData.filter(t => t.teamName.toLowerCase().includes(value));
            await renderTeams(filtered);
        });
    }
};

window.initTeamsList = initTeamsList;