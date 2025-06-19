import { FIELDS_API_URL } from '../../common/js/constants.js';
import { populateZoneSelect } from '../../common/js/zones.js';
import { populateGroundSelect } from '../js/grounds.js';
import { validateFields, Alert, fetchWithAuth } from '../../common/js/utils.js';

function attachFieldFormListener() {
    const fieldForm = document.getElementById('field-create-form');
    const submitButton = fieldForm.querySelector('button[type="submit"]');

    populateZoneSelect('fieldZone', "Seleccionar zona");
    populateGroundSelect('fieldType', "Seleccionar tipo de suelo");
    
    fieldForm.addEventListener('submit', async function(event) {
        event.preventDefault();

        const isValid = validateFields([
            { input: document.getElementById('fieldName'), message: 'Nombre es requerido' },
            { input: document.getElementById('fieldZone'), message: 'Zona es requerida' },
            { input: document.getElementById('fieldAddress'), message: 'Dirección es requerida' },
            { input: document.getElementById('fieldType'), message: 'Tipo de cancha es requerido' },
            { input: document.getElementById('fieldRoof'), message: 'Techo es requerido' },
            { input: document.getElementById('fieldLights'), message: 'Iluminación es requerida' }
        ]);

        if (!isValid) return;

        const formData = new FormData(fieldForm);
        const fieldData = {
            name: formData.get('fieldName'),
            groundType: formData.get('fieldType'),
            hasRoof: formData.get('fieldRoof') === 'true',
            hasIllumination: formData.get('fieldLights') === 'true',
            zone: formData.get('fieldZone'),
            address: formData.get('fieldAddress')
        };

        submitButton.disabled = true;
        submitButton.textContent = 'Guardando...';
        
        try {
            const response = await fetchWithAuth(FIELDS_API_URL, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(fieldData),
            });
            if (!response.ok) {
                const errorText = await response.text();
                let errorMsg = errorText;
                try {
                    const errorJson = JSON.parse(errorText);
                    if (errorText.includes('duplicate key value') || errorText.includes('already exists')) {
                        errorMsg = 'Ya existe una cancha con ese nombre. Por favor, elija otro nombre.';
                    } else {
                        errorMsg = errorJson.error || errorJson.message || errorText;
                    }
                } catch (e) {
                    errorMsg = 'Error al crear la cancha. Por favor, intenta nuevamente.';
                }
                Alert.error(errorMsg);
            }
            Alert.success('Cancha creada correctamente!');
            fieldForm.reset();
        } catch (error) {
            Alert.error(error.message || 'Error al crear la cancha');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Crear Cancha';
        }
    });
}

window.attachFieldFormListener = attachFieldFormListener; 