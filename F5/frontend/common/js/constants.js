export const API_URL = 'http://localhost:8080';

/* ================= DASHBOARD ================= */
export const FIELDS_API_URL = `${API_URL}/fields`;
export const TEAMS_API_URL = `${API_URL}/teams`; 
export const SCHEDULES_API_URL = `${FIELDS_API_URL}/schedules`;
export const PROFILE_API_URL = `${API_URL}/users/profile`;
export const COMMENTS_API_URL = `${API_URL}/comments`;
export const BLOCKED_SLOTS_API_URL = `${FIELDS_API_URL}/blocked-slots`;
export const OPEN_MATCHES_API_URL = `${API_URL}/open-matches`;
export const CLOSED_MATCHES_API_URL = `${API_URL}/closed-matches`;

/* ================= AUTHENTICATION ================= */
export const VERIFICATION_API_URL = `${API_URL}/verifications`;
export const RECOVER_PASSWORD_API_URL = `${VERIFICATION_API_URL}/recover-password`;
export const NEW_PASSWORD_API_URL = `${VERIFICATION_API_URL}/reset-password`;
export const REGISTER_API_URL = `${API_URL}/users`; 
export const LOGIN_API_URL = `${API_URL}/sessions`;