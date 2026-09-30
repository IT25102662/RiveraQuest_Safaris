// REST API Client for Boat Safari Management System
const API_BASE = '/api';

function getAuthHeaders() {
    const user = (window.AuthState && window.AuthState.currentUser) ? window.AuthState.currentUser : null;
    const headers = { 'Content-Type': 'application/json' };
    if (user) {
        headers['X-User-Role'] = user.role || 'CUSTOMER';
        headers['X-User-Id'] = user.id ? user.id.toString() : '1';
    }
    return headers;
}

const API = {
    // Auth APIs
    register: (userData) => fetch(`${API_BASE}/auth/register`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(userData)
    }).then(res => handleRes(res)),

    login: (credentials) => fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(credentials)
    }).then(res => handleRes(res)),

    // User APIs
    getUsers: () => fetch(`${API_BASE}/users`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    updateUser: (id, user) => fetch(`${API_BASE}/users/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(user)
    }).then(res => handleRes(res)),
    deleteUser: (id) => fetch(`${API_BASE}/users/${id}`, { method: 'DELETE', headers: getAuthHeaders() }),

    // Boat APIs
    getBoats: () => fetch(`${API_BASE}/boats`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getBoat: (id) => fetch(`${API_BASE}/boats/${id}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    createBoat: (boat, fleetManagerId) => fetch(`${API_BASE}/boats${fleetManagerId ? '?fleetManagerId=' + fleetManagerId : ''}`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(boat)
    }).then(res => handleRes(res)),
    updateBoat: (id, boat) => fetch(`${API_BASE}/boats/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(boat)
    }).then(res => handleRes(res)),
    deleteBoat: (id) => fetch(`${API_BASE}/boats/${id}`, { method: 'DELETE', headers: getAuthHeaders() }),

    // Maintenance APIs
    addMaintenance: (boatId, log) => fetch(`${API_BASE}/boats/${boatId}/maintenance`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(log)
    }).then(res => handleRes(res)),
    getMaintenanceLogs: (boatId) => fetch(`${API_BASE}/boats/${boatId}/maintenance`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getAllMaintenanceLogs: () => fetch(`${API_BASE}/boats/maintenance/all`, { headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Route APIs
    getRoutes: () => fetch(`${API_BASE}/routes`, { headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Guide APIs
    getGuides: () => fetch(`${API_BASE}/guides`, { headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Trip APIs
    getTrips: (routeId) => fetch(`${API_BASE}/trips${routeId ? '?routeId=' + routeId : ''}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getTrip: (id) => fetch(`${API_BASE}/trips/${id}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    createTrip: (boatId, routeId, guideId, trip) => {
        const params = new URLSearchParams({ boatId, routeId });
        if (guideId) params.set('guideId', guideId);
        return fetch(`${API_BASE}/trips?${params.toString()}`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(trip)
        }).then(res => handleRes(res));
    },
    updateTrip: (id, boatId, routeId, guideId, trip) => {
        const params = new URLSearchParams({ boatId, routeId });
        if (guideId) params.set('guideId', guideId);
        return fetch(`${API_BASE}/trips/${id}?${params.toString()}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(trip)
        }).then(res => handleRes(res));
    },
    updateTripStatus: (id, status) => fetch(`${API_BASE}/trips/${id}/status?status=${status}`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),
    deleteTrip: (id) => fetch(`${API_BASE}/trips/${id}`, { method: 'DELETE', headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Booking APIs
    getBookings: (userId) => fetch(`${API_BASE}/bookings${userId ? '?userId=' + userId : ''}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getBooking: (id) => fetch(`${API_BASE}/bookings/${id}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    createBooking: (bookingDTO) => fetch(`${API_BASE}/bookings`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(bookingDTO)
    }).then(res => handleRes(res)),
    cancelBooking: (id) => fetch(`${API_BASE}/bookings/${id}/cancel`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),
    cancelBookingWithReason: (id, reason) => fetch(`${API_BASE}/bookings/${id}/cancel?reason=${encodeURIComponent(reason)}`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),
    validateBooking: (id, status) => fetch(`${API_BASE}/bookings/${id}/validate?status=${encodeURIComponent(status)}`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Safety Checklist APIs
    getSafetyChecklist: (tripId) => fetch(`${API_BASE}/safety/trip/${tripId}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getAllSafetyChecklists: () => fetch(`${API_BASE}/safety`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    saveSafetyChecklist: (tripId, checklist) => fetch(`${API_BASE}/safety/trip/${tripId}`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(checklist)
    }).then(res => handleRes(res)),
    approveDeparture: (tripId, inspectorName) => fetch(`${API_BASE}/safety/trip/${tripId}/approve?inspectorName=${encodeURIComponent(inspectorName)}`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Promotion APIs
    getPromotions: () => fetch(`${API_BASE}/promotions`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    searchPromotions: (keyword, status) => {
        const params = new URLSearchParams();
        if (keyword) params.set('keyword', keyword);
        if (status) params.set('status', status);
        return fetch(`${API_BASE}/promotions/search?${params.toString()}`, { headers: getAuthHeaders() }).then(res => handleRes(res));
    },
    getPromotion: (id) => fetch(`${API_BASE}/promotions/${id}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    createPromotion: (promo, marketingOfficerId) => fetch(`${API_BASE}/promotions${marketingOfficerId ? '?marketingOfficerId=' + marketingOfficerId : ''}`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(promo)
    }).then(res => handleRes(res)),
    updatePromotion: (id, promo) => fetch(`${API_BASE}/promotions/${id}`, {
        method: 'PUT',
        headers: getAuthHeaders(),
        body: JSON.stringify(promo)
    }).then(res => handleRes(res)),
    deletePromotion: (id) => fetch(`${API_BASE}/promotions/${id}`, { method: 'DELETE', headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Review APIs
    getReviews: (tripId) => fetch(`${API_BASE}/reviews${tripId ? '?tripId=' + tripId : ''}`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    searchReviews: (keyword, rating, status) => {
        const params = new URLSearchParams();
        if (keyword) params.set('keyword', keyword);
        if (rating) params.set('rating', rating);
        if (status) params.set('status', status);
        return fetch(`${API_BASE}/reviews/search?${params.toString()}`, { headers: getAuthHeaders() }).then(res => handleRes(res));
    },
    getReviewSummary: () => fetch(`${API_BASE}/reviews/summary`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    createReview: (userId, tripId, rating, comment) => fetch(`${API_BASE}/reviews?userId=${userId}&tripId=${tripId}&rating=${rating}&comment=${encodeURIComponent(comment)}`, {
        method: 'POST',
        headers: getAuthHeaders()
    }).then(res => handleRes(res)),
    updateReviewStatus: (id, status) => fetch(`${API_BASE}/reviews/${id}/status?status=${status}`, { method: 'PUT', headers: getAuthHeaders() }).then(res => handleRes(res)),
    deleteReview: (id) => fetch(`${API_BASE}/reviews/${id}`, { method: 'DELETE', headers: getAuthHeaders() }).then(res => handleRes(res)),

    // Report APIs
    getSummaryReport: () => fetch(`${API_BASE}/reports/summary`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getRevenueReport: () => fetch(`${API_BASE}/reports/revenue`, { headers: getAuthHeaders() }).then(res => handleRes(res)),
    getBoatReport: () => fetch(`${API_BASE}/reports/boats`, { headers: getAuthHeaders() }).then(res => handleRes(res))
};

async function handleRes(res) {
    if (!res.ok) {
        const errorData = await res.json().catch(() => ({ message: res.statusText }));
        if (res.status === 403) {
            throw new Error(`⚠️ Access Denied: ${errorData.message || 'You are not authorized for this action.'}`);
        }
        throw new Error(errorData.message || 'API Error');
    }
    if (res.status === 204) return null;
    return res.json();
}