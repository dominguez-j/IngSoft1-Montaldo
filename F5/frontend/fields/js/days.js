const days = [
    { value: "MONDAY", label: "Lunes" },
    { value: "TUESDAY", label: "Martes" },
    { value: "WEDNESDAY", label: "Miercoles" },
    { value: "THURSDAY", label: "Jueves" },
    { value: "FRIDAY", label: "Viernes" },
    { value: "SATURDAY", label: "Sabado" },
    { value: "SUNDAY", label: "Domingo" }
];

export function populateDaySelect(selectId) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = '<option value="">Seleccionar día</option>';
    days.forEach(day => {
        const option = document.createElement('option');
        option.value = day.value;
        option.textContent = day.label;
        select.appendChild(option);
    });
}

export { days }; 