const API_URL = '';

const auth = {
    token: localStorage.getItem('auth_token'),
    user: null,

    async login(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const data = Object.fromEntries(formData);
        
        try {
            const response = await fetch(`${API_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                const token = await response.text();
                this.token = token;
                localStorage.setItem('auth_token', token);
                
                // Get user info with the new token
                const userResponse = await fetch(`${API_URL}/auth/me`, {
                    headers: { 'X-Auth-Token': token }
                });
                this.user = await userResponse.json();

                ui.showToast('Successfully logged in');
                ui.closeModal('login-modal');
                ui.updateAuthView();
            } else {
                ui.showToast('Invalid credentials', 'error');
            }
        } catch (err) {
            ui.showToast('Login failed. Server unreachable.', 'error');
        }
    },

    async register(event) {
        event.preventDefault();
        const formData = new FormData(event.target);
        const data = Object.fromEntries(formData);

        try {
            const response = await fetch(`${API_URL}/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                ui.showToast('Account created! Please login.');
                ui.closeModal('register-modal');
                ui.showModal('login-modal');
            } else {
                const msg = await response.text();
                ui.showToast(msg, 'error');
            }
        } catch (err) {
            ui.showToast('Registration failed.', 'error');
        }
    },

    async logout() {
        try {
            await fetch(`${API_URL}/auth/logout`, {
                method: 'POST',
                headers: { 'X-Auth-Token': this.token }
            });
        } catch (err) {
            console.error('Logout error', err);
        }
        this.token = null;
        this.user = null;
        localStorage.removeItem('auth_token');
        ui.updateAuthView();
        ui.showToast('Logged out');
    }
};

const app = {
    cars: [],

    async loadCars() {
        const city = document.getElementById('city-filter').value;
        const url = city ? `${API_URL}/car?city=${city}` : `${API_URL}/car`;
        
        try {
            const response = await fetch(url);
            this.cars = await response.json();
            this.renderCars();
        } catch (err) {
            console.error('Failed to load cars', err);
        }
    },

    renderCars() {
        const grid = document.getElementById('car-grid');
        if (this.cars.length === 0) {
            grid.innerHTML = '<div class="loader">No cars available for selected criteria.</div>';
            return;
        }

        grid.innerHTML = this.cars.map(car => `
            <div class="car-card fade-in">
                <div class="car-image" style="background-image: url('${car.imageUrl || 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=800'}')"></div>
                <div class="car-info">
                    <div class="car-brand">${car.brand}</div>
                    <div class="car-model">${car.model} (${car.productionYear})</div>
                    <div class="car-details">
                        <span>${car.cityBased || 'Main Hub'}</span>
                        <div class="car-price">$${car.dailyPrice || 99}<span>/day</span></div>
                    </div>
                    <button onclick="app.openRentModal(${car.id}, '${car.brand} ${car.model}')" class="btn btn-primary full-width">Rent This Car</button>
                </div>
            </div>
        `).join('');
    },

    async openRentModal(id, name) {
        if (!auth.token) {
            ui.showToast('Please login to book a car', 'error');
            ui.showModal('login-modal');
            return;
        }
        document.getElementById('rent-car-id').value = id;
        document.getElementById('rent-car-info').innerText = `Booking: ${name}`;
        
        // Fetch booked dates
        const container = document.getElementById('booked-dates-container');
        container.innerHTML = '<div class="loader-sm">Checking busy dates...</div>';
        
        ui.showModal('rent-modal');

        try {
            const response = await fetch(`${API_URL}/rentals/car/${id}`);
            const rentals = await response.json();
            
            if (rentals.length === 0) {
                container.innerHTML = '<span class="status-available">All dates available</span>';
            } else {
                const datesHtml = rentals.map(r => `
                    <div class="booked-item">
                        <span>${r.startDate}</span> <i class="to-arrow">→</i> <span>${r.endDate}</span>
                    </div>
                `).join('');
                container.innerHTML = '<strong>Already Reserved:</strong>' + datesHtml;
            }
        } catch (err) {
            container.innerHTML = '<span class="status-error">Could not load schedule</span>';
        }
    },

    async confirmRental(event) {
        event.preventDefault();
        const carId = document.getElementById('rent-car-id').value;
        const startDate = document.getElementById('start-date').value;
        const endDate = document.getElementById('end-date').value;

        try {
            const response = await fetch(`${API_URL}/rentals/rent`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Auth-Token': auth.token
                },
                body: JSON.stringify({ carId, startDate, endDate })
            });

            if (response.ok) {
                ui.showToast('Booking success! We will contact you soon.');
                ui.closeModal('rent-modal');
                this.loadCars(); // reload to show updated status if needed
            } else {
                const errorData = await response.json().catch(() => ({}));
                const message = errorData.message || 'Booking failed. Check dates or availability.';
                ui.showToast(message, 'error');
            }
        } catch (err) {
            ui.showToast('Server error during booking.', 'error');
        }
    }
};

const ui = {
    showModal(id) {
        document.getElementById(id).style.display = 'block';
    },

    closeModal(id) {
        document.getElementById(id).style.display = 'none';
    },

    showToast(message, type = 'success') {
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        toast.innerText = message;
        document.body.appendChild(toast);
        
        // Simple dynamic styling for toast
        Object.assign(toast.style, {
            position: 'fixed',
            bottom: '20px',
            right: '20px',
            padding: '1rem 2rem',
            borderRadius: '12px',
            background: type === 'success' ? '#28a745' : '#dc3545',
            color: 'white',
            zIndex: '3000',
            boxShadow: '0 10px 30px rgba(0,0,0,0.5)',
            animation: 'fadeIn 0.3s ease'
        });

        setTimeout(() => toast.remove(), 3000);
    },

    updateAuthView() {
        const authLinks = document.getElementById('auth-links');
        const userProfile = document.getElementById('user-profile');
        const usernameDisplay = document.getElementById('username-display');

        if (auth.token) {
            authLinks.classList.add('hidden');
            userProfile.classList.remove('hidden');
            // Extract username from token if user object is not available yet
            usernameDisplay.innerText = auth.user ? auth.user.username : 'User';
        } else {
            authLinks.classList.remove('hidden');
            userProfile.classList.add('hidden');
        }
    }
};

// Tooltip/Init
document.addEventListener('DOMContentLoaded', () => {
    app.loadCars();
    ui.updateAuthView();
});
