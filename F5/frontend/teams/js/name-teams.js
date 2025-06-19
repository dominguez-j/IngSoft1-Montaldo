import { TEAMS_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

export async function fetchTeams(includeMembers = false) {
    try {
        const teamsListContainer = document.getElementById('teamsListContainer');
        if (teamsListContainer) {
            teamsListContainer.innerHTML = '<div class="loading">Cargando equipos...</div>';
        }

        const params = new URLSearchParams({
            includeMembers: includeMembers
        });

        const response = await fetchWithAuth(`${TEAMS_API_URL}?${params.toString()}`);
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        const data = await response.json();
        return data.content || [];
    } catch (error) {
        Alert.error(error.message || 'Error al obtener los equipos.');
        return [];
    }
}

export async function renderTeams(teams, selectElement) {
    if (!selectElement) return;
    selectElement.innerHTML = '<option value="">Seleccionar nombre del equipo</option>';
    
    if (!Array.isArray(teams)) {
        return;
    }

    for (const team of teams) {
        const option = document.createElement('option');
        option.value = team.teamName;
        option.textContent = team.teamName;
        selectElement.appendChild(option);
    }
}