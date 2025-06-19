const grounds = [
    { value: "SYNTHETIC_GRASS", label: "Césped sintético" },
    { value: "NATURAL_GRASS", label: "Césped natural" },
    { value: "CONCRETE", label: "Concreto" },
    { value: "SAND", label: "Arena" }
];

export function populateGroundSelect(selectId, label) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = `<option value="">${label}</option>`;
    grounds.forEach(groundType => {
        const option = document.createElement('option');
        option.value = groundType.value;
        option.textContent = groundType.label;
        select.appendChild(option);
    });
}

export { grounds }; 