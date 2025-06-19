import { VERIFICATION_API_URL } from '../../common/js/constants.js';

const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');

async function verifyEmail(token) {
    if (!token) {
        window.location.href = '../../auth/html/error-verification.html';
        return;
    }

    try {
        const response = await fetch(VERIFICATION_API_URL+'?token='+encodeURIComponent(token), {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            window.location.href = '../../auth/html/error-verification.html';
            return;
        }

        window.location.href = '../../auth/html/successfull-verification.html';
    } catch (error) {
        window.location.href = '../../auth/html/error-verification.html';
    }
}

verifyEmail(token);