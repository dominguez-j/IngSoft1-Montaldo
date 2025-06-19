import { LOGIN_API_URL } from '../../common/js/constants.js';
import { Alert, validateFields } from '../../common/js/utils.js';

const loginForm = document.getElementById('loginForm');
const submitButton = loginForm.querySelector('button[type="submit"]');

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const isValid = validateFields([
        { input: document.getElementById('email'), message: 'Email inválido' , validator: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)},
        { input: document.getElementById('password'), message: 'Contraseña inválida' , validator: v => v.length >= 6}
    ]);
    
    if (!isValid) return;

    const formData = new FormData(loginForm);
    const loginData = {
        email: formData.get('email'),
        password: formData.get('password')
    };

    submitButton.disabled = true;
    submitButton.textContent = 'Iniciando sesión...';
    try {
        const response = await fetch(LOGIN_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(loginData)
        });

        if (!response.ok) {
            Alert.error('Credenciales inválidas o no verificaste el correo electrónico');
        }

        const data = await response.json();
        
        localStorage.setItem('accessToken', data.accessToken);
        if (data.refreshToken) {
            localStorage.setItem('refreshToken', data.refreshToken);
        }

        window.location.href = '../../main/html/dashboard.html';
    } catch (error) {
        Alert.error('Error al iniciar sesión');
    } finally {
        submitButton.disabled = false;
        submitButton.textContent = 'Iniciar sesión';
    }
});