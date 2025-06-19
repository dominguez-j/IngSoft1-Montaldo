import { zonesCABA } from '../../common/js/zones.js';
import { grounds } from '../js/grounds.js';
import { days } from '../js/days.js';
import { Alert, fetchWithAuth } from '../../common/js/utils.js';
import { COMMENTS_API_URL, SCHEDULES_API_URL } from '../../common/js/constants.js';

const formatTime = (timeStr) => {
    return timeStr.substring(0, 5);
};

const SchedulesModule = {
    async fetch() {
        try {
            const response = await fetchWithAuth(SCHEDULES_API_URL + '/'+ window.currentFieldName);
            if (response.ok) {
                const data = await response.json();
                return data || [];
            }
        } catch (error) {
            Alert.error(error.message || 'Error al obtener los horarios.');
            return [];
        }
    },

    async render(schedules, container) {
        if (!schedules || schedules.length === 0) {
            container.innerHTML = '<p class="no-schedules">No hay horarios disponibles para esta cancha.</p>';
            return;
        }
        container.innerHTML = '';
        schedules.forEach(schedule => {
            const scheduleItem = document.createElement('div');
            scheduleItem.classList.add('schedule');
            const dayOfWeek = days.find(day => day.value === schedule.dayOfWeek)?.label || schedule.dayOfWeek;
            scheduleItem.innerHTML = `
                <div class="schedule-item">
                    <div class="field-detail-item"> 
                        <span class="detail-value">${dayOfWeek}</span>
                    </div>
                    <div class="field-detail-item">
                        <span class="detail-label">Horario:</span>
                        <span class="detail-value">${formatTime(schedule.openingTime)} - ${formatTime(schedule.closingTime)}</span>
                    </div>
                    <div class="field-detail-item">
                        <span class="detail-label">Turnos de:</span>
                        <span class="detail-value">${schedule.slotDurationMinutes} minutos</span>
                    </div>
                </div>
            `;
            container.appendChild(scheduleItem);
        });
    }
};

const CommentsModule = {
    async fetch() {
        try {
            const response = await fetchWithAuth(COMMENTS_API_URL + '/'+ window.currentField.name);
            if (response.ok) {
                const data = await response.json();
                return data.content || [];
            }
        } catch (error) {
            console.error(error);
            Alert.error(error.message || 'Error al obtener los comentarios.');
        }
    },

    async render(comments, container) {
        if (!comments) return;
        container.innerHTML = '';
        comments.forEach(comment => {
            const commentItem = document.createElement('div');
            commentItem.classList.add('comment');
            commentItem.innerHTML = `
                <span class="comment-author">${escapeHTML(comment.userName)}</span>
                <span class="comment-date">${formatCommentDate(comment.date)}</span>
                <div class="comment-content">${escapeHTML(comment.content)}</div>
                <div class="comment-valoration">${renderStars(comment.valoration)}</div>
            `;
            container.appendChild(commentItem);
        });
    },

    setupStarRating() {
        const stars = document.querySelectorAll('#star-rating span');
        const valInput = document.getElementById('comment-valoration');
        let current = parseInt(valInput.value) || 0;

        function highlight(rating) {
            stars.forEach(star => {
                star.classList.toggle('selected', parseInt(star.dataset.value) <= rating && rating > 0);
            });
        }

        stars.forEach(star => {
            star.addEventListener('mouseenter', () => highlight(parseInt(star.dataset.value)));
            star.addEventListener('mouseleave', () => highlight(current));
            star.addEventListener('click', () => {
                current = parseInt(star.dataset.value);
                valInput.value = current;
                highlight(current);
            });
        });

        highlight(current);
    },

    async handleCommentSubmit(event) {
        event.preventDefault();
        const commentText = document.getElementById('comment-text').value;
        const commentValoration = document.getElementById('comment-valoration').value;
        const comment = {
            content: commentText,
            valoration: commentValoration,
            fieldName: window.currentField.name
        };

        const submitButton = event.target.querySelector('button[type="submit"]');
        submitButton.disabled = true;
        submitButton.textContent = 'Enviando...';

        try {
            const response = await fetchWithAuth(COMMENTS_API_URL, {       
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(comment)
            });
            if (response.ok) {
                Alert.success('Comentario enviado correctamente');
                const comments = await CommentsModule.fetch();
                const commentsContainer = document.getElementById('comments-container');
                await CommentsModule.render(comments, commentsContainer);
                event.target.reset();
                document.getElementById('comment-valoration').value = '0';
                CommentsModule.setupStarRating();
            }
        } catch (error) {
            Alert.error(error.message || 'Error al enviar el comentario. Por favor, intente nuevamente.');
        } finally {
            submitButton.disabled = false;
            submitButton.textContent = 'Enviar Comentario';
        }
    }
};

function escapeHTML(str) {
    return String(str).replace(/[&<>'"]/g, tag => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', "'": '&#39;', '"': '&quot;'
    }[tag]));
}

function formatCommentDate(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleDateString('es-AR', { year: 'numeric', month: 'short', day: 'numeric' });
}

function renderStars(valoration) {
    const fullStar = '★';
    const emptyStar = '☆';
    let stars = '';
    for (let i = 1; i <= 5; i++) {
        stars += i <= valoration ? fullStar : emptyStar;
    }
    return `<span style="color:#6be89c;font-size:1.1em;">${stars}</span>`;
}

export async function initFieldDetails() {
    const fieldName = window.currentFieldName;
    if (!fieldName) return;

    try {
        const field = window.currentField;
        if (!field) {
            Alert.error('No se encontraron los datos de la cancha');
            return;
        }

        renderFieldDetails(field);
        await loadSchedules();
        await loadComments();
        setupCommentForm();
    } catch (error) {
        Alert.error(error.message || 'Error al cargar los detalles de la cancha');
        renderError();
    }
}

function renderFieldDetails(field) {
    const titleElement = document.getElementById('field-details-title');
    if (titleElement) {
        titleElement.textContent = `Detalles de ${field.name}`;
    }

    const detailsContainer = document.getElementById('field-details-container');
    if (detailsContainer) {
        const zoneLabel = (zonesCABA.find(b => b.value === field.zone)) 
            ? zonesCABA.find(b => b.value === field.zone).label 
            : field.zone;
        const groundTypeLabel = (grounds.find(g => g.value === field.groundType)) 
            ? grounds.find(g => g.value === field.groundType).label 
            : field.groundType;
        detailsContainer.innerHTML = `
            <div class="field-detail-item">
                <span class="detail-label">Nombre:</span>
                <span class="detail-value">${field.name}</span>
            </div>
            <div class="field-detail-item">
                <span class="detail-label">Tipo:</span>
                <span class="detail-value">${groundTypeLabel}</span>
            </div>
            <div class="field-detail-item">
                <span class="detail-label">Techo:</span>
                <span class="detail-value">${field.hasRoof ? 'Sí' : 'No'}</span>
            </div>
            <div class="field-detail-item">
                <span class="detail-label">Iluminación:</span>
                <span class="detail-value">${field.hasIllumination ? 'Sí' : 'No'}</span>
            </div>
            <div class="field-detail-item">
                <span class="detail-label">Zona:</span>
                <span class="detail-value">${zoneLabel}</span>
            </div>
            <div class="field-detail-item">
                <span class="detail-label">Dirección:</span>
                <span class="detail-value">${field.address}</span>
            </div>
        `;
    }
}

async function loadSchedules() {
    const schedulesContainer = document.getElementById('schedules-container');
    const schedules = await SchedulesModule.fetch();
    await SchedulesModule.render(schedules, schedulesContainer);
}

async function loadComments() {
    const commentsContainer = document.getElementById('comments-container');
    const comments = await CommentsModule.fetch();
    await CommentsModule.render(comments, commentsContainer);
}

function setupCommentForm() {
    const commentForm = document.getElementById('comment-form');
    if (commentForm) {
        commentForm.addEventListener('submit', CommentsModule.handleCommentSubmit);
    }
    if (document.getElementById('star-rating')) {
        CommentsModule.setupStarRating();
    }
}

function renderError() {
    const detailsContainer = document.getElementById('field-details-container');
    if (detailsContainer) {
        detailsContainer.innerHTML = `
            <div class="error-message">
                Error al cargar los detalles de la cancha. Por favor, intente nuevamente.
            </div>
        `;
    }
}

window.initFieldDetails = initFieldDetails; 