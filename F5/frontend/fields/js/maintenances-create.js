import { BLOCKED_SLOTS_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth, validateFields } from '../../common/js/utils.js';
import { fetchFields, renderFields } from '../js/name-fields.js';
import { fetchFreeSlots, clearSlotsState, confirmedSlot, setConfirmedSlot } from '../js/free-slots.js';

function attachMaintenancesFormListener() {
    const maintenanceCreateForm = document.getElementById('maintenance-create-form');
    const fieldSelect = document.getElementById('fieldName');
    const submitButton = maintenanceCreateForm.querySelector('button[type="submit"]');

    maintenanceCreateForm.reset();
    clearSlotsState();

    fetchFields(true).then(async fields => {
        await renderFields(fields, fieldSelect);
        
        fieldSelect.addEventListener('change', async (e) => {
            const selectedField = e.target.value;
            if (selectedField) {
                clearSlotsState();
                await fetchFreeSlots(selectedField, 7);
            }
        });
    });

    maintenanceCreateForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        if (!confirmedSlot) {
            Alert.error('Por favor, seleccione un horario disponible');
            return;
        }

        const isValid = validateFields([
            { input: document.getElementById('fieldName'), message: 'Nombre es requerido' },
            { input: document.getElementById('reason'), message: 'Motivo es requerido' }
        ]);

        if (!isValid) return;

        const formData = new FormData(maintenanceCreateForm);
        const maintenanceData = {
            fieldName: formData.get('fieldName'),
            date: confirmedSlot.date,
            slotNumber: confirmedSlot.slotNumber,
            reason: formData.get('reason')
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';

        try {
            const response = await fetchWithAuth(BLOCKED_SLOTS_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(maintenanceData),
            });

            if (response.ok) {
                Alert.success('Mantenimiento creado con éxito');
                maintenanceCreateForm.reset();
                clearSlotsState();
                setConfirmedSlot(null);
            } else {
                Alert.error('Error al crear el mantenimiento');
            }
        } catch (error) {
            Alert.error(error.message || 'Error al crear el mantenimiento');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Crear Mantenimiento';
        }
    });
}

window.attachMaintenancesFormListener = attachMaintenancesFormListener;