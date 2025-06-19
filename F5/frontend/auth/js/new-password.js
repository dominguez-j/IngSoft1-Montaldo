import { NEW_PASSWORD_API_URL } from '../../common/js/constants.js';
import { Alert, validateFields } from '../../common/js/utils.js';

const form = document.querySelector('.new-password-form');
const submitButton = form.querySelector('button[type="submit"]');

form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const isValid = validateFields([
        { input: document.getElementById('password'), message: 'La contraseña debe tener al menos 6 caracteres' , validator: v => v.length >= 6},
        { input: document.getElementById('confirm_password'), message: 'Las contraseñas no coinciden' , validator: v => v === document.getElementById('password').value},
    ]);

    if (!isValid) return;

    const formData = new FormData(form);
    const passwordData = {
        token: new URLSearchParams(window.location.search).get('token') || '',
        newPassword: formData.get('password')
    };

    submitButton.disabled = true;
    submitButton.textContent = 'Guardando...';

    try {
        const response = await fetch(NEW_PASSWORD_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(passwordData)
        });

        if (!response.ok) {
            const errorText = await response.text();
            Alert.error(errorText || 'Error al actualizar la contraseña');
        }

        Alert.success('Contraseña actualizada exitosamente');
        form.reset();
        setTimeout(() => {
            window.location.href = '../../auth/html/login.html';
        }, 5000);
    } catch (error) {
        Alert.error('Error de conexión. Por favor, intenta nuevamente.');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Guardada';
    }
});