import { RECOVER_PASSWORD_API_URL } from '../../common/js/constants.js';
import { Alert, validateFields } from '../../common/js/utils.js';

const form = document.querySelector('.recover-section');
const emailInput = document.getElementById('email');
const submitButton = form.querySelector('button[type="submit"]');

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    
    const isValid = validateFields([
        { input: emailInput, message: 'Email inválido' , validator: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)}
    ]);

    if (!isValid) return;

    const formData = new FormData(form);
    const emailData = {
        email: formData.get('email')
    };


    submitButton.disabled = true;
    submitButton.textContent = 'Enviando...';
    
    try {
        const response = await fetch(RECOVER_PASSWORD_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                email: emailData.email
            })
        });

        if (!response.ok) {
            const errorText = await response.text();
            Alert.error(errorText || 'Error al procesar la solicitud');
        }

        Alert.success('Se ha enviado un correo con las instrucciones para recuperar tu contraseña.');
        form.reset();
        setTimeout(() => {
            window.location.href = '../../auth/html/login.html';
        }, 5000);
    } catch (error) {
        Alert.error('Error de conexión. Por favor, intenta nuevamente.');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Enviado';
    }
});