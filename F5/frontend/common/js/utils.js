export function clearError(input) {
    const errorDiv = input.nextElementSibling;
    if (errorDiv && errorDiv.classList.contains('error-message')) {
        errorDiv.textContent = '';
    }
    input.classList.remove('error');
}

export function showError(input, message) {
    let errorDiv = input.nextElementSibling;
    if (!errorDiv || !errorDiv.classList.contains('error-message')) {
        errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        input.parentNode.insertBefore(errorDiv, input.nextSibling);
    }
    errorDiv.textContent = message;
    input.classList.add('error');
}

export const Alert = {
    container: null,

    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'alert-container';
            document.body.appendChild(this.container);
        }
    },

    show(message, type = 'info') {
        this.init();

        const alert = document.createElement('div');
        alert.className = `alert alert-${type}`;
        alert.textContent = message;

        this.container.appendChild(alert);

        setTimeout(() => {
            alert.classList.add('fade-out');
            setTimeout(() => {
                this.container.removeChild(alert);
            }, 300);
        }, 3000);
    },

    success(message) {
        this.show(message, 'success');
    },

    error(message) {
        this.show(message, 'error');
    },

    warning(message) {
        this.show(message, 'warning');
    },

    info(message) {
        this.show(message, 'info');
    }
}; 

export function validateFields(fields) {
    let isValid = true;
    fields.forEach(({ input, message, validator }) => {
        const value = input.value.trim();
        const valid = validator ? validator(value) : !!value;
        if (!valid) {
            showError(input, message);
            isValid = false;
        } else {
            clearError(input);
        }
    });
    return isValid;
}

export async function fetchWithAuth(url, options = {}) {
    if (!options.headers) options.headers = {};
    options.headers['Authorization'] = 'Bearer ' + localStorage.getItem('accessToken');
    const response = await fetch(url, options);
    if (response.status === 401 || response.status === 403) {
        alert('Tu sesión ha caducado. Por favor, inicia sesión nuevamente.');
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        window.location.href = '../../auth/html/login.html';
        throw new Error('Sesión caducada');
    }
    return response;
} 

export function updatePagination(currentPage, totalPages) {
    const prevButton = document.getElementById('prevPage');
    const nextButton = document.getElementById('nextPage');
    const pageInfo = document.querySelector('.page-info');

    if (prevButton && nextButton && pageInfo) {
        prevButton.disabled = currentPage === 0;
        nextButton.disabled = currentPage >= totalPages - 1;
        pageInfo.textContent = `Página ${currentPage + 1} de ${totalPages}`;
    }
}