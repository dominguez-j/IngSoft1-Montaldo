import { FIELDS_API_URL } from '../../common/js/constants.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';

export async function fetchFields(owned = true) {
    try {
        const fieldsListContainer = document.getElementById('fieldsListContainer');
        if (fieldsListContainer) {
            fieldsListContainer.innerHTML = '<div class="loading">Cargando canchas...</div>';
        }

        const params = new URLSearchParams({
            owned: owned
        });

        const response = await fetchWithAuth(`${FIELDS_API_URL}?${params.toString()}`);
        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }
        const data = await response.json();
        return data.content || [];
    } catch (error) {
        Alert.error(error.message || 'Error al obtener las canchas.');
        return [];
    }
}

export async function renderFields(fields, selectElement) {
    if (!selectElement) return;
    selectElement.innerHTML = '<option value="">Seleccionar nombre de la cancha</option>';
    
    if (!Array.isArray(fields)) {
        return;
    }

    for (const field of fields) {
        const option = document.createElement('option');
        option.value = field.name;
        option.textContent = field.name;
        selectElement.appendChild(option);
    }
}