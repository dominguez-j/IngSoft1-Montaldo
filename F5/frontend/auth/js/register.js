import { REGISTER_API_URL } from '../../common/js/constants.js';
import { populateZoneSelect } from '../../common/js/zones.js';
import { Alert, validateFields } from '../../common/js/utils.js';

const registerForm = document.getElementById('registerForm');
const submitButton = registerForm.querySelector('button[type="submit"]');

populateZoneSelect('zone', "Seleccionar zona");

registerForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const isValid = validateFields([
        { input: document.getElementById('name'), message: 'El nombre es obligatorio' },
        { input: document.getElementById('surname'), message: 'El apellido es obligatorio' },
        { input: document.getElementById('email'), message: 'Correo electrónico inválido' , validator: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)},
        { input: document.getElementById('password'), message: 'La contraseña debe tener al menos 6 caracteres' , validator: v => v.length >= 6},
        { input: document.getElementById('confirmPassword'), message: 'Las contraseñas no coinciden' , validator: v => v === document.getElementById('password').value},
        { input: document.getElementById('age'), message: 'Debe ser mayor de 13 años' , validator: v => v >= 13},
        { input: document.getElementById('gender'), message: 'Seleccione un género' },
        { input: document.getElementById('zone'), message: 'Debe seleccionar un barrio' }
    ]);
    if (!isValid) return;

    const formData = new FormData(registerForm);
    const registerData = {
        email: formData.get('email'),
        password: formData.get('password'),
        age: parseInt(formData.get('age'), 10),
        name: formData.get('name'),
        surname: formData.get('surname'),
        gender: formData.get('gender'),
        zone: formData.get('zone')
    };

    submitButton.disabled = true;
    submitButton.textContent = 'Registrando...';

    try {
        const response = await fetch(REGISTER_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(registerData),
        });

        if (!response.ok) {
            const errorText = await response.text();
            let errorMsg = errorText;
            try {
                const errorJson = JSON.parse(errorText);
                errorMsg = errorJson.error || errorJson.message || errorText;
            } catch (e) {
                Alert.error('Error al registrar. Por favor, intenta nuevamente.');
            }
            Alert.error(errorMsg);
            return;
        }

        window.location.href = '../../auth/html/successfull-register.html';
    } catch (error) {
        Alert.error(error.message || 'Error al registrar. Por favor, intenta nuevamente.');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Registrarse';
    }
});