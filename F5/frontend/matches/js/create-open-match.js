import { BLOCKED_SLOTS_API_URL, OPEN_MATCHES_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, validateFields } from '../../common/js/utils.js';
import { fetchFields, renderFields } from '../../fields/js/name-fields.js';
import { fetchFreeSlots, clearSlotsState, confirmedSlot, setConfirmedSlot } from '../../fields/js/free-slots.js';

let blockedSlotData = null;

function attachOpenMatchFormListener() {
    const openMatchForm = document.getElementById('open-match-form');
    const fieldSelect = document.getElementById('fieldName');
    const submitButton = openMatchForm.querySelector('button[type="submit"]');

    openMatchForm.reset();
    clearSlotsState();

    fetchFields(false).then(async fields => {
        await renderFields(fields, fieldSelect);
        
        fieldSelect.addEventListener('change', async (e) => {
            const selectedField = e.target.value;
            if (selectedField) {
                clearSlotsState();
                await fetchFreeSlots(selectedField, 7);
            }
        });

        document.addEventListener('click', async (e) => {
            if (e.target.classList.contains('btn-confirm')) {
                e.preventDefault();
                await reserveSlot(fieldSelect.value, confirmedSlot);
            }
        });
    });

    openMatchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!confirmedSlot) {
            Alert.error('Por favor, seleccione un horario disponible');
            return;
        }

        const isValid = validateFields([
            { input: document.getElementById('fieldName'), message: 'Nombre es requerido' },
            { input: document.getElementById('minPlayers'), message: 'Cantidad mínima de jugadores es requerida' },
            { input: document.getElementById('maxPlayers'), message: 'Cantidad máxima de jugadores es requerida' }
        ]);

        if (!isValid) return;

        const formData = new FormData(openMatchForm);
        const openMatchData = {
            minPlayers: parseInt(formData.get('minPlayers')),
            maxPlayers: parseInt(formData.get('maxPlayers')),
            fieldName: formData.get('fieldName'),
            blockedSlotId: blockedSlotData.id
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';

        try {
            const response = await fetchWithAuth(OPEN_MATCHES_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(openMatchData)
            });

            if (response.ok) {
                Alert.success('Partido creado con éxito');
                openMatchForm.reset();
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
    const openMatchData = {
        fieldName: fieldName,
        date: slot.date,
        slotNumber: slot.slotNumber,
        reason: 'Partido abierto'
    };

    try {
        const response = await fetchWithAuth(BLOCKED_SLOTS_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(openMatchData)
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

window.attachOpenMatchFormListener = attachOpenMatchFormListener;
window.reserveSlot = reserveSlot;