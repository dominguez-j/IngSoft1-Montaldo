import { fetchFields } from '../../fields/js/name-fields.js';

export async function populateFieldNameSelect(selectId, label) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = `<option value="">${label}</option>`;
    const fields = await fetchFields();
    fields.forEach(field => {
        const option = document.createElement('option');
        option.value = field.name;
        option.textContent = field.name;
        select.appendChild(option);
    });
}