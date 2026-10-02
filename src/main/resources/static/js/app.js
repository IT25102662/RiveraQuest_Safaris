//Application Controller & View Router for Boat Safari Management System 

const VIEW_PERMISSIONS = {
    'home-view': ['ALL'],
    'package-listing-view': ['ALL'],
    'trip-schedule-view': ['ALL'],
    'login-view': ['ALL'],
    'register-view': ['ALL'],

    // Tourist Workspace
    'tourist-dashboard-view': ['CUSTOMER', 'ADMIN'],
    'trip-booking-view': ['CUSTOMER', 'DESK_OFFICER', 'ADMIN'],
    'booking-details-view': ['CUSTOMER', 'DESK_OFFICER', 'ADMIN'],
    'booking-history-view': ['CUSTOMER', 'DESK_OFFICER', 'ADMIN'],

    // Desk Officer Workspace
    'desk-dashboard-view': ['DESK_OFFICER', 'ADMIN'],

    // Fleet Manager Workspace
    'fleet-dashboard-view': ['FLEET_MANAGER', 'ADMIN'],
    'boat-management-view': ['FLEET_MANAGER', 'ADMIN'],
    'boat-details-view': ['FLEET_MANAGER', 'ADMIN'],
    'maintenance-management-view': ['FLEET_MANAGER', 'ADMIN'],

    // Safety Officer Workspace
    'safety-dashboard-view': ['SAFETY_OFFICER', 'ADMIN'],
    'safety-records-view': ['SAFETY_OFFICER', 'ADMIN'],
    'safety-checklist-view': ['SAFETY_OFFICER', 'DESK_OFFICER', 'ADMIN'],

    // Marketing Officer Workspace
    'marketing-dashboard-view': ['MARKETING_OFFICER', 'ADMIN'],
    'promotions-view': ['MARKETING_OFFICER', 'ADMIN'],
    'reviews-view': ['MARKETING_OFFICER', 'CUSTOMER', 'ADMIN'],

    // System Admin Workspace
    'admin-dashboard-view': ['ADMIN'],
    'reports-view': ['ADMIN'],
    'user-management-view': ['ADMIN']
};

const App = {
    currentView: 'home-view',
    selectedSeats: [],

    init() {
        AuthState.init();
        this.bindEvents();

        // Initial view navigation based on logged in role
        const role = AuthState.currentUser ? AuthState.currentUser.role : 'CUSTOMER';
        const initialDashboard = AuthState.getDashboardForRole(role);
        this.navigate(initialDashboard);
    },

    bindEvents() {
        document.querySelectorAll('[data-view]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const view = btn.getAttribute('data-view');
                this.navigate(view);
            });
        });
    },

    navigate(viewId, params = {}) {
        const userRole = AuthState.currentUser ? AuthState.currentUser.role : 'CUSTOMER';
        const allowedRoles = VIEW_PERMISSIONS[viewId] || ['ALL'];

        // Strict Role Guard - Check if current role is authorized
        if (!allowedRoles.includes('ALL') && !allowedRoles.includes(userRole)) {
            this.showAccessDeniedModal(viewId, userRole);
            return;
        }

        document.querySelectorAll('.view-page').forEach(el => el.classList.add('hidden'));
        const targetView = document.getElementById(viewId);
        if (targetView) {
            targetView.classList.remove('hidden');
            this.currentView = viewId;
            this.updateActiveNavState(viewId);
            window.scrollTo({ top: 0, behavior: 'smooth' });
            this.loadViewData(viewId, params);
        }
    },

    updateActiveNavState(viewId) {
        document.querySelectorAll('[data-view]').forEach(btn => {
            const btnView = btn.getAttribute('data-view');
            if (btnView === viewId) {
                btn.classList.add('bg-cyan-600', 'text-white', 'font-bold', 'shadow-md');
                btn.classList.remove('text-slate-300', 'hover:bg-slate-800');
            } else {
                btn.classList.remove('bg-cyan-600', 'text-white', 'font-bold', 'shadow-md');
                btn.classList.add('text-slate-300');
            }
        });
    },

    showAccessDeniedModal(attemptedView, currentRole) {
        const modal = document.getElementById('access-denied-modal');
        const msgEl = document.getElementById('access-denied-message');
        if (msgEl) {
            msgEl.textContent = `Role '${currentRole.replace('_', ' ')}' is not authorized to access page '${attemptedView}'.`;
        }
        if (modal) {
            modal.classList.remove('hidden');
        } else {
            showToast(`Access Denied: Role '${currentRole}' cannot access ${attemptedView}`, 'error');
        }

        // Auto redirect back to user's assigned role dashboard after 2 seconds
        setTimeout(() => {
            if (modal) modal.classList.add('hidden');
            const fallbackDashboard = AuthState.getDashboardForRole(currentRole);
            this.navigate(fallbackDashboard);
        }, 2000);
    },

    async loadViewData(viewId, params) {
        try {
            switch(viewId) {
                case 'home-view':
                    await this.renderHomePage();
                    break;
                case 'tourist-dashboard-view':
                    await this.renderCustomerDashboard();
                    break;
                case 'desk-dashboard-view':
                    await this.renderDeskDashboard();
                    break;
                case 'fleet-dashboard-view':
                    await this.renderFleetDashboard();
                    break;
                case 'safety-dashboard-view':
                    await this.renderSafetyDashboard();
                    break;
                case 'safety-records-view':
                    await this.renderSafetyRecords();
                    break;
                case 'marketing-dashboard-view':
                    await this.renderMarketingDashboard();
                    break;
                case 'admin-dashboard-view':
                    await this.renderAdminDashboard();
                    break;
                case 'package-listing-view':
                    await this.renderTripListing(params);
                    break;
                case 'boat-management-view':
                    await this.renderBoatManagement();
                    break;
                case 'boat-details-view':
                    await this.renderBoatDetails(params.boatId || 1);
                    break;
                case 'trip-schedule-view':
                    await this.renderTripSchedules();
                    break;
                case 'trip-booking-view':
                    await this.renderTripBooking(params.tripId || 1);
                    break;
                case 'booking-details-view':
                    await this.renderBookingDetails(params.bookingId || 1);
                    break;
                case 'booking-history-view':
                    await this.renderBookingHistory();
                    break;
                case 'safety-checklist-view':
                    await this.renderSafetyChecklist(params.tripId || 1);
                    break;
                case 'maintenance-management-view':
                    await this.renderMaintenanceManagement();
                    break;
                case 'promotions-view':
                    await this.renderPromotions();
                    break;
                case 'reviews-view':
                    await this.renderReviews();
                    break;
                case 'reports-view':
                    await this.renderReports();
                    break;
                case 'user-management-view':
    await this.renderUserManagement();
    break;

case 'customer-profile-view':
    await this.renderCustomerProfile();
    break;
            }
        } catch (err) {
            console.error('View load error:', err);
            showToast(err.message, 'error');
        }
    },

    // 1. Home Page View
    // A trip is upcoming when its departure date and time are still in the future
    isUpcoming(t) {
        return new Date(`${t.tripDate}T${t.departureTime}`) > new Date();
    },

    // Label shown on trips that can no longer be booked (null when the trip is still open)
    tripLabel(t) {
        if (t.status === 'Cancelled') return 'CANCELLED';
        if (t.status === 'Completed') return 'COMPLETED';
        if (!this.isUpcoming(t)) return 'DEPARTED';
        return null;
    },

    isBookable(t) {
        return t.status === 'Scheduled' && this.isUpcoming(t);
    },

    // Open trips first (soonest departure first), then closed trips (most recent first)
    sortTrips(trips) {
        const key = t => new Date(`${t.tripDate}T${t.departureTime}`).getTime();
        const open = trips.filter(t => this.isBookable(t)).sort((a, b) => key(a) - key(b));
        const closed = trips.filter(t => !this.isBookable(t)).sort((a, b) => key(b) - key(a));
        return [...open, ...closed];
    },

    tripClosedNotice(t) {
        const label = this.tripLabel(t);
        if (!label) return '';
        const text = label === 'CANCELLED' ? 'This trip was cancelled and cannot be booked.'
            : label === 'COMPLETED' ? 'This trip has been completed and cannot be booked.'
            : `This trip departed on ${t.tripDate} at ${t.departureTime} and can no longer be booked.`;
        return `<p class="text-xs font-semibold text-rose-600 mb-2">${text}</p>`;
    },

    async renderHomePage() {
        const trips = await API.getTrips();
        const container = document.getElementById('featured-trips-grid');
        if (!container) return;

        const upcoming = trips.filter(t => t.status === 'Scheduled' && this.isUpcoming(t)).slice(0, 3);

        container.innerHTML = upcoming.map(t => `
            <div class="glass-card rounded-2xl overflow-hidden shadow-lg transition hover:shadow-2xl hover:-translate-y-1">
                ${Scenery.html(t, 'h-52')}
                <div class="p-6">
                    <div class="flex items-center justify-between mb-2">
                        <span class="text-xs font-semibold px-2.5 py-1 bg-cyan-100 text-cyan-800 rounded-full">${t.route.origin} → ${t.route.destination}</span>
                        <span class="text-xs text-slate-500 font-medium">📅 ${t.tripDate}</span>
                    </div>
                    <h3 class="text-xl font-bold text-slate-900 mb-2">🚤 ${t.boat.name}</h3>
                    <p class="text-slate-600 text-sm mb-4">Departs ${t.departureTime} · ${t.availableSeats} seats available</p>
                    <div class="flex items-center justify-between border-t border-slate-100 pt-4">
                        <div>
                            <span class="text-xs text-slate-400 block">Price per tourist</span>
                            <span class="text-xl font-extrabold text-cyan-700">LKR ${t.price.toLocaleString()}</span>
                        </div>
                        <button onclick="App.navigate('trip-booking-view', {tripId: ${t.id}})" class="px-4 py-2 bg-cyan-600 hover:bg-cyan-700 text-white font-medium rounded-xl text-sm transition">Book Trip →</button>
                    </div>
                </div>
            </div>
        `).join('');
    },

    // 2. Customer Dashboard
    async renderCustomerDashboard() {
        const user = AuthState.currentUser;
        const bookings = await API.getBookings(user.id);
        const upcomingContainer = document.getElementById('customer-upcoming-bookings');
        const pastContainer = document.getElementById('customer-past-bookings');

        const confirmed = bookings.filter(b => b.status === 'CONFIRMED' && b.trip);
        const upcoming = confirmed.filter(b => this.isUpcoming(b.trip))
            .sort((x, y) => `${x.trip.tripDate}T${x.trip.departureTime}`.localeCompare(`${y.trip.tripDate}T${y.trip.departureTime}`));
        const past = confirmed.filter(b => !this.isUpcoming(b.trip))
            .sort((x, y) => `${y.trip.tripDate}T${y.trip.departureTime}`.localeCompare(`${x.trip.tripDate}T${x.trip.departureTime}`));

        const countUp = document.getElementById('customer-upcoming-count');
        const countPast = document.getElementById('customer-past-count');
        if (countUp) countUp.textContent = upcoming.length;
        if (countPast) countPast.textContent = past.length;

        const card = (b, isPast) => `
            <div class="glass-card p-5 rounded-2xl flex flex-col md:flex-row items-center justify-between gap-4 border-l-4 ${isPast ? 'border-slate-300' : 'border-cyan-500'}">
                <div class="flex items-center gap-4 flex-1 min-w-0">
                <div class="w-36 shrink-0">${Scenery.html(b.trip, 'h-28', 'rounded-xl overflow-hidden' + (isPast ? ' grayscale opacity-80' : ''))}</div>
                <div>
                    <div class="flex items-center gap-2 mb-1">
                        <span class="font-mono font-bold ${isPast ? 'text-slate-500' : 'text-cyan-700'}">${b.bookingReference}</span>
                        <span class="px-2 py-0.5 text-xs rounded-full ${isPast ? 'bg-slate-200 text-slate-700 font-bold' : 'badge-confirmed'}">${isPast ? 'COMPLETED' : 'CONFIRMED'}</span>
                    </div>
                    <h4 class="text-lg font-bold text-slate-800">${b.trip.route.origin} → ${b.trip.route.destination}</h4>
                    <p class="text-xs text-slate-500 mt-1">🗓 ${isPast ? 'Departed' : 'Departure'}: <strong>${b.trip.tripDate} ${b.trip.departureTime}</strong> | 🚤 Boat: ${b.trip.boat.name} | Seats: ${b.seatNumbers}</p>
                </div>
                </div>
                <div class="flex items-center gap-3">
                    <button onclick="App.navigate('booking-details-view', {bookingId: ${b.id}})" class="px-4 py-2 bg-slate-800 text-white rounded-xl text-xs font-semibold hover:bg-slate-900 transition">${isPast ? 'View Receipt' : 'Digital Boarding Pass'}</button>
                    ${isPast
                        ? `<button onclick="App.openReviewModal()" class="px-3 py-2 text-amber-700 hover:bg-amber-50 rounded-xl text-xs font-semibold transition">★ Write a Review</button>`
                        : `<button onclick="App.cancelBooking(${b.id})" class="px-3 py-2 text-rose-600 hover:bg-rose-50 rounded-xl text-xs font-semibold transition">Cancel</button>`}
                </div>
            </div>`;

        if (upcomingContainer) {
            upcomingContainer.innerHTML = upcoming.length === 0
                ? `<div class="p-6 text-center text-slate-500 glass-card rounded-2xl">No active upcoming boat safari bookings found. Explore trips to book your adventure!</div>`
                : upcoming.map(b => card(b, false)).join('');
        }
        if (pastContainer) {
            pastContainer.innerHTML = past.length === 0
                ? `<div class="p-6 text-center text-slate-400 text-sm glass-card rounded-2xl">Your completed trips will appear here after you sail.</div>`
                : past.map(b => card(b, true)).join('');
        }
    },

    // 3. Desk Officer Dashboard
    async renderDeskDashboard() {
        const trips = await API.getTrips();
        const bookings = await API.getBookings();
        this.deskBookingsCache = bookings;

        // Calculate Real Backend Statistics
        const todayBookings = bookings.length;
        const pendingValidation = bookings.filter(b => b.validationStatus === 'PENDING_VALIDATION').length;
        const confirmed = bookings.filter(b => b.status === 'CONFIRMED').length;
        const cancelled = bookings.filter(b => b.status === 'CANCELLED').length;
        const walkIns = bookings.filter(b => (b.notes && b.notes.toLowerCase().includes('walk-in')) || b.paymentMethod === 'CASH_COUNTER').length;

        if (document.getElementById('desk-today-bookings-count')) document.getElementById('desk-today-bookings-count').textContent = todayBookings;
        if (document.getElementById('desk-pending-val-count')) document.getElementById('desk-pending-val-count').textContent = pendingValidation;
        if (document.getElementById('desk-confirmed-count')) document.getElementById('desk-confirmed-count').textContent = confirmed;
        if (document.getElementById('desk-cancelled-count')) document.getElementById('desk-cancelled-count').textContent = cancelled;
        if (document.getElementById('desk-walkins-count')) document.getElementById('desk-walkins-count').textContent = walkIns;

        this.filterDeskBookings();
    },

    filterDeskBookings() {
        const searchInput = (document.getElementById('desk-search-input') ? document.getElementById('desk-search-input').value.toLowerCase().trim() : '');
        const statusFilter = (document.getElementById('desk-status-filter') ? document.getElementById('desk-status-filter').value : 'ALL');

        let filtered = this.deskBookingsCache || [];

        if (statusFilter !== 'ALL') {
            filtered = filtered.filter(b => b.status === statusFilter || b.validationStatus === statusFilter);
        }

        if (searchInput) {
            filtered = filtered.filter(b =>
                (b.bookingReference && b.bookingReference.toLowerCase().includes(searchInput)) ||
                (b.user && b.user.fullName && b.user.fullName.toLowerCase().includes(searchInput)) ||
                (b.user && b.user.phoneNumber && b.user.phoneNumber.toLowerCase().includes(searchInput)) ||
                (b.user && b.user.email && b.user.email.toLowerCase().includes(searchInput)) ||
                (b.trip && b.trip.route && (b.trip.route.origin.toLowerCase().includes(searchInput) || b.trip.route.destination.toLowerCase().includes(searchInput)))
            );
        }

        const tbody = document.getElementById('desk-bookings-table');
        if (tbody) {
            if (filtered.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" class="p-6 text-center text-slate-500 text-xs">No matching customer booking records found.</td></tr>`;
                return;
            }

            tbody.innerHTML = filtered.map(b => `
                <tr class="border-b border-slate-100 hover:bg-slate-50 text-xs">
                    <td class="p-3 font-mono font-bold text-cyan-700">${b.bookingReference}</td>
                    <td class="p-3 font-bold text-slate-800">
                        ${b.user.fullName}
                        <span class="block text-[10px] text-slate-400 font-normal">📞 ${b.user.phoneNumber || 'N/A'} | ✉️ ${b.user.email}</span>
                    </td>
                    <td class="p-3 text-slate-600">${b.trip.route.origin} → ${b.trip.route.destination}</td>
                    <td class="p-3 text-slate-600 font-semibold">${b.seatCount} (${b.seatNumbers})</td>
                    <td class="p-3 font-bold text-slate-800">LKR ${b.finalPrice.toLocaleString()}</td>
                    <td class="p-3">
                        <span class="px-2 py-0.5 text-[10px] font-bold rounded-full ${b.status === 'CONFIRMED' ? 'badge-confirmed' : 'badge-cancelled'} mb-1 inline-block">${b.status}</span>
                        <span class="px-2 py-0.5 text-[10px] font-bold rounded-full ${b.validationStatus === 'VALIDATED' ? 'bg-emerald-100 text-emerald-800' : 'bg-amber-100 text-amber-800'} block">${b.validationStatus || 'VALIDATED'}</span>
                    </td>
                    <td class="p-3 flex items-center gap-1.5 flex-wrap">
                        <button onclick="App.toggleValidationStatus(${b.id}, '${b.validationStatus === 'VALIDATED' ? 'PENDING_VALIDATION' : 'VALIDATED'}')" class="px-2 py-1 bg-teal-600 text-white rounded text-[11px] font-semibold hover:bg-teal-700 transition">
                            ${b.validationStatus === 'VALIDATED' ? 'Mark Pending' : '✓ Validate'}
                        </button>
                        <button onclick="App.navigate('booking-details-view', {bookingId: ${b.id}})" class="px-2 py-1 bg-slate-800 text-white rounded text-[11px] font-semibold hover:bg-slate-900 transition">Receipt</button>
                        ${b.status === 'CONFIRMED' ? `<button onclick="App.openCancelModal(${b.id})" class="px-2 py-1 bg-rose-600 text-white rounded text-[11px] font-semibold hover:bg-rose-700 transition">Cancel</button>` : ''}
                    </td>
                </tr>
            `).join('');
        }
    },

    // ---- Tick-box trip pickers (voucher, walk-in booking, review) ----
    renderTripChecklist(id, trips, labelFn, emptyText) {
        const box = document.getElementById(id);
        if (!box) return;
        box.innerHTML = trips.length === 0
            ? `<p class="p-3 text-xs text-slate-400">${emptyText || 'No trips available.'}</p>`
            : trips.map(t => `
                <label class="trip-option">
                    <input type="checkbox" value="${t.id}" onchange="App.onTripTick(this)">
                    <span>${labelFn(t)}</span>
                </label>`).join('');
    },

    onTripTick(input) {
        const box = input.closest('.trip-checklist');
        if (box && box.dataset.mode === 'single' && input.checked) {
            box.querySelectorAll('input[type=checkbox]').forEach(cb => { if (cb !== input) cb.checked = false; });
        }
    },

    tickAllTrips(id, state) {
        document.querySelectorAll(`#${id} input[type=checkbox]`).forEach(cb => { cb.checked = state; });
    },

    setTripTicks(id, ids) {
        document.querySelectorAll(`#${id} input[type=checkbox]`).forEach(cb => { cb.checked = ids.includes(parseInt(cb.value)); });
    },

    getTickedTripIds(id) {
        return Array.from(document.querySelectorAll(`#${id} input[type=checkbox]:checked`)).map(cb => parseInt(cb.value));
    },

    async openWalkInModal() {
        const modal = document.getElementById('walkin-booking-modal');
        const tripsSelect = document.getElementById('walkin-trip-select');

        if (!modal || !tripsSelect) return;

        const trips = (await API.getTrips()).filter(t => t.status === 'Scheduled' && t.availableSeats > 0 && this.isUpcoming(t));

        this.renderTripChecklist('walkin-trip-select', trips,
            t => `<strong>${t.route.origin} → ${t.route.destination}</strong><em>${t.tripDate} ${t.departureTime} · ${t.boat.name} · ${t.availableSeats} seats left</em>`,
            'No departures scheduled');

        modal.classList.remove('hidden');
    },

    async submitWalkInBooking() {
        const name = document.getElementById('walkin-name').value.trim();
        const email = document.getElementById('walkin-email').value.trim();
        const phone = document.getElementById('walkin-phone').value.trim();
        const nic = document.getElementById('walkin-nic').value.trim();
        const tripId = this.getTickedTripIds('walkin-trip-select')[0];
        const seatCount = parseInt(document.getElementById('walkin-seat-count').value);

        if (!name || !email || !phone || !tripId || !seatCount) {
            return showToast('Please fill all required walk-in customer details!', 'error');
        }

        const payload = {
            customerName: name,
            customerEmail: email,
            customerPhone: phone,
            customerNic: nic,
            tripId: tripId,
            seatCount: seatCount,
            paymentMethod: 'CASH_COUNTER',
            notes: `Walk-in booking processed by ${AuthState.currentUser.fullName}`,
            bookingOfficerId: AuthState.currentUser.id
        };

        try {
            const booking = await API.createBooking(payload);
            showToast(`🎉 Walk-in booking created successfully! Reference: ${booking.bookingReference}`);
            document.getElementById('walkin-booking-modal').classList.add('hidden');
            this.renderDeskDashboard();
        } catch (e) {
            showToast('Walk-in booking error: ' + e.message, 'error');
        }
    },

    async toggleValidationStatus(bookingId, newStatus) {
        try {
            await API.validateBooking(bookingId, newStatus);
            showToast(`Customer validation status updated to ${newStatus}`);
            this.renderDeskDashboard();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    openCancelModal(bookingId) {
        this.currentCancelBookingId = bookingId;
        const modal = document.getElementById('cancel-reason-modal');
        if (modal) modal.classList.remove('hidden');
    },

    async confirmCancelWithReason() {
        const reason = document.getElementById('cancel-reason-input').value.trim();
        if (!reason) return showToast('Please enter a cancellation reason!', 'error');

        try {
            await API.cancelBookingWithReason(this.currentCancelBookingId, reason);
            showToast('Booking cancelled and reason recorded.');
            document.getElementById('cancel-reason-modal').classList.add('hidden');
            document.getElementById('cancel-reason-input').value = '';
            this.renderDeskDashboard();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // 4. Fleet Manager Dashboard
    async renderFleetDashboard() {
        const boats = await API.getBoats();
        const logs = await API.getAllMaintenanceLogs();

        document.getElementById('fleet-total-boats').textContent = boats.length;
        document.getElementById('fleet-available-boats').textContent = boats.filter(b => b.status === 'AVAILABLE').length;
        document.getElementById('fleet-maintenance-boats').textContent = boats.filter(b => b.status === 'MAINTENANCE').length;

        const tbody = document.getElementById('fleet-boats-table');
        if (tbody) {
            tbody.innerHTML = boats.map(b => `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-bold text-slate-800 text-xs">${b.name} (${b.registrationNumber})</td>
                    <td class="p-3 text-xs font-semibold text-slate-700">${b.capacity} Passengers</td>
                    <td class="p-3 text-xs text-slate-600">${b.captainName} (${b.crewCount} crew)</td>
                    <td class="p-3"><span class="px-2.5 py-1 text-xs font-bold rounded-full ${b.status === 'AVAILABLE' ? 'badge-available' : 'badge-maintenance'}">${b.status}</span></td>
                    <td class="p-3 text-xs font-semibold ${b.engineStatus === 'GOOD' ? 'text-emerald-600' : 'text-rose-600'}">${b.engineStatus} (${b.fuelLevelPercentage}%)</td>
                    <td class="p-3 flex items-center gap-2">
                        <button onclick="App.navigate('boat-details-view', {boatId: ${b.id}})" class="px-2.5 py-1 bg-cyan-600 text-white text-xs font-medium rounded-lg">Logs</button>
                        <button onclick="App.deleteBoat(${b.id})" class="px-2.5 py-1 bg-rose-600 text-white text-xs font-medium rounded-lg">Delete</button>
                    </td>
                </tr>
            `).join('');
        }
    },

    // 5. Safety Officer Dashboard
    async renderSafetyDashboard() {
        const trips = await API.getTrips();
        let checklists = [];
        try { checklists = await API.getAllSafetyChecklists(); } catch (e) { checklists = []; }
        const byTrip = {};
        (Array.isArray(checklists) ? checklists : []).forEach(c => { if (c.trip) byTrip[c.trip.id] = c; });

        let cleared = 0, hold = 0, pending = 0;
        const badge = {
            CLEARED: '<span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap bg-emerald-100 text-emerald-800">Cleared</span>',
            HOLD: '<span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap bg-rose-100 text-rose-800">On Hold</span>',
            PENDING: '<span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap bg-amber-100 text-amber-800">Awaiting Audit</span>'
        };
        const tbody = document.getElementById('safety-trips-table');
        if (tbody) {
            tbody.innerHTML = trips.map(t => {
                const c = byTrip[t.id];
                const st = c ? (c.departureApproved ? 'CLEARED' : 'HOLD') : 'PENDING';
                if (st === 'CLEARED') cleared++; else if (st === 'HOLD') hold++; else pending++;
                return `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-bold text-slate-800 text-xs">${t.route.origin} → ${t.route.destination}</td>
                    <td class="p-3 text-xs text-slate-600">${t.boat.name} (${t.boat.registrationNumber})</td>
                    <td class="p-3 text-xs font-semibold text-cyan-700">${t.tripDate} ${t.departureTime}</td>
                    <td class="p-3 text-xs font-semibold text-slate-700">${t.bookedSeats} / ${t.boat.capacity} Passengers</td>
                    <td class="p-3"><span class="px-2.5 py-1 text-xs font-bold rounded-full badge-scheduled">${t.status}</span></td>
                    <td class="p-3">${badge[st]}</td>
                    <td class="p-3">
                        <button onclick="App.navigate('safety-checklist-view', {tripId: ${t.id}})" class="px-3 py-1.5 bg-emerald-600 text-white rounded-lg text-xs font-bold shadow hover:bg-emerald-700">${c ? 'Review / Edit Audit →' : 'Start Safety Audit →'}</button>
                    </td>
                </tr>`;
            }).join('');
        }
        document.getElementById('saf-count-cleared').textContent = cleared;
        document.getElementById('saf-count-hold').textContent = hold;
        document.getElementById('saf-count-pending').textContent = pending;
    },

    // 5b. Safety Inspection Records (Read / Update / Hold / Delete)
    async renderSafetyRecords() {
        try {
            this.safetyRecords = await API.getAllSafetyChecklists();
        } catch (e) {
            this.safetyRecords = [];
            showToast(e.message, 'error');
        }
        this.filterSafetyRecords();
    },

    filterSafetyRecords() {
        const tbody = document.getElementById('safety-records-table');
        if (!tbody) return;
        const q = (document.getElementById('safety-record-search').value || '').toLowerCase().trim();
        const f = document.getElementById('safety-record-filter').value;
        const rows = (this.safetyRecords || []).filter(c => {
            const st = c.departureApproved ? 'CLEARED' : 'HOLD';
            if (f !== 'ALL' && st !== f) return false;
            const text = `${c.trip.route.origin} ${c.trip.route.destination} ${c.trip.boat.name} ${c.inspectorName || ''}`.toLowerCase();
            return !q || text.includes(q);
        }).sort((x, y) => String(y.inspectionTime).localeCompare(String(x.inspectionTime)));

        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="p-6 text-center text-xs text-slate-400">No safety inspection records found.</td></tr>';
            return;
        }
        tbody.innerHTML = rows.map(c => `
            <tr class="border-b border-slate-100 hover:bg-slate-50">
                <td class="p-3 text-xs"><div class="font-bold text-slate-800">${c.trip.route.origin} → ${c.trip.route.destination}</div><div class="text-cyan-700 font-semibold">${c.trip.tripDate} ${c.trip.departureTime}</div></td>
                <td class="p-3 text-xs text-slate-600">${c.trip.boat.name}</td>
                <td class="p-3 text-xs text-slate-700">${c.inspectorName || '-'}</td>
                <td class="p-3 text-xs text-slate-600">
                    <div>🦺 ${c.lifeJacketsChecked ? c.lifeJacketCount + ' checked' : '<span class="text-rose-600 font-bold">not checked</span>'} · 🩹 ${c.firstAidKitChecked ? 'kit OK' : '<span class="text-rose-600 font-bold">missing</span>'}</div>
                    <div>🌦 ${c.weatherAdvisoryStatus}</div>
                    ${c.comments ? `<div class="text-slate-400 italic">${c.comments}</div>` : ''}
                </td>
                <td class="p-3 text-xs text-slate-500">${String(c.inspectionTime || '').replace('T', ' ').substring(0, 16)}</td>
                <td class="p-3">${c.departureApproved
                    ? '<span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap bg-emerald-100 text-emerald-800">Cleared</span>'
                    : '<span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap bg-rose-100 text-rose-800">On Hold</span>'}</td>
                <td class="p-3"><div class="flex flex-wrap gap-1.5">
                    <button onclick="App.navigate('safety-checklist-view', {tripId: ${c.trip.id}})" class="px-2.5 py-1 bg-slate-800 text-white rounded-lg text-xs font-bold">Edit</button>
                    <button onclick="App.deleteSafetyRecord(${c.trip.id})" class="px-2.5 py-1 bg-rose-600 text-white rounded-lg text-xs font-bold">Delete</button>
                </div></td>
            </tr>`).join('');
    },

    async deleteSafetyRecord(tripId) {
        if (!confirm('Delete this safety audit record? The trip will return to "Awaiting Audit". This cannot be undone.')) return;
        try {
            await API.deleteSafetyChecklist(tripId);
            showToast('Safety audit record deleted.');
            this.navigate('safety-records-view');
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async holdSafetyDeparture() {
        const reason = prompt('Reason for placing this departure on safety hold:');
        if (reason === null) return;
        if (!reason.trim()) { showToast('A reason is required to place a departure on hold.', 'error'); return; }
        try {
            const inspector = document.getElementById('safety-inspector-name').value || AuthState.currentUser.fullName;
            await API.holdDeparture(this.currentTripIdForSafety, inspector, reason.trim());
            showToast('Departure placed on safety hold.');
            this.renderSafetyChecklist(this.currentTripIdForSafety);
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // 6. Marketing Officer Dashboard
    async renderMarketingDashboard() {
        const trips = await API.getTrips();
        const promos = await API.getPromotions();
        const reviewSummary = await API.getReviewSummary();

        document.getElementById('mkt-total-pkgs').textContent = trips.length;
        document.getElementById('mkt-active-promos').textContent = promos.filter(p => (p.computedStatus || (p.active ? 'ACTIVE' : 'INACTIVE')) === 'ACTIVE').length;
        document.getElementById('mkt-total-reviews').textContent = reviewSummary.totalReviews;
        document.getElementById('mkt-avg-rating').textContent = reviewSummary.averageRating.toFixed(1);

        const pkgContainer = document.getElementById('mkt-packages-grid');
        if (pkgContainer) {
            pkgContainer.innerHTML = trips.map(t => `
                <div class="glass-card p-3 rounded-2xl flex items-center gap-4 overflow-hidden">
                    <div class="w-28 shrink-0">${Scenery.html(t, 'h-20', 'compact rounded-xl overflow-hidden')}</div>
                    <div class="flex-1 min-w-0">
                        <span class="text-[10px] font-bold px-2 py-0.5 bg-teal-100 text-teal-800 rounded-full">${t.status}</span>
                        <h4 class="font-bold text-slate-900 text-sm mt-1">${t.route.origin} → ${t.route.destination}</h4>
                        <p class="text-xs text-slate-500">🚤 ${t.boat.name} | 📅 ${t.tripDate} ${t.departureTime} | LKR ${t.price.toLocaleString()} per tourist</p>
                    </div>
                    <button onclick="App.openTripModal(${t.id})" class="px-3 py-1.5 bg-slate-800 text-white rounded-lg text-xs font-semibold">Manage</button>
                </div>
            `).join('');
        }
    },

    // 7. Admin Dashboard
    async renderAdminDashboard() {
        const report = await API.getSummaryReport();
        document.getElementById('stat-total-customers').textContent = report.totalCustomers;
        document.getElementById('stat-total-boats').textContent = report.totalBoats;
        document.getElementById('stat-total-bookings').textContent = report.totalBookings;
        document.getElementById('stat-total-revenue').textContent = 'LKR ' + (report.totalRevenue || 0).toLocaleString();

        const bookings = await API.getBookings();
        const tbody = document.getElementById('admin-recent-bookings-table');
        if (tbody) {
            tbody.innerHTML = bookings.slice(0, 6).map(b => `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-mono font-bold text-cyan-700 text-xs">${b.bookingReference}</td>
                    <td class="p-3 font-medium text-slate-800 text-xs">${b.user.fullName}</td>
                    <td class="p-3 text-slate-600 text-xs">${b.trip.route.origin} → ${b.trip.route.destination}</td>
                    <td class="p-3 text-slate-600 text-xs">${b.seatCount} (${b.seatNumbers})</td>
                    <td class="p-3 font-semibold text-slate-800 text-xs">LKR ${b.finalPrice.toLocaleString()}</td>
                    <td class="p-3"><span class="px-2 py-0.5 text-xs rounded-full ${b.status === 'CONFIRMED' ? 'badge-confirmed' : 'badge-cancelled'}">${b.status}</span></td>
                </tr>
            `).join('');
        }
    },

    // Trip Listing
    async renderTripListing(params = {}) {
        const origin = (params.origin || '').toLowerCase();

        const role = AuthState.currentUser ? AuthState.currentUser.role : 'CUSTOMER';
        const isOfficer = role === 'MARKETING_OFFICER' || role === 'ADMIN';

        const addBtn = document.getElementById('add-package-btn');
        if (addBtn) addBtn.classList.toggle('hidden', !isOfficer);

        let trips = await API.getTrips();
        if (origin) {
            trips = trips.filter(t =>
                t.route.origin.toLowerCase().includes(origin) || t.route.destination.toLowerCase().includes(origin)
            );
        }
        trips = this.sortTrips(trips);

        const container = document.getElementById('package-listing-grid');
        if (container) {
            container.innerHTML = trips.map(t => `
                <div class="glass-card rounded-2xl overflow-hidden shadow transition hover:shadow-xl flex flex-col justify-between ${this.isBookable(t) ? '' : 'opacity-80'}">
                    <div>
                        <div class="relative">
                            ${Scenery.html(t, 'h-52', this.isBookable(t) ? '' : 'grayscale opacity-60')}
                            ${this.tripLabel(t) ? `<span class="absolute top-3 left-3 px-3 py-1 bg-rose-600 text-white text-[11px] font-extrabold rounded-full shadow">⏱ ${this.tripLabel(t)}</span>` : ''}
                        </div>
                        <div class="p-5">
                            <div class="flex items-center justify-between mb-2">
                                <span class="text-xs font-bold px-2.5 py-1 bg-teal-100 text-teal-800 rounded-full">${t.route.origin} → ${t.route.destination}</span>
                                <span class="text-xs text-slate-500 font-medium">📅 ${t.tripDate}</span>
                            </div>
                            <h3 class="text-lg font-bold text-slate-900 mb-1">🚤 ${t.boat.name}</h3>
                            <p class="text-slate-600 text-xs mb-3">Departs ${t.departureTime} · Arrives ${t.arrivalTime}${t.guide ? ' · Guide: ' + t.guide.name : ''}</p>
                            ${this.tripClosedNotice(t)}
                            ${isOfficer ? `<span class="text-[10px] font-bold px-2 py-0.5 rounded-full ${t.status === 'Scheduled' ? 'bg-emerald-100 text-emerald-800' : t.status === 'Cancelled' ? 'bg-rose-100 text-rose-800' : 'bg-slate-200 text-slate-600'}">${t.status}</span>` : ''}
                        </div>
                    </div>
                    <div class="p-5 border-t border-slate-100 flex items-center justify-between bg-slate-50/50">
                        <div>
                            <span class="text-xs text-slate-400 block">Per Person · ${t.availableSeats} seats left</span>
                            <span class="text-lg font-extrabold text-cyan-700">LKR ${t.price.toLocaleString()}</span>
                        </div>
                        <div class="flex gap-2">
                            ${isOfficer ? `
                                <button onclick="App.openTripModal(${t.id})" class="px-3 py-2 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-xl text-xs font-bold transition">Edit</button>
                                <button onclick="App.deleteTripConfirm(${t.id})" class="px-3 py-2 bg-rose-100 hover:bg-rose-200 text-rose-700 rounded-xl text-xs font-bold transition">Delete</button>
                            ` : `
                                ${this.isBookable(t)
                                    ? `<button onclick="App.navigate('trip-booking-view', {tripId: ${t.id}})" class="px-4 py-2 bg-cyan-600 hover:bg-cyan-700 text-white font-medium rounded-xl text-xs transition">Book This Trip</button>`
                                    : `<span class="px-4 py-2 bg-slate-200 text-slate-500 rounded-xl text-xs font-bold cursor-not-allowed">Not available</span>`}
                            `}
                        </div>
                    </div>
                </div>
            `).join('');
        }
    },

    async openTripModal(id = null) {
        const [boats, routes, guides] = await Promise.all([API.getBoats(), API.getRoutes(), API.getGuides()]);

        document.getElementById('trip-boat-select').innerHTML = boats.map(b => `<option value="${b.id}">${b.name} (${b.capacity} seats)</option>`).join('');
        document.getElementById('trip-route-select').innerHTML = routes.map(r => `<option value="${r.id}">${r.origin} → ${r.destination}</option>`).join('');
        document.getElementById('trip-guide-select').innerHTML = '<option value="">No guide assigned</option>' +
            guides.map(g => `<option value="${g.id}">${g.name}</option>`).join('');

        document.getElementById('package-id').value = '';
        document.getElementById('package-date').value = '';
        document.getElementById('package-departure').value = '';
        document.getElementById('package-arrival').value = '';
        document.getElementById('package-price').value = '';
        document.getElementById('package-capacity').value = '20';
        document.getElementById('package-status').value = 'Scheduled';

        if (id) {
            try {
                const t = await API.getTrip(id);
                document.getElementById('package-id').value = t.id;
                document.getElementById('trip-boat-select').value = t.boat.id;
                document.getElementById('trip-route-select').value = t.route.id;
                document.getElementById('trip-guide-select').value = t.guide ? t.guide.id : '';
                document.getElementById('package-date').value = t.tripDate;
                document.getElementById('package-departure').value = t.departureTime;
                document.getElementById('package-arrival').value = t.arrivalTime;
                document.getElementById('package-price').value = t.price;
                document.getElementById('package-capacity').value = t.passengerCapacity;
                document.getElementById('package-status').value = t.status;
                document.getElementById('package-modal-title').textContent = 'Edit Trip';
            } catch (e) {
                showToast(e.message, 'error');
                return;
            }
        } else {
            document.getElementById('package-modal-title').textContent = '+ Add New Trip';
        }

        document.getElementById('package-modal').classList.remove('hidden');
    },

    async saveTrip() {
        const id = document.getElementById('package-id').value;
        const boatId = document.getElementById('trip-boat-select').value;
        const routeId = document.getElementById('trip-route-select').value;
        const guideId = document.getElementById('trip-guide-select').value || null;

        const payload = {
            tripDate: document.getElementById('package-date').value,
            departureTime: document.getElementById('package-departure').value,
            arrivalTime: document.getElementById('package-arrival').value,
            price: parseFloat(document.getElementById('package-price').value),
            passengerCapacity: parseInt(document.getElementById('package-capacity').value) || 20,
            status: document.getElementById('package-status').value
        };

        if (!boatId || !routeId || !payload.tripDate || !payload.departureTime || !payload.arrivalTime || !payload.price) {
            showToast('Please fill in all required fields (*).', 'error');
            return;
        }

        try {
            if (id) {
                await API.updateTrip(id, boatId, routeId, guideId, payload);
                showToast('Trip updated successfully.');
            } else {
                await API.createTrip(boatId, routeId, guideId, payload);
                showToast('Trip created successfully.');
            }
            document.getElementById('package-modal').classList.add('hidden');
            this.renderTripListing();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async deleteTripConfirm(id) {
        if (!confirm('Are you sure you want to delete this trip?')) return;
        try {
            await API.deleteTrip(id);
            showToast('Trip removed.');
            this.renderTripListing();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Boat Management
    async renderBoatManagement() {
        const boats = await API.getBoats();
        const tbody = document.getElementById('boats-table-body');
        if (tbody) {
            tbody.innerHTML = boats.map(b => `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-bold text-slate-800 text-sm"><div class="flex items-center gap-3"><div class="w-16 shrink-0">${Scenery.html({ id: b.id, boat: b, route: {} }, 'h-10', 'compact rounded-lg overflow-hidden')}</div>${b.name}</div></td>
                    <td class="p-3 font-mono text-xs text-slate-600">${b.registrationNumber}</td>
                    <td class="p-3 text-xs text-slate-600 font-semibold">${b.capacity} Passengers</td>
                    <td class="p-3 text-xs">${b.captainName} (${b.crewCount} crew)</td>
                    <td class="p-3"><span class="px-2.5 py-1 text-xs font-bold rounded-full ${b.status === 'AVAILABLE' ? 'badge-available' : 'badge-maintenance'}">${b.status}</span></td>
                    <td class="p-3 text-xs font-medium ${b.engineStatus === 'GOOD' ? 'text-emerald-600' : 'text-rose-600'}">${b.engineStatus} (${b.fuelLevelPercentage}%)</td>
                    <td class="p-3"><span class="px-2.5 py-1 text-xs font-bold rounded-full ${b.safetyStatus === 'Cleared' ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800'}">${b.safetyStatus || 'Cleared'}</span></td>
                    <td class="p-3 flex items-center gap-2">
                        <button onclick="App.navigate('boat-details-view', {boatId: ${b.id}})" class="px-2.5 py-1 bg-cyan-600 text-white text-xs font-medium rounded-lg hover:bg-cyan-700">Details</button>
                        <button onclick="App.openBoatModal(${b.id})" class="px-2.5 py-1 bg-slate-800 text-white text-xs font-medium rounded-lg hover:bg-slate-900">Edit</button>
                        <button onclick="App.deleteBoat(${b.id})" class="px-2.5 py-1 bg-rose-600 text-white text-xs font-medium rounded-lg hover:bg-rose-700">Delete</button>
                    </td>
                </tr>
            `).join('');
        }
    },

    async openBoatModal(id = null) {
        document.getElementById('boat-id').value = '';
        document.getElementById('boat-name').value = '';
        document.getElementById('boat-reg').value = '';
        document.getElementById('boat-capacity').value = '';
        document.getElementById('boat-engine-type').value = '';
        document.getElementById('boat-captain').value = '';
        document.getElementById('boat-crew-count').value = '2';
        document.getElementById('boat-status').value = 'AVAILABLE';
        document.getElementById('boat-engine-status').value = 'GOOD';
        document.getElementById('boat-safety-status').value = 'Cleared';
        document.getElementById('boat-fuel').value = '100';

        if (id) {
            try {
                const b = await API.getBoat(id);
                document.getElementById('boat-id').value = b.id;
                document.getElementById('boat-name').value = b.name || '';
                document.getElementById('boat-reg').value = b.registrationNumber || '';
                document.getElementById('boat-capacity').value = b.capacity ?? '';
                document.getElementById('boat-engine-type').value = b.engineType || '';
                document.getElementById('boat-captain').value = b.captainName || '';
                document.getElementById('boat-crew-count').value = b.crewCount ?? 2;
                document.getElementById('boat-status').value = b.status || 'AVAILABLE';
                document.getElementById('boat-engine-status').value = b.engineStatus || 'GOOD';
                document.getElementById('boat-safety-status').value = b.safetyStatus || 'Cleared';
                document.getElementById('boat-fuel').value = b.fuelLevelPercentage ?? 100;
                document.getElementById('boat-modal-title').textContent = 'Edit Boat';
            } catch (e) {
                showToast(e.message, 'error');
                return;
            }
        } else {
            document.getElementById('boat-modal-title').textContent = '+ Add New Boat';
        }

        document.getElementById('boat-modal').classList.remove('hidden');
    },

    async saveBoat() {
        const id = document.getElementById('boat-id').value;
        const payload = {
            name: document.getElementById('boat-name').value.trim(),
            registrationNumber: document.getElementById('boat-reg').value.trim(),
            capacity: parseInt(document.getElementById('boat-capacity').value),
            engineType: document.getElementById('boat-engine-type').value.trim(),
            captainName: document.getElementById('boat-captain').value.trim(),
            crewCount: parseInt(document.getElementById('boat-crew-count').value) || 0,
            status: document.getElementById('boat-status').value,
            engineStatus: document.getElementById('boat-engine-status').value,
            safetyStatus: document.getElementById('boat-safety-status').value,
            fuelLevelPercentage: parseFloat(document.getElementById('boat-fuel').value) || 0
        };

        if (!payload.name || !payload.registrationNumber || !payload.capacity) {
            showToast('Please fill in all required fields (*).', 'error');
            return;
        }

        try {
            if (id) {
                await API.updateBoat(id, payload);
                showToast('Boat updated successfully.');
            } else {
                const user = AuthState.currentUser;
                const fleetManagerId = user && user.role === 'FLEET_MANAGER' ? user.id : null;
                await API.createBoat(payload, fleetManagerId);
                showToast('Boat created successfully.');
            }
            document.getElementById('boat-modal').classList.add('hidden');
            this.renderBoatManagement();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Boat Details
    async renderBoatDetails(boatId) {
        const boat = await API.getBoat(boatId);
        const logs = await API.getMaintenanceLogs(boatId);

        document.getElementById('boat-detail-name').textContent = boat.name;
        document.getElementById('boat-detail-reg').textContent = boat.registrationNumber;
        document.getElementById('boat-detail-capacity').textContent = boat.capacity;
        document.getElementById('boat-detail-status').textContent = boat.status;
        document.getElementById('boat-detail-engine').textContent = boat.engineStatus;
        document.getElementById('boat-detail-fuel').textContent = boat.fuelLevelPercentage + '%';
        document.getElementById('boat-detail-captain').textContent = boat.captainName;

        const logsContainer = document.getElementById('boat-detail-logs');
        if (logsContainer) {
            logsContainer.innerHTML = logs.length === 0 ? `<p class="text-slate-500 text-sm">No maintenance history recorded for this vessel.</p>` :
                logs.map(l => `
                    <div class="p-3 border-b border-slate-100 text-xs">
                        <div class="flex justify-between font-bold text-slate-800 mb-1">
                            <span>🗓 ${l.maintenanceDate} | Performed by: ${l.performedBy}</span>
                            <span class="text-cyan-700">LKR ${(l.cost||0).toLocaleString()}</span>
                        </div>
                        <p class="text-slate-600">${l.description}</p>
                    </div>
                `).join('');
        }
    },

    // Trip Schedules Page
    async renderTripSchedules() {
        const trips = this.sortTrips(await API.getTrips());
        const container = document.getElementById('trip-schedules-list');
        const role = AuthState.currentUser ? AuthState.currentUser.role : 'CUSTOMER';
        const canBook = ['CUSTOMER', 'DESK_OFFICER', 'ADMIN'].includes(role);

        if (container) {
            container.innerHTML = trips.map(t => `
                <div class="glass-card p-5 rounded-2xl flex flex-col md:flex-row items-center justify-between gap-4 border-l-4 ${this.isBookable(t) ? 'border-cyan-600' : 'border-rose-400 opacity-80'}">
                    <div>
                        <span class="text-xs font-bold px-2.5 py-1 bg-cyan-100 text-cyan-800 rounded-full">${t.route.origin} → ${t.route.destination}</span>
                        ${this.tripLabel(t) ? `<span class="ml-2 text-[11px] font-extrabold px-2.5 py-1 bg-rose-600 text-white rounded-full">⏱ ${this.tripLabel(t)}</span>` : ''}
                        <h3 class="text-lg font-bold text-slate-900 mt-2">🚤 ${t.boat.name}</h3>
                        <p class="text-xs text-slate-600 mt-1">🗓 Departure: <strong>${t.tripDate} ${t.departureTime}</strong>${t.guide ? ' | Guide: ' + t.guide.name : ''}</p>
                    </div>
                    <div class="text-right flex flex-col items-end gap-2">
                        <span class="text-sm font-bold ${this.isBookable(t) ? 'text-cyan-700' : 'text-rose-600'}">${this.isBookable(t) ? t.availableSeats + ' Seats Available' : 'Booking closed'}</span>
                        ${canBook && this.isBookable(t) ? `<button onclick="App.navigate('trip-booking-view', {tripId: ${t.id}})" class="px-5 py-2 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-xs font-bold shadow transition">Book Trip Seats →</button>` : ''}
                    </div>
                </div>
            `).join('');
        }
    },

    // Trip Booking Page (with Interactive Seat Selector Grid)
    async renderTripBooking(tripId) {
        const trip = await API.getTrip(tripId);
        this.currentTrip = trip;
        this.selectedSeats = [];
        this.currentDiscountAmount = 0;
        this.currentPromoCode = '';
        document.getElementById('promo-code-input').value = '';

        const photoBox = document.getElementById('booking-trip-photo');
        if (photoBox) photoBox.innerHTML = Scenery.html(trip, 'h-60', 'rounded-2xl mb-5 overflow-hidden');
        document.getElementById('booking-trip-title').textContent = `${trip.route.origin} → ${trip.route.destination}`;
        document.getElementById('booking-trip-time').textContent = `${trip.tripDate} ${trip.departureTime}`;
        document.getElementById('booking-trip-boat').textContent = `${trip.boat.name} (${trip.boat.capacity} Max Capacity)`;
        document.getElementById('booking-unit-price').textContent = trip.price;

        const grid = document.getElementById('seat-map-grid');
        grid.innerHTML = '';
        const capacity = trip.boat.capacity;
        const bookedCount = trip.bookedSeats;

        for (let i = 1; i <= capacity; i++) {
            const seatNum = `S-${i < 10 ? '0' + i : i}`;
            const isAlreadyBooked = i <= bookedCount;

            const seatBtn = document.createElement('button');
            seatBtn.className = `seat-btn ${isAlreadyBooked ? 'seat-booked' : 'seat-available'}`;
            seatBtn.textContent = seatNum;
            if (isAlreadyBooked) {
                seatBtn.disabled = true;
            } else {
                seatBtn.addEventListener('click', () => this.toggleSeatSelection(seatNum, seatBtn));
            }
            grid.appendChild(seatBtn);
        }

        this.updateBookingSummary();
    },

    toggleSeatSelection(seatNum, btnEl) {
        if (this.selectedSeats.includes(seatNum)) {
            this.selectedSeats = this.selectedSeats.filter(s => s !== seatNum);
            btnEl.classList.remove('seat-selected');
        } else {
            this.selectedSeats.push(seatNum);
            btnEl.classList.add('seat-selected');
        }
        if (this.currentPromoCode) {
            this.currentDiscountAmount = 0;
            this.currentPromoCode = '';
            showToast('Seat selection changed. Please re-apply your voucher.', 'error');
        }
        this.updateBookingSummary();
    },

    updateBookingSummary() {
        const unitPrice = this.currentTrip ? this.currentTrip.price : 0;
        const count = this.selectedSeats.length;
        const subtotal = count * unitPrice;
        const promoDiscount = this.currentDiscountAmount || 0;
        const finalPrice = Math.max(0, subtotal - promoDiscount);

        document.getElementById('selected-seats-list').textContent = count > 0 ? this.selectedSeats.join(', ') : 'None selected';
        document.getElementById('booking-seat-count').textContent = count;
        document.getElementById('booking-subtotal').textContent = 'LKR ' + subtotal.toLocaleString();
        document.getElementById('booking-discount').textContent = 'LKR ' + promoDiscount.toLocaleString();
        document.getElementById('booking-total').textContent = 'LKR ' + finalPrice.toLocaleString();
    },

    async applyPromoCode() {
        const codeInput = document.getElementById('promo-code-input').value.trim();
        if (!codeInput) return showToast('Please enter a voucher code', 'error');
        if (this.selectedSeats.length === 0) return showToast('Please select at least 1 seat before applying a voucher.', 'error');

        try {
            const result = await API.validatePromotion(codeInput, this.currentTrip.id, this.selectedSeats.length);
            this.currentDiscountAmount = result.discountAmount;
            this.currentPromoCode = result.code;
            showToast(`Voucher applied! You saved LKR ${result.discountAmount.toLocaleString()}.`);
        } catch (e) {
            this.currentDiscountAmount = 0;
            this.currentPromoCode = '';
            showToast(e.message, 'error');
        }
        this.updateBookingSummary();
    },

    async submitBooking() {
        if (this.selectedSeats.length === 0) {
            return showToast('Please select at least 1 seat on the boat grid map!', 'error');
        }

        const payload = {
            userId: AuthState.currentUser.id,
            tripId: this.currentTrip.id,
            seatCount: this.selectedSeats.length,
            seatNumbers: this.selectedSeats.join(', '),
            promoCode: this.currentPromoCode || '',
            paymentMethod: document.getElementById('payment-method-select').value,
            notes: document.getElementById('booking-notes-input').value
        };

        try {
            const booking = await API.createBooking(payload);
            showToast('🎉 Booking confirmed successfully! Reference: ' + booking.bookingReference);
            this.navigate('booking-details-view', { bookingId: booking.id });
        } catch (err) {
            showToast('Booking failed: ' + err.message, 'error');
        }
    },

    // Booking Details (Confirmation Receipt)
    async renderBookingDetails(bookingId) {
        const b = await API.getBooking(bookingId);
        document.getElementById('receipt-ref').textContent = b.bookingReference;
        document.getElementById('receipt-status').textContent = b.status;
        document.getElementById('receipt-customer').textContent = b.user.fullName;
        document.getElementById('receipt-package').textContent = `${b.trip.route.origin} → ${b.trip.route.destination}`;
        document.getElementById('receipt-date').textContent = `${b.trip.tripDate} ${b.trip.departureTime}`;
        document.getElementById('receipt-boat').textContent = `${b.trip.boat.name} (${b.trip.boat.registrationNumber})`;
        document.getElementById('receipt-seats').textContent = `${b.seatCount} seat(s) [${b.seatNumbers}]`;
        document.getElementById('receipt-price').textContent = `LKR ${b.finalPrice.toLocaleString()}`;
    },

    // Booking History
    async renderBookingHistory() {
        const user = AuthState.currentUser;
        const bookings = await API.getBookings(user.role === 'CUSTOMER' ? user.id : null);
        const tbody = document.getElementById('history-table-body');
        if (tbody) {
            tbody.innerHTML = bookings.map(b => `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-mono font-bold text-cyan-700 text-xs">${b.bookingReference}</td>
                    <td class="p-3 text-xs font-bold text-slate-800">${b.user.fullName}</td>
                    <td class="p-3 text-xs text-slate-600">${b.trip.route.origin} → ${b.trip.route.destination}</td>
                    <td class="p-3 text-xs text-slate-600">${b.seatNumbers}</td>
                    <td class="p-3 font-bold text-slate-800 text-xs">LKR ${b.finalPrice.toLocaleString()}</td>
                    <td class="p-3"><span class="px-2 py-0.5 text-xs rounded-full ${b.status === 'CONFIRMED' ? 'badge-confirmed' : 'badge-cancelled'}">${b.status}</span></td>
                    <td class="p-3 flex items-center gap-2">
                        <button onclick="App.navigate('booking-details-view', {bookingId: ${b.id}})" class="px-2.5 py-1 bg-slate-800 text-white text-xs rounded-lg hover:bg-slate-900">Receipt</button>
                        ${b.status === 'CONFIRMED' ? `<button onclick="App.cancelBooking(${b.id})" class="px-2 py-1 text-rose-600 hover:bg-rose-50 rounded-lg text-xs font-semibold">Cancel</button>` : ''}
                    </td>
                </tr>
            `).join('');
        }
    },

    // Customer Profile View
    async renderCustomerProfile() {
        const c = await API.getCustomer(AuthState.currentUser.id);
        const set = (id, value) => { document.getElementById(id).value = value || ''; };
        set('profile-first-name', c.firstName);
        set('profile-last-name', c.lastName);
        set('profile-email', c.email);
        set('profile-phone', c.phoneNumber);
        set('profile-street', c.street);
        set('profile-city', c.city);
        set('profile-country', c.country);
        document.getElementById('profile-member-since').textContent = c.registrationDate || '-';
        document.getElementById('profile-account-status').textContent = c.accountStatus || '-';
    },

    async saveCustomerProfile() {
        const get = (id) => document.getElementById(id).value.trim();
        if (!get('profile-first-name')) {
            return showToast('First name cannot be empty.', 'error');
        }
        const payload = {
            firstName: get('profile-first-name'),
            lastName: get('profile-last-name'),
            phoneNumber: get('profile-phone'),
            street: get('profile-street'),
            city: get('profile-city'),
            country: get('profile-country')
        };
        try {
            await API.updateCustomerProfile(AuthState.currentUser.id, payload);
            showToast('Profile updated successfully.');
            await this.renderCustomerProfile();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Safety Checklist View
    async renderSafetyChecklist(tripId) {
        const trip = await API.getTrip(tripId);
        document.getElementById('safety-trip-title').textContent = `${trip.route.origin} → ${trip.route.destination} - ${trip.boat.name}`;

        try {
            const safety = await API.getSafetyChecklist(tripId);
            document.getElementById('safety-existing-actions').classList.remove('hidden');
            document.getElementById('lifejackets-check').checked = safety.lifeJacketsChecked;
            document.getElementById('lifejackets-count').value = safety.lifeJacketCount;
            document.getElementById('firstaid-check').checked = safety.firstAidKitChecked;
            document.getElementById('emergency-contact').value = safety.emergencyContactNumber;
            document.getElementById('weather-status').value = safety.weatherAdvisoryStatus;
            document.getElementById('safety-inspector-name').value = safety.inspectorName || AuthState.currentUser.fullName;
            document.getElementById('safety-comments').value = safety.comments || '';

            const statusBadge = document.getElementById('departure-status-badge');
            if (safety.departureApproved) {
                statusBadge.className = 'px-3 py-1 bg-emerald-100 text-emerald-800 font-bold rounded-full text-xs';
                statusBadge.textContent = '✅ DEPARTURE APPROVED';
            } else {
                statusBadge.className = 'px-3 py-1 bg-rose-100 text-rose-800 font-bold rounded-full text-xs';
                statusBadge.textContent = '⛔ DEPARTURE HOLD ON SAFETY';
            }
        } catch (e) {
            document.getElementById('safety-existing-actions').classList.add('hidden');
            document.getElementById('lifejackets-check').checked = false;
            document.getElementById('lifejackets-count').value = trip.boat.capacity;
            document.getElementById('firstaid-check').checked = false;
            document.getElementById('emergency-contact').value = '+94 77 123 4567';
            document.getElementById('weather-status').value = 'CLEAR';
            document.getElementById('safety-inspector-name').value = AuthState.currentUser.fullName;
            document.getElementById('safety-comments').value = '';
            const statusBadge = document.getElementById('departure-status-badge');
            statusBadge.className = 'px-3 py-1 bg-amber-100 text-amber-800 font-bold rounded-full text-xs';
            statusBadge.textContent = 'PENDING SAFETY AUDIT';
        }

        this.currentTripIdForSafety = tripId;
    },

    async saveSafetyChecklist() {
        const count = parseInt(document.getElementById('lifejackets-count').value);
        const phone = document.getElementById('emergency-contact').value.trim();
        if (isNaN(count) || count < 0) { showToast('Life jacket count must be zero or more.', 'error'); return; }
        if (!/^[+0-9][0-9 ()-]{6,19}$/.test(phone)) { showToast('Enter a valid emergency contact number.', 'error'); return; }
        if (!document.getElementById('safety-inspector-name').value.trim()) { showToast('Inspector name is required.', 'error'); return; }
        const payload = {
            lifeJacketsChecked: document.getElementById('lifejackets-check').checked,
            lifeJacketCount: parseInt(document.getElementById('lifejackets-count').value),
            firstAidKitChecked: document.getElementById('firstaid-check').checked,
            emergencyContactNumber: document.getElementById('emergency-contact').value,
            weatherAdvisoryStatus: document.getElementById('weather-status').value,
            inspectorName: document.getElementById('safety-inspector-name').value,
            comments: document.getElementById('safety-comments').value,
            departureApproved: false
        };

        try {
            await API.saveSafetyChecklist(this.currentTripIdForSafety, payload);
            showToast('Safety audit checklist saved!');
            this.renderSafetyChecklist(this.currentTripIdForSafety);
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async approveDeparture() {
        try {
            const inspector = document.getElementById('safety-inspector-name').value || AuthState.currentUser.fullName;
            await API.approveDeparture(this.currentTripIdForSafety, inspector);
            showToast('🚀 Departure Official Clearance Granted!');
            this.renderSafetyChecklist(this.currentTripIdForSafety);
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Maintenance Management View (Create / Read / Update / Delete)
    async renderMaintenanceManagement() {
        try {
            this.maintenanceLogs = await API.getAllMaintenanceLogs();
        } catch (e) {
            this.maintenanceLogs = [];
            showToast(e.message, 'error');
        }
        this.filterMaintenance();
    },

    filterMaintenance() {
        const tbody = document.getElementById('maintenance-logs-table');
        if (!tbody) return;
        const q = (document.getElementById('maint-search').value || '').toLowerCase().trim();
        const f = document.getElementById('maint-status-filter').value;
        const rows = (this.maintenanceLogs || []).filter(l => {
            if (f !== 'ALL' && l.status !== f) return false;
            const text = `${l.boat.name} ${l.boat.registrationNumber} ${l.description} ${l.performedBy || ''}`.toLowerCase();
            return !q || text.includes(q);
        }).sort((x, y) => String(y.maintenanceDate).localeCompare(String(x.maintenanceDate)));

        if (!rows.length) {
            tbody.innerHTML = '<tr><td colspan="7" class="p-6 text-center text-xs text-slate-400">No maintenance logs found.</td></tr>';
            return;
        }
        const badge = {
            COMPLETED: 'bg-emerald-100 text-emerald-800',
            IN_PROGRESS: 'bg-amber-100 text-amber-800',
            SCHEDULED: 'bg-sky-100 text-sky-800'
        };
        tbody.innerHTML = rows.map(l => `
            <tr class="border-b border-slate-100 hover:bg-slate-50">
                <td class="p-3 font-bold text-slate-800 text-xs">${l.boat.name} (${l.boat.registrationNumber})</td>
                <td class="p-3 text-xs text-slate-600">${l.maintenanceDate}</td>
                <td class="p-3 text-xs text-slate-700">${l.description}</td>
                <td class="p-3 text-xs font-semibold text-cyan-700">LKR ${(l.cost||0).toLocaleString()}</td>
                <td class="p-3 text-xs text-slate-600">${l.performedBy || '-'}</td>
                <td class="p-3"><span class="px-2.5 py-1 text-xs font-bold rounded-full whitespace-nowrap ${badge[l.status] || 'bg-slate-200 text-slate-600'}">${String(l.status).replace('_', ' ')}</span></td>
                <td class="p-3"><div class="flex gap-1.5">
                    <button onclick="App.openMaintenanceModal(${l.id})" class="px-2.5 py-1 bg-slate-800 text-white rounded-lg text-xs font-bold">Edit</button>
                    <button onclick="App.deleteMaintenance(${l.id})" class="px-2.5 py-1 bg-rose-600 text-white rounded-lg text-xs font-bold">Delete</button>
                </div></td>
            </tr>`).join('');
    },

    async openMaintenanceModal(id) {
        let boats = [];
        try { boats = await API.getBoats(); } catch (e) { showToast(e.message, 'error'); return; }
        const sel = document.getElementById('maint-boat');
        sel.innerHTML = boats.map(b => `<option value="${b.id}">${b.name} (${b.registrationNumber})</option>`).join('');
        sel.disabled = false;
        document.getElementById('maint-id').value = '';
        document.getElementById('maint-date').value = new Date().toISOString().substring(0, 10);
        document.getElementById('maint-status').value = 'COMPLETED';
        document.getElementById('maint-description').value = '';
        document.getElementById('maint-cost').value = '';
        document.getElementById('maint-by').value = '';
        document.getElementById('maint-modal-title').textContent = '+ Add Maintenance Log';

        if (id) {
            const l = (this.maintenanceLogs || []).find(x => x.id === id);
            if (!l) { showToast('Maintenance log not found.', 'error'); return; }
            document.getElementById('maint-id').value = l.id;
            sel.value = l.boat.id;
            sel.disabled = true;
            document.getElementById('maint-date').value = l.maintenanceDate;
            document.getElementById('maint-status').value = l.status;
            document.getElementById('maint-description').value = l.description || '';
            document.getElementById('maint-cost').value = l.cost ?? '';
            document.getElementById('maint-by').value = l.performedBy || '';
            document.getElementById('maint-modal-title').textContent = 'Edit Maintenance Log';
        }
        document.getElementById('maintenance-modal').classList.remove('hidden');
    },

    async saveMaintenance() {
        const id = document.getElementById('maint-id').value;
        const boatId = document.getElementById('maint-boat').value;
        const date = document.getElementById('maint-date').value;
        const status = document.getElementById('maint-status').value;
        const description = document.getElementById('maint-description').value.trim();
        const costRaw = document.getElementById('maint-cost').value;
        const cost = costRaw === '' ? 0 : parseFloat(costRaw);

        if (!boatId || !date || !description) { showToast('Please fill in all required fields (*).', 'error'); return; }
        if (isNaN(cost) || cost < 0) { showToast('Cost must be zero or more.', 'error'); return; }
        if (status === 'COMPLETED' && date > new Date().toISOString().substring(0, 10)) {
            showToast('A completed service cannot be dated in the future.', 'error');
            return;
        }
        const payload = {
            maintenanceDate: date,
            description: description,
            cost: cost,
            performedBy: document.getElementById('maint-by').value.trim(),
            status: status
        };
        try {
            if (id) {
                await API.updateMaintenance(id, payload);
                showToast('Maintenance log updated.');
            } else {
                await API.addMaintenance(boatId, payload);
                showToast('Maintenance log added.');
            }
            document.getElementById('maintenance-modal').classList.add('hidden');
            await this.renderMaintenanceManagement();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async deleteMaintenance(id) {
        if (!confirm('Delete this maintenance log? This cannot be undone.')) return;
        try {
            await API.deleteMaintenance(id);
            showToast('Maintenance log deleted.');
            await this.renderMaintenanceManagement();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Promotions View
    async renderPromotions() {
        await this.filterVouchers();
    },

    async filterVouchers() {
        const grid = document.getElementById('promotions-list-grid');
        const loading = document.getElementById('promotions-loading');
        const empty = document.getElementById('promotions-empty');
        if (!grid) return;

        const keyword = (document.getElementById('voucher-search-input') || {}).value || '';
        const status = (document.getElementById('voucher-status-filter') || {}).value || 'ALL';

        grid.innerHTML = '';
        loading.classList.remove('hidden');
        empty.classList.add('hidden');

        try {
            const promos = await API.searchPromotions(keyword, status);
            loading.classList.add('hidden');

            if (!promos.length) {
                empty.classList.remove('hidden');
                return;
            }

            // A voucher is closed when it is expired, switched off, or has used up its limit
            const isClosedVoucher = p => (p.computedStatus || p.status || 'ACTIVE') !== 'ACTIVE'
                || (p.usageLimit > 0 && p.usageCount >= p.usageLimit);

            const renderCard = (p) => {
                const closed = isClosedVoucher(p);
                const computed = p.computedStatus || p.status || 'ACTIVE';
                const label = computed !== 'ACTIVE' ? computed
                    : closed ? 'LIMIT REACHED'
                    : new Date(p.validFrom) > new Date() ? 'STARTS ' + p.validFrom
                    : 'ACTIVE';
                const badgeClass = label === 'ACTIVE' ? 'bg-emerald-100 text-emerald-800'
                    : label.startsWith('STARTS') ? 'bg-sky-100 text-sky-800'
                    : computed === 'EXPIRED' ? 'bg-rose-100 text-rose-800'
                    : label === 'LIMIT REACHED' ? 'bg-amber-100 text-amber-800'
                    : 'bg-slate-200 text-slate-600';
                const discountLabel = p.discountType === 'FIXED_AMOUNT' ? `LKR ${p.fixedAmount} OFF` : `${p.discountPercentage}% OFF`;
                const usageLabel = p.usageLimit ? `${p.usageCount} / ${p.usageLimit}` : `${p.usageCount} (unlimited)`;
                const theme = Array.from(String(p.code || '')).reduce((a, ch) => a + ch.charCodeAt(0), 0) % 4;
                return `
                <div class="glass-card voucher-card vt-${theme} p-5 rounded-2xl overflow-hidden ${closed ? 'opacity-75' : ''}">
                    <div class="voucher-strip vs-${theme} -mx-5 -mt-5 mb-3 ${closed ? 'grayscale' : ''}"><span class="voucher-off">${discountLabel}</span></div>
                    <div class="flex items-center justify-between mb-2">
                        <span class="voucher-code font-mono font-extrabold text-lg">${p.code}</span>
                    </div>
                    <p class="text-sm font-bold text-slate-800 mb-1">${p.title || ''}</p>
                    <p class="text-xs text-slate-600 mb-3">${p.description || ''}</p>
                    <div class="flex justify-between items-center text-xs text-slate-400 border-t border-slate-100 pt-2 mb-2">
                        <span>${p.validFrom} → ${p.validUntil}</span>
                        <span>Used: ${usageLabel}</span>
                    </div>
                    <div class="flex items-center justify-between">
                        <span class="px-2.5 py-1 rounded-full text-xs font-bold ${badgeClass}">${label}</span>
                        <div class="flex gap-2">
                            <button onclick="App.openVoucherModal(${p.id})" class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 rounded-lg text-xs font-bold transition">Edit</button>
                            <button onclick="App.deleteVoucher(${p.id})" class="px-3 py-1.5 bg-rose-100 hover:bg-rose-200 text-rose-700 rounded-lg text-xs font-bold transition">Delete</button>
                        </div>
                    </div>
                </div>`;
            };

            const heading = (title, note) => `
                <div class="col-span-full mt-2">
                    <h2 class="text-lg font-extrabold text-slate-700">${title}</h2>
                    <p class="text-xs text-slate-500">${note}</p>
                </div>`;
            const open = promos.filter(p => !isClosedVoucher(p));
            const closedList = promos.filter(isClosedVoucher);
            let html = '';
            if (open.length) html += heading(`Active Vouchers (${open.length})`, 'Customers can use these right now.') + open.map(renderCard).join('');
            if (closedList.length) html += heading(`Expired & Inactive Vouchers (${closedList.length})`, 'These can no longer be used for bookings.') + closedList.map(renderCard).join('');
            grid.innerHTML = html;
        } catch (e) {
            loading.classList.add('hidden');
            showToast(e.message, 'error');
        }
    },

    toggleVoucherDiscountFields() {
        const type = document.getElementById('voucher-discount-type').value;
        document.getElementById('voucher-percentage-wrap').classList.toggle('hidden', type !== 'PERCENTAGE');
        document.getElementById('voucher-fixed-wrap').classList.toggle('hidden', type !== 'FIXED_AMOUNT');
        document.getElementById('voucher-max-discount-wrap').classList.toggle('hidden', type !== 'PERCENTAGE');
    },

    async openVoucherModal(id = null) {
        document.getElementById('voucher-id').value = '';
        document.getElementById('voucher-code').value = '';
        document.getElementById('voucher-title').value = '';
        document.getElementById('voucher-description').value = '';
        document.getElementById('voucher-discount-type').value = 'PERCENTAGE';
        document.getElementById('voucher-discount-percentage').value = '';
        document.getElementById('voucher-fixed-amount').value = '';
        document.getElementById('voucher-max-discount').value = '';
        document.getElementById('voucher-min-booking').value = '';
        document.getElementById('voucher-start-date').value = '';
        document.getElementById('voucher-expiry-date').value = '';
        document.getElementById('voucher-usage-limit').value = '';
        document.getElementById('voucher-status').value = 'ACTIVE';
        this.toggleVoucherDiscountFields();

        const trips = await API.getTrips();
        this.renderTripChecklist('voucher-applicable-trips', trips,
            t => `<strong>${t.route.origin} → ${t.route.destination}</strong><em>${t.tripDate} ${t.departureTime}</em>`,
            'No trips available.');

        if (id) {
            try {
                const p = await API.getPromotion(id);
                document.getElementById('voucher-id').value = p.id;
                document.getElementById('voucher-code').value = p.code || '';
                document.getElementById('voucher-title').value = p.title || '';
                document.getElementById('voucher-description').value = p.description || '';
                document.getElementById('voucher-discount-type').value = p.discountType || 'PERCENTAGE';
                document.getElementById('voucher-discount-percentage').value = p.discountPercentage ?? '';
                document.getElementById('voucher-fixed-amount').value = p.fixedAmount ?? '';
                document.getElementById('voucher-max-discount').value = p.maxDiscount ?? '';
                document.getElementById('voucher-min-booking').value = p.minBookingAmount ?? '';
                document.getElementById('voucher-start-date').value = p.validFrom || '';
                document.getElementById('voucher-expiry-date').value = p.validUntil || '';
                document.getElementById('voucher-usage-limit').value = p.usageLimit ?? '';
                document.getElementById('voucher-status').value = p.status === 'INACTIVE' ? 'INACTIVE' : 'ACTIVE';
                this.toggleVoucherDiscountFields();
                document.getElementById('voucher-modal-title').textContent = 'Edit Voucher';

                const applicableIds = await API.getApplicableTrips(id);
                this.setTripTicks('voucher-applicable-trips', applicableIds);
            } catch (e) {
                showToast(e.message, 'error');
                return;
            }
        } else {
            document.getElementById('voucher-modal-title').textContent = '+ Add New Voucher';
        }

        document.getElementById('voucher-modal').classList.remove('hidden');
    },

    async saveVoucher() {
        const id = document.getElementById('voucher-id').value;
        const type = document.getElementById('voucher-discount-type').value;

        const payload = {
            code: document.getElementById('voucher-code').value.trim().toUpperCase(),
            title: document.getElementById('voucher-title').value.trim(),
            description: document.getElementById('voucher-description').value.trim(),
            discountType: type,
            discountPercentage: type === 'PERCENTAGE' ? parseFloat(document.getElementById('voucher-discount-percentage').value) : null,
            fixedAmount: type === 'FIXED_AMOUNT' ? parseFloat(document.getElementById('voucher-fixed-amount').value) : null,
            maxDiscount: document.getElementById('voucher-max-discount').value ? parseFloat(document.getElementById('voucher-max-discount').value) : null,
            minBookingAmount: document.getElementById('voucher-min-booking').value ? parseFloat(document.getElementById('voucher-min-booking').value) : null,
            validFrom: document.getElementById('voucher-start-date').value,
            validUntil: document.getElementById('voucher-expiry-date').value,
            usageLimit: document.getElementById('voucher-usage-limit').value ? parseInt(document.getElementById('voucher-usage-limit').value) : null,
            status: document.getElementById('voucher-status').value
        };

        if (!payload.code || !payload.title || !payload.validFrom || !payload.validUntil) {
            showToast('Please fill in all required fields (*).', 'error');
            return;
        }

        const selectedTripIds = this.getTickedTripIds('voucher-applicable-trips');

        try {
            let savedPromo;
            if (id) {
                savedPromo = await API.updatePromotion(id, payload);
                showToast('Voucher updated successfully.');
            } else {
                const user = AuthState.currentUser;
                const marketingOfficerId = user && user.role === 'MARKETING_OFFICER' ? user.id : null;
                savedPromo = await API.createPromotion(payload, marketingOfficerId);
                showToast('Voucher created successfully.');
            }
            await API.setApplicableTrips(savedPromo.id, selectedTripIds);
            document.getElementById('voucher-modal').classList.add('hidden');
            this.filterVouchers();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async deleteVoucher(id) {
        if (!confirm('Are you sure you want to delete this voucher?')) return;
        try {
            await API.deletePromotion(id);
            showToast('Voucher removed.');
            this.filterVouchers();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Reviews View
    async renderReviews() {
        const role = AuthState.currentUser ? AuthState.currentUser.role : 'CUSTOMER';
        const isOfficer = role === 'MARKETING_OFFICER' || role === 'ADMIN';
        const wall = document.getElementById('reviews-wall-grid');
        const panel = document.getElementById('reviews-officer-panel');
        const addReviewBtn = document.getElementById('add-review-btn');

        if (isOfficer) {
            if (wall) wall.classList.add('hidden');
            if (panel) panel.classList.remove('hidden');
            if (addReviewBtn) addReviewBtn.classList.add('hidden');
            await this.renderRatingSummary();
            await this.filterReviews();
        } else {
            if (panel) panel.classList.add('hidden');
            if (wall) wall.classList.remove('hidden');
            if (addReviewBtn) addReviewBtn.classList.remove('hidden');
            const reviews = await API.getReviews();
            if (wall) {
                wall.innerHTML = reviews.map(r => `
                    <div class="glass-card p-5 rounded-2xl">
                        <div class="flex items-center justify-between mb-2">
                            <span class="font-bold text-slate-800 text-sm">${r.user.fullName}</span>
                            <span class="text-amber-500 font-bold text-sm">★ ${r.rating}.0 / 5.0</span>
                        </div>
                        <span class="text-xs font-bold text-cyan-700 block mb-2">Trip: ${r.trip.route.origin} → ${r.trip.route.destination}</span>
                        <p class="text-slate-600 text-xs italic">"${r.comment}"</p>
                    </div>
                `).join('');
            }
        }
    },

    async renderRatingSummary() {
        const container = document.getElementById('rating-summary-cards');
        if (!container) return;
        try {
            const summary = await API.getReviewSummary();
            const breakdown = summary.breakdown || {};
            container.innerHTML = `
                <div class="glass-card p-4 rounded-2xl text-center col-span-2 md:col-span-1">
                    <span class="text-xs font-bold text-slate-400 uppercase block mb-1">Average Rating</span>
                    <span class="text-2xl font-extrabold text-amber-600">${summary.averageRating.toFixed(1)} / 5.0</span>
                </div>
                ${[5, 4, 3, 2, 1].map(star => `
                    <div class="glass-card p-4 rounded-2xl text-center">
                        <span class="text-xs font-bold text-slate-400 uppercase block mb-1">${star} ★</span>
                        <span class="text-xl font-extrabold text-slate-800">${breakdown[star] || 0}</span>
                    </div>
                `).join('')}
            `;
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async filterReviews() {
        const grid = document.getElementById('reviews-management-grid');
        const loading = document.getElementById('reviews-loading');
        const empty = document.getElementById('reviews-empty');
        if (!grid) return;

        const keyword = (document.getElementById('review-search-input') || {}).value || '';
        const rating = parseInt((document.getElementById('review-rating-filter') || {}).value || '0');
        const status = (document.getElementById('review-status-filter') || {}).value || 'ALL';

        grid.innerHTML = '';
        loading.classList.remove('hidden');
        empty.classList.add('hidden');

        try {
            const reviews = await API.searchReviews(keyword, rating || null, status);
            loading.classList.add('hidden');

            if (!reviews.length) {
                empty.classList.remove('hidden');
                return;
            }

            const statusColors = {
                NEW: 'bg-cyan-100 text-cyan-800',
                REVIEWED: 'bg-slate-200 text-slate-700',
                HANDLED: 'bg-emerald-100 text-emerald-800',
                FLAGGED: 'bg-rose-100 text-rose-800'
            };

            grid.innerHTML = reviews.map(r => `
                <div class="glass-card p-5 rounded-2xl">
                    <div class="flex items-center justify-between mb-2">
                        <span class="font-bold text-slate-800 text-sm">${r.user.fullName}</span>
                        <span class="text-amber-500 font-bold text-sm">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</span>
                    </div>
                    <span class="text-xs font-bold text-cyan-700 block mb-2">Trip: ${r.trip.route.origin} → ${r.trip.route.destination}</span>
                    <p class="text-slate-600 text-xs italic mb-3">"${r.comment}"</p>
                    <div class="flex justify-between items-center text-xs text-slate-400 border-t border-slate-100 pt-2 mb-3">
                        <span>${new Date(r.reviewDate).toLocaleDateString()}</span>
                        <span class="px-2.5 py-1 rounded-full text-xs font-bold ${statusColors[r.status] || statusColors.NEW}">${r.status || 'NEW'}</span>
                    </div>
                    <div class="flex gap-2">
                        <select onchange="App.updateReviewStatus(${r.id}, this.value)" class="flex-1 px-2 py-1.5 bg-slate-100 rounded-lg text-xs font-bold border-0">
                            <option value="NEW" ${r.status === 'NEW' ? 'selected' : ''}>New</option>
                            <option value="REVIEWED" ${r.status === 'REVIEWED' ? 'selected' : ''}>Reviewed</option>
                            <option value="HANDLED" ${r.status === 'HANDLED' ? 'selected' : ''}>Handled</option>
                            <option value="FLAGGED" ${r.status === 'FLAGGED' ? 'selected' : ''}>Flagged</option>
                        </select>
                        <button onclick="App.deleteFeedback(${r.id})" class="px-3 py-1.5 bg-rose-100 hover:bg-rose-200 text-rose-700 rounded-lg text-xs font-bold transition">Delete</button>
                    </div>
                </div>
            `).join('');
        } catch (e) {
            loading.classList.add('hidden');
            showToast(e.message, 'error');
        }
    },

    async updateReviewStatus(id, status) {
        try {
            await API.updateReviewStatus(id, status);
            showToast('Feedback status updated.');
            await this.filterReviews();
            await this.renderRatingSummary();
        } catch (e) {
            showToast(e.message, 'error');
            this.filterReviews();
        }
    },

    async deleteFeedback(id) {
        if (!confirm('Are you sure you want to delete this feedback? This cannot be undone.')) return;
        try {
            await API.deleteReview(id);
            showToast('Feedback removed.');
            this.filterReviews();
            this.renderRatingSummary();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Home page highlight tiles: description + picture
    featureInfo: {
        whale: {
            tag: 'Mirissa Whale Coast', title: 'Whale &amp; Dolphin Watching',
            text: 'Head out from Mirissa Harbour into the open sea with a certified naturalist guide. Sri Lanka\'s southern coast is known for blue whales, sperm whales and dolphins, and sightings are usually best from about November to April.',
            points: ['Certified seaworthy vessels with life jackets for every guest', 'Naturalist guide on board', 'Early departures for calmer seas', 'Live seat availability and an instant digital boarding pass'],
            cta: 'Browse Whale Trips', go: () => App.navigate('package-listing-view', { origin: 'Mirissa' })
        },
        river: {
            tag: 'Madu River & Bentota', title: 'Mangrove River Tours',
            text: 'Glide through narrow, shaded mangrove tunnels on the Madu River and Bentota estuary. Stop at small river islands such as Kothduwa and look out for birds, monitor lizards and fish along the banks.',
            points: ['Calm water that suits families and first-time visitors', 'Island stops with a local guide', 'Short half-day schedules', 'Small boats with limited seats for a quieter trip'],
            cta: 'Browse River Trips', go: () => App.navigate('package-listing-view', { origin: 'Balapitiya' })
        },
        safety: {
            tag: 'Safety First', title: 'Safety Audited Vessels',
            text: 'No departure sails until our Safety Officer has completed the pre-departure audit. Fleet records for engine status, fuel level and maintenance are kept up to date for every vessel.',
            points: ['Life jacket count and first aid kit checked', 'Emergency contact number confirmed', 'Weather advisory reviewed before clearance', 'Departure is approved or held by the Safety Officer'],
            cta: 'View Departures', go: () => App.navigate('trip-schedule-view')
        },
        vouchers: {
            tag: 'Save on your booking', title: 'Seasonal Vouchers',
            text: 'Enter a voucher code on the seat selection page to reduce your total. The system checks every code against the trip, the dates and your booking amount before applying the discount.',
            points: ['Percentage and fixed-amount offers', 'Each code has a start and an expiry date', 'Some codes apply only to selected trips', 'Your discount is shown before you confirm'],
            cta: 'Find a Trip to Book', go: () => App.navigate('package-listing-view')
        }
    },

    async openFeatureInfo(key) {
        const f = this.featureInfo[key];
        if (!f) return;
        document.getElementById('feature-modal-photo').className = 'feature-photo feature-photo-lg relative feat-' + key;
        document.getElementById('feature-modal-tag').textContent = f.tag;
        document.getElementById('feature-modal-title').innerHTML = f.title;
        document.getElementById('feature-modal-text').textContent = f.text;
        document.getElementById('feature-modal-points').innerHTML = f.points.map(p =>
            `<li class="flex gap-2 text-sm text-slate-700"><span class="text-emerald-600 font-bold">✓</span><span>${p}</span></li>`).join('');
        const extra = document.getElementById('feature-modal-extra');
        extra.innerHTML = '';
        const cta = document.getElementById('feature-modal-cta');
        cta.textContent = f.cta;
        cta.onclick = () => { this.closeFeatureInfo(); f.go(); };
        document.getElementById('feature-modal').classList.remove('hidden');

        // Live voucher codes (shown only if the server allows this role to read them)
        if (key === 'vouchers') {
            try {
                const promos = await API.getPromotions();
                const active = (Array.isArray(promos) ? promos : []).filter(p => (p.computedStatus || p.status) === 'ACTIVE');
                if (active.length) {
                    extra.innerHTML = '<div class="text-[11px] font-bold text-slate-400 uppercase mb-2">Offers available now</div>' +
                        active.map(p => {
                            const off = p.discountType === 'FIXED_AMOUNT' ? `LKR ${p.fixedAmount} off` : `${p.discountPercentage}% off`;
                            return `<span class="inline-block mr-2 mb-2 px-3 py-1 rounded-full bg-amber-100 text-amber-900 text-xs font-bold"><span class="font-mono">${p.code}</span> · ${off}</span>`;
                        }).join('');
                }
            } catch (e) { /* not available for this role: show the description only */ }
        }
    },

    closeFeatureInfo() {
        document.getElementById('feature-modal').classList.add('hidden');
    },

    async openReviewModal() {
        const user = AuthState.currentUser;
        if (!user || user.role !== 'CUSTOMER') return;

        try {
            const bookings = await API.getBookings(user.id);
            const uniqueTrips = [];
            const seen = new Set();
            bookings.forEach(b => {
                const trip = b.trip;
                if (trip && !seen.has(trip.id)) {
                    seen.add(trip.id);
                    uniqueTrips.push(trip);
                }
            });

            this.renderTripChecklist('review-package-select', uniqueTrips,
                t => `<strong>${t.route.origin} → ${t.route.destination}</strong><em>${t.tripDate}</em>`,
                'No booked trips yet');
            if (uniqueTrips.length === 1) this.tickAllTrips('review-package-select', true);
        } catch (e) {
            showToast(e.message, 'error');
            return;
        }

        this.setReviewStar(0);
        document.getElementById('review-comment-input').value = '';
        document.getElementById('review-modal').classList.remove('hidden');
    },

    setReviewStar(value) {
        document.getElementById('review-rating-value').value = value;
        document.querySelectorAll('#review-star-picker span').forEach(star => {
            const starValue = parseInt(star.dataset.star);
            star.classList.toggle('text-amber-500', starValue <= value);
            star.classList.toggle('text-slate-300', starValue > value);
        });
    },

    async submitReview() {
        const user = AuthState.currentUser;
        const tripId = this.getTickedTripIds('review-package-select')[0];
        const rating = parseInt(document.getElementById('review-rating-value').value);
        const comment = document.getElementById('review-comment-input').value.trim();

        if (!tripId) {
            showToast('You need a booking for a trip before you can review it.', 'error');
            return;
        }
        if (!rating) {
            showToast('Please select a star rating.', 'error');
            return;
        }
        if (!comment) {
            showToast('Please write a short review.', 'error');
            return;
        }

        try {
            await API.createReview(user.id, tripId, rating, comment);
            showToast('Thank you for your review!');
            document.getElementById('review-modal').classList.add('hidden');
            this.renderReviews();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Reports View
    async renderReports() {
        const rev = await API.getRevenueReport();
        const summary = await API.getSummaryReport();

        document.getElementById('report-gross-rev').textContent = 'LKR ' + (rev.grossRevenue||0).toLocaleString();
        document.getElementById('report-discounts').textContent = 'LKR ' + (rev.totalDiscounts||0).toLocaleString();
        document.getElementById('report-net-rev').textContent = 'LKR ' + (rev.netRevenue||0).toLocaleString();
    },

    // User Management View
       async renderUserManagement() {
        const users = await API.getUsers();
        const tbody = document.getElementById('users-table-body');
        if (tbody) {
            const myEmail = AuthState.currentUser ? AuthState.currentUser.email : null;
            tbody.innerHTML = users.map(u => `
                <tr class="border-b border-slate-100 hover:bg-slate-50">
                    <td class="p-3 font-bold text-slate-800 text-xs">${u.fullName}</td>
                    <td class="p-3 text-xs text-slate-600">${u.email}</td>
                    <td class="p-3 font-mono text-xs text-slate-600">${u.phoneNumber}</td>
                    <td class="p-3 text-xs font-bold text-cyan-700">${u.role}</td>
                    <td class="p-3"><span class="px-2 py-0.5 text-xs rounded-full ${u.status === 'ACTIVE' ? 'badge-confirmed' : 'badge-cancelled'}">${u.status}</span></td>
                    <td class="p-3">
                        ${u.email === myEmail ? '<span class="text-xs text-slate-400 italic">Current user</span>' : `<button onclick="App.toggleUserStatus(${u.id}, '${u.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE'}')" class="px-2.5 py-1 text-xs font-medium rounded-lg ${u.status === 'ACTIVE' ? 'bg-rose-600 hover:bg-rose-700' : 'bg-emerald-600 hover:bg-emerald-700'} text-white">
                            ${u.status === 'ACTIVE' ? 'Suspend' : 'Activate'}
                        </button>`}
                    </td>
                </tr>
            `).join('');
        }
    },

    async toggleUserStatus(id, newStatus) {
        if (!confirm(`Are you sure you want to set this user's status to ${newStatus}?`)) return;
        try {
            await API.updateUserStatus(id, newStatus);
            showToast(`User status updated to ${newStatus}.`);
            this.renderUserManagement();
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    // Actions
    async cancelBooking(bookingId) {
        if (!confirm('Are you sure you want to cancel this boat safari booking?')) return;
        try {
            await API.cancelBooking(bookingId);
            showToast('Booking cancelled.');
            this.loadViewData(this.currentView, {});
        } catch (e) {
            showToast(e.message, 'error');
        }
    },

    async deleteBoat(boatId) {
        if (!confirm('Are you sure you want to delete this boat record?')) return;
        try {
            await API.deleteBoat(boatId);
            showToast('Boat record deleted.');
            this.renderBoatManagement();
        } catch (e) {
            showToast(e.message, 'error');
        }
    }
};

function showToast(msg, type = 'success') {
    const toast = document.createElement('div');
    toast.className = `fixed bottom-5 right-5 z-50 px-5 py-3 rounded-xl shadow-2xl text-white font-medium text-sm flex items-center gap-2 transform transition-all duration-300 ${type === 'error' ? 'bg-rose-600' : 'bg-emerald-600'}`;
    toast.innerHTML = `<span>${type === 'error' ? '⚠️' : '✅'}</span> ${msg}`;
    document.body.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('opacity-0', 'translate-y-2');
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

window.App = App;
window.showToast = showToast;

document.addEventListener('DOMContentLoaded', () => App.init());
