import { SCHEDULES_API_URL } from '../../common/js/constants.js';
import { populateDaySelect } from '../js/days.js';
import { fetchFields, renderFields } from '../js/name-fields.js';
import { Alert, validateFields, fetchWithAuth } from '../../common/js/utils.js';

function attachSchedulerFormListener() {
    const schedulerForm = document.getElementById('scheduler-create-form');
    const submitButton = schedulerForm.querySelector('button[type="submit"]');
    
    populateDaySelect('day');
    fetchFields().then(async fields => {
        await renderFields(fields, document.getElementById('fieldName'));
    });

    schedulerForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const isValid = validateFields([
            { input: document.getElementById('fieldName'), message: 'Nombre de la cancha es requerido' },
            { input: document.getElementById('day'), message: 'Día de la semana es requerido' },
            { input: document.getElementById('hourStart'), message: 'Hora de inicio es requerida' },
            { input: document.getElementById('hourEnd'), message: 'Hora de fin es requerida' },
            { input: document.getElementById('slotDurationMinutes'), message: 'Duración del slot es requerida' }
        ]);

        if (!isValid) return;

        const formData = new FormData(schedulerForm);
        const schedulerData = {
            fieldName: formData.get('fieldName'),
            dayOfWeek: formData.get('day'),
            openingTime: formData.get('hourStart'),
            closingTime: formData.get('hourEnd'),
            slotDurationMinutes: formData.get('slotDurationMinutes')
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';

        try {
            const response = await fetchWithAuth(SCHEDULES_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(schedulerData),
            });
            if (!response.ok) {
                let errorText = '';
                try {
                    errorText = await response.text();
                    if (errorText) {
                        const errorJson = JSON.parse(errorText);
                        Alert.error(errorJson.error || errorJson.message || errorText);
                    }
                } catch (e) {
                    Alert.error(errorText || 'Error creando el horario');
                }
            }
            Alert.success('Horario creado correctamente!');
            schedulerForm.reset();
        } catch (error) {
            Alert.error(error.message || 'Error creando el horario o el horario ya existe. Por favor, intente nuevamente.');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Crear Horario';
        }
    });
}

window.attachSchedulerFormListener = attachSchedulerFormListener; 