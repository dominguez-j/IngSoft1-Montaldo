const zonesCABA = [
  { value: "agronomia", label: "Agronomía" },
  { value: "almagro", label: "Almagro" },
  { value: "balvanera", label: "Balvanera" },
  { value: "barracas", label: "Barracas" },
  { value: "belgrano", label: "Belgrano" },
  { value: "boedo", label: "Boedo" },
  { value: "caballito", label: "Caballito" },
  { value: "chacarita", label: "Chacarita" },
  { value: "coghlan", label: "Coghlan" },
  { value: "colegiales", label: "Colegiales" },
  { value: "constitucion", label: "Constitución" },
  { value: "flores", label: "Flores" },
  { value: "floresta", label: "Floresta" },
  { value: "la_boca", label: "La Boca" },
  { value: "la_paternal", label: "La Paternal" },
  { value: "liniers", label: "Liniers" },
  { value: "mataderos", label: "Mataderos" },
  { value: "monte_castro", label: "Monte Castro" },
  { value: "monserrat", label: "Monserrat" },
  { value: "nueva_pompeya", label: "Nueva Pompeya" },
  { value: "nunez", label: "Núñez" },
  { value: "palermo", label: "Palermo" },
  { value: "parque_avellaneda", label: "Parque Avellaneda" },
  { value: "parque_chacabuco", label: "Parque Chacabuco" },
  { value: "parque_chas", label: "Parque Chas" },
  { value: "parque_patricios", label: "Parque Patricios" },
  { value: "paternal", label: "Paternal" },
  { value: "pompeya", label: "Pompeya" },
  { value: "puerto_madero", label: "Puerto Madero" },
  { value: "recoleta", label: "Recoleta" },
  { value: "retiro", label: "Retiro" },
  { value: "saavedra", label: "Saavedra" },
  { value: "san_cristobal", label: "San Cristóbal" },
  { value: "san_nicolas", label: "San Nicolás" },
  { value: "san_telmo", label: "San Telmo" },
  { value: "velez_sarsfield", label: "Vélez Sarsfield" },
  { value: "versalles", label: "Versalles" },
  { value: "villa_crespo", label: "Villa Crespo" },
  { value: "villa_del_parque", label: "Villa del Parque" },
  { value: "villa_devoto", label: "Villa Devoto" },
  { value: "villa_general_mitre", label: "Villa General Mitre" },
  { value: "villa_lugano", label: "Villa Lugano" },
  { value: "villa_luro", label: "Villa Luro" },
  { value: "villa_ortuzar", label: "Villa Ortúzar" },
  { value: "villa_pueyrredon", label: "Villa Pueyrredón" },
  { value: "villa_real", label: "Villa Real" },
  { value: "villa_riachuelo", label: "Villa Riachuelo" },
  { value: "villa_santa_rita", label: "Villa Santa Rita" },
  { value: "villa_soldati", label: "Villa Soldati" },
  { value: "villa_urquiza", label: "Villa Urquiza" }
];

export function populateZoneSelect(selectId, label) {
  const select = document.getElementById(selectId);
  if (!select) return;
  select.innerHTML = `<option value="">${label}</option>`;
  zonesCABA.forEach(zone => {
    const option = document.createElement('option');
    option.value = zone.value;
    option.textContent = zone.label;
    select.appendChild(option);
  });
}

export { zonesCABA }; 