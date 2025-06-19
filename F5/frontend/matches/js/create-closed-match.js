import { BLOCKED_SLOTS_API_URL, CLOSED_MATCHES_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, validateFields } from '../../common/js/utils.js';
import { fetchFields, renderFields } from '../../fields/js/name-fields.js';
import { fetchTeams, renderTeams } from '../../teams/js/name-teams.js';
import { fetchFreeSlots, clearSlotsState, confirmedSlot, setConfirmedSlot } from '../../fields/js/free-slots.js';

let blockedSlotData = null;

function attachClosedMatchFormListener() {
    const closedMatchForm = document.getElementById('closed-match-form');
    const fieldSelect = document.getElementById('fieldName');
    const submitButton = closedMatchForm.querySelector('button[type="submit"]');

    closedMatchForm.reset();
    clearSlotsState();

    fetchFields(false).then(async fields => {
        await renderFields(fields, fieldSelect);
        
        fetchTeams().then(async teams => {
            const teamASelect = document.getElementById('teamAName');
            const teamBSelect = document.getElementById('teamBName');
            await renderTeams(teams, teamASelect);
            await renderTeams(teams, teamBSelect);
        });

        fieldSelect.addEventListener('change', async (e) => {
            const selectedField = e.target.value;
            if (selectedField) {
                clearSlotsState();
                await fetchFreeSlots(selectedField, 7);
            }
        });

        const teamASelect = document.getElementById('teamAName');
        const teamBSelect = document.getElementById('teamBName');

        teamASelect.addEventListener('change', () => {
            const selectedTeamA = teamASelect.value;
            const options = teamBSelect.options;
            
            for (let i = 0; i < options.length; i++) {
                if (options[i].value === selectedTeamA) {
                    options[i].disabled = true;
                } else {
                    options[i].disabled = false;
                }
            }
            
            if (teamBSelect.value === selectedTeamA) {
                teamBSelect.value = '';
            }
        });

        teamBSelect.addEventListener('change', () => {
            const selectedTeamB = teamBSelect.value;
            const options = teamASelect.options;
            
            for (let i = 0; i < options.length; i++) {
                if (options[i].value === selectedTeamB) {
                    options[i].disabled = true;
                } else {
                    options[i].disabled = false;
                }
            }
            
            if (teamASelect.value === selectedTeamB) {
                teamASelect.value = '';
            }
        });

        document.addEventListener('click', async (e) => {
            if (e.target.classList.contains('btn-confirm')) {
                e.preventDefault();
                await reserveSlot(fieldSelect.value, confirmedSlot);
            }
        });
    });

    closedMatchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!confirmedSlot) {
            Alert.error('Por favor, seleccione un horario disponible');
            return;
        }

        const isValid = validateFields([
            { input: document.getElementById('fieldName'), message: 'Nombre es requerido' },
            { input: document.getElementById('teamAName'), message: 'Equipo 1 es requerido' },
            { input: document.getElementById('teamBName'), message: 'Equipo 2 es requerido' }
        ]);

        if (!isValid) return;

        const formData = new FormData(closedMatchForm);
        const closedMatchData = {
            fieldName: formData.get('fieldName'),
            teamAName: formData.get('teamAName'),
            teamBName: formData.get('teamBName'),
            blockedSlotId: blockedSlotData.id
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';

        try {
            const response = await fetchWithAuth(CLOSED_MATCHES_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(closedMatchData)
            });

            if (response.ok) {
                Alert.success('Partido creado con éxito');
                closedMatchForm.reset();
                clearSlotsState();
                blockedSlotData = null;
            } else {
                const errorText = await response.text();
                Alert.error('Error al crear el partido');
            }
        } catch (error) {
            Alert.error(error.message || 'Error al crear el partido');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Crear Partido';
        }
    });
}

async function reserveSlot(fieldName, slot) {
    const closedMatchData = {
        fieldName: fieldName,
        date: slot.date,
        slotNumber: slot.slotNumber,
        reason: 'Partido cerrado'
    };

    try {
        const response = await fetchWithAuth(BLOCKED_SLOTS_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(closedMatchData)
        });

        if (response.ok) {
            setConfirmedSlot(slot);
            blockedSlotData = await response.json();
        } else {
            const errorText = await response.text();
            let errorMsg = errorText;
            try {
                const errorJson = JSON.parse(errorText);
                errorMsg = errorJson.error || errorJson.message || errorText;
            } catch (e) {
                errorMsg = 'Error al reservar el horario';
            }
            Alert.error(errorMsg);
        }
    } catch (error) {
        Alert.error(error.message || 'Error al reservar el horario');
    }
}

window.attachClosedMatchFormListener = attachClosedMatchFormListener;
window.reserveSlot = reserveSlot;