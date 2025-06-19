import { TEAMS_API_URL } from '../../common/js/constants.js';
import { populateRankSelect } from './ranks.js';
import { validateFields, Alert, fetchWithAuth } from '../../common/js/utils.js';

function attachTeamFormListener() {
    const teamForm = document.getElementById('team-create-form');
    const submitButton = teamForm.querySelector('button[type="submit"]');
    const primaryColorInput = document.getElementById('primaryColor');
    const secondaryColorInput = document.getElementById('secondaryColor');
    const teamNameInput = document.getElementById('teamName');
    const teamRankInput = document.getElementById('teamRank');
    
    populateRankSelect('teamRank');

    function resetForm() {
        teamNameInput.value = '';
        teamRankInput.value = '';
        primaryColorInput.value = '#000000';
        secondaryColorInput.value = '#000000';
    }

    teamForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const isValid = validateFields([
            { input: teamNameInput, message: 'Nombre del equipo es requerido' },
            { input: teamRankInput, message: 'Rango es requerido' }
        ]);
        
        if (!isValid) return;

        const formData = new FormData(teamForm);
        const teamData = {
            teamName: formData.get('teamName'),
            primaryColor: formData.get('primaryColor'),
            subColor: formData.get('secondaryColor'),
            ranking: formData.get('teamRank')
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';
        
        try {
            const response = await fetchWithAuth(TEAMS_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(teamData)
            });
            if (!response.ok) {
                const errorText = await response.text();
                let errorMsg = errorText;
                try {
                    const errorJson = JSON.parse(errorText);
                    if (errorText.includes('duplicate key value') || errorText.includes('already exists')) {
                        errorMsg = 'Ya existe un equipo con ese nombre. Por favor, elija otro nombre.';
                    } else {
                        errorMsg = errorJson.error || errorJson.message || errorText;
                    }
                } catch (e) {
                    errorMsg = 'Error al crear el equipo. Por favor, intenta nuevamente.';
                }
                Alert.error(errorMsg);
            } else {
                Alert.success('Equipo creado correctamente!');
                resetForm();
            }
        } catch (error) {
            Alert.error(error.message || 'Error al crear el equipo');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Crear Equipo';
        }
    });
}

window.attachTeamFormListener = attachTeamFormListener; 