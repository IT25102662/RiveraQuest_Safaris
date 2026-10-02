// Auth & Role Session Manager for Boat Safari Management System

const ROLE_DASHBOARDS = {
    CUSTOMER: 'home-view',
    DESK_OFFICER: 'desk-dashboard-view',
    FLEET_MANAGER: 'fleet-dashboard-view',
    SAFETY_OFFICER: 'safety-dashboard-view',
    MARKETING_OFFICER: 'marketing-dashboard-view',
    ADMIN: 'admin-dashboard-view'
};

const DEFAULT_USERS = {
    CUSTOMER: { id: 1, fullName: "John Smith", email: "tourist@gmail.com", role: "CUSTOMER" },    DESK_OFFICER: { id: 2, fullName: "Nimali Silva", email: "desk@boatsafari.lk", role: "DESK_OFFICER" },
    FLEET_MANAGER: { id: 3, fullName: "Captain Ruwan", email: "fleet@boatsafari.lk", role: "FLEET_MANAGER" },
    SAFETY_OFFICER: { id: 4, fullName: "Dhammika Jayawardena", email: "safety@boatsafari.lk", role: "SAFETY_OFFICER" },
    MARKETING_OFFICER: { id: 5, fullName: "Chathuri Wickramasinghe", email: "marketing@boatsafari.lk", role: "MARKETING_OFFICER" },
    ADMIN: { id: 1, fullName: "Kasun Perera", email: "admin@boatsafari.lk", role: "ADMIN" }
};

const AuthState = {
    currentUser: null,

    init() {
        const stored = localStorage.getItem('safari_session');
        if (stored) {
            try { this.currentUser = JSON.parse(stored); } catch (e) { this.currentUser = DEFAULT_USERS.CUSTOMER; }
        } else {
            this.currentUser = DEFAULT_USERS.CUSTOMER;
            localStorage.setItem('safari_session', JSON.stringify(this.currentUser));
        }
        this.updateWorkspaceInfo();
    },

    setUserSession(user) {
        this.currentUser = user;
        localStorage.setItem('safari_session', JSON.stringify(user));
        this.updateWorkspaceInfo();
    },

    async loginWithCredentials(email, password) {
        try {
            const res = await API.login({ email, password });
            const sessionUser = {
                id: res.id,
                fullName: res.fullName,
                email: res.email,
                role: res.role,
                status: res.status,
                token: res.token
            };
            this.setUserSession(sessionUser);
            showToast(`Welcome back, ${res.fullName}! Authenticated as ${res.role}`);
            
            // Redirect to designated role dashboard
            const targetDashboard = ROLE_DASHBOARDS[res.role] || 'home-view';
            if (window.App) {
                window.App.navigate(targetDashboard);
            }
            return res;
        } catch (err) {
            showToast(err.message, 'error');
            throw err;
        }
    },

    logout() {
        this.setUserSession(DEFAULT_USERS.CUSTOMER);
        showToast("Logged out successfully");
        if (window.App) {
            window.App.navigate('login-view');
        }
    },

    getDashboardForRole(role) {
        return ROLE_DASHBOARDS[role] || 'home-view';
    },

    updateWorkspaceInfo() {
        const role = this.currentUser ? this.currentUser.role : 'CUSTOMER';
        const fullName = this.currentUser ? this.currentUser.fullName : 'Guest';

        // Update workspace headers & sidebars dynamically
        document.querySelectorAll('.logged-user-name').forEach(el => el.textContent = fullName);
        document.querySelectorAll('.logged-user-role').forEach(el => el.textContent = role.replace('_', ' '));

        // Toggle Workspace Visibility - Each stakeholder has their own dedicated workspace container!
        document.querySelectorAll('.stakeholder-workspace').forEach(ws => {
            const wsRole = ws.getAttribute('data-workspace-role');
            if (wsRole === role) {
                ws.classList.remove('hidden');
            } else {
                ws.classList.add('hidden');
            }
        });
    }
};

window.AuthState = AuthState;
