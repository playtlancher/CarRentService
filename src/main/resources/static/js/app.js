const API_URL = '';

const auth = {
    token: localStorage.getItem('auth_token'),
    user: null,

    async login(event) {
        event.preventDefault();
        ui.setAuthError('login-error', '');
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
                
                const userResponse = await fetch(`${API_URL}/auth/me`, {
                    headers: { 'X-Auth-Token': token }
                });
                this.user = await userResponse.json();

                ui.showToast('Successfully logged in');
                ui.closeModal('login-modal');
                ui.updateAuthView();
            } else {
                const msg = await response.text().then(t => t || 'Invalid credentials');
                ui.setAuthError('login-error', msg);
                ui.showToast(msg, 'error');
            }
        } catch (err) {
            const msg = 'Login failed. Server unreachable.';
            ui.setAuthError('login-error', msg);
            ui.showToast(msg, 'error');
        }
    },

    async register(event) {
        event.preventDefault();
        ui.setAuthError('register-error', '');
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
                const msg = await response.text().then(t => t || 'Registration failed');
                ui.setAuthError('register-error', msg);
                ui.showToast(msg, 'error');
            }
        } catch (err) {
            const msg = 'Registration failed. Server unreachable.';
            ui.setAuthError('register-error', msg);
            ui.showToast(msg, 'error');
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
    map: null,
    mapMarkers: [],

    showSection(sectionId) {
        ['cars', 'my-rentals', 'admin'].forEach(id => {
            const el = document.getElementById('section-' + id);
            if (el) el.classList.toggle('hidden', id !== sectionId);
        });

        const hero = document.querySelector('.hero');
        if (hero) hero.classList.toggle('hidden', sectionId !== 'cars');

        document.querySelectorAll('.nav-links a').forEach(link => {
            const onclick = link.getAttribute('onclick');
            if (onclick && onclick.includes(`'${sectionId}'`)) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        if (sectionId === 'cars') {
            this.renderCars();
            this.renderMap();
        }
        if (sectionId === 'my-rentals') this.loadMyRentals();
        if (sectionId === 'admin') this.loadAdminCars();
    },

    async loadCars() {
        const city = document.getElementById('city-filter').value;
        const url = city ? `${API_URL}/car?city=${city}` : `${API_URL}/car`;
        try {
            const response = await fetch(url);
            this.cars = await response.json();
            this.renderCars();
            this.renderMap();
        } catch (err) {
            console.error('Failed to load cars', err);
        }
    },

    renderMap() {
        const container = document.getElementById('map-container');
        if (!container) return;

        if (this.map) {
            this.map.remove();
            this.map = null;
        }

        this.mapMarkers.forEach(m => { if (m && m.remove) m.remove(); });
        this.mapMarkers = [];

        const withCoords = this.cars.filter(c => c.latitude != null && c.longitude != null);
        if (withCoords.length === 0) {
            container.innerHTML = '<p class="loader">No car locations to show. Add latitude/longitude in Admin.</p>';
            return;
        }
        container.innerHTML = '';
        const map = L.map(container).setView([withCoords[0].latitude, withCoords[0].longitude], 10);
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { attribution: '© OpenStreetMap' }).addTo(map);
        withCoords.forEach(car => {
            const m = L.marker([car.latitude, car.longitude]).addTo(map)
                .bindPopup(`<b>${car.brand} ${car.model}</b><br>${car.cityBased || ''}`);
            this.mapMarkers.push(m);
        });
        if (withCoords.length > 1) {
            map.fitBounds(withCoords.map(c => [c.latitude, c.longitude]), { padding: [20, 20] });
        }
        setTimeout(() => map.invalidateSize(), 100);
        this.map = map;
    },

    renderCars() {
        const grid = document.getElementById('car-grid');
        if (!grid) return;
        if (this.cars.length === 0) {
            grid.innerHTML = '<div class="loader">No cars available for selected criteria.</div>';
            return;
        }
        grid.innerHTML = this.cars.map(car => `
            <div class="car-card fade-in">
                <div class="car-image" style="background-image: url('${(car.imageUrl || 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=800').replace(/'/g, "\\'")}')"></div>
                <div class="car-info">
                    <div class="car-brand">${car.brand}</div>
                    <div class="car-model">${car.model} (${car.productionYear})</div>
                    <div class="car-details">
                        <span>${car.cityBased || 'Main Hub'}</span>
                        <div class="car-price">$${car.dailyPrice || 99}<span>/day</span></div>
                    </div>
                    <button onclick="app.openRentModal(${car.id}, '${(car.brand + ' ' + car.model).replace(/'/g, "\\'")}')" class="btn btn-primary full-width">Rent This Car</button>
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
            const response = await fetch(`${API_URL}/rentals/car/${id}`, {
                headers: { 'X-Auth-Token': auth.token }
            });
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
                body: JSON.stringify({ carId: Number(carId), startDate, endDate })
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
    },

    async loadMyRentals() {
        const list = document.getElementById('my-rentals-list');
        if (!list) return;
        if (!auth.token) { list.innerHTML = '<p class="loader">Please log in to see your rentals.</p>'; return; }
        list.innerHTML = '<div class="loader">Loading...</div>';
        try {
            const [rentalsRes, carsRes] = await Promise.all([
                fetch(`${API_URL}/rentals/history`, { headers: { 'X-Auth-Token': auth.token } }),
                fetch(`${API_URL}/car`)
            ]);
            if (!rentalsRes.ok) { list.innerHTML = '<p class="status-error">Failed to load rentals.</p>'; return; }
            const rentals = await rentalsRes.json();
            const cars = await carsRes.json();
            const carMap = Object.fromEntries(cars.map(c => [c.id, c]));
            if (rentals.length === 0) {
                list.innerHTML = '<p class="loader">You have no rentals yet.</p>';
                return;
            }
            list.innerHTML = rentals.map(r => {
                const car = carMap[r.carId] || {};
                return `<div class="rental-card">
                    <div>
                        <strong>${car.brand || ''} ${car.model || ''}</strong> (${r.startDate} → ${r.endDate})<br>
                        <span class="text-dim">$${r.totalPrice || '-'} · ${r.status}</span>
                    </div>
                </div>`;
            }).join('');
        } catch (err) {
            list.innerHTML = '<p class="status-error">Could not load rentals.</p>';
        }
    },

    async loadAdminCars() {
        const list = document.getElementById('admin-cars-list');
        if (!list) return;
        if (!auth.token || (auth.user && auth.user.role !== 'ADMIN')) {
            list.innerHTML = '<p class="loader">Admin only.</p>';
            return;
        }
        list.innerHTML = '<div class="loader">Loading...</div>';
        try {
            const r = await fetch(`${API_URL}/car`, { headers: { 'X-Auth-Token': auth.token } });
            const cars = await r.json();
            this.cars = cars;
            if (cars.length === 0) { list.innerHTML = '<p class="loader">No cars. Add one above.</p>'; return; }
            list.innerHTML = cars.map(car => `
                <div class="car-card fade-in">
                    <div class="car-image" style="background-image: url('${(car.imageUrl || 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&q=80&w=800').replace(/'/g, "\\'")}')"></div>
                    <div class="car-info" style="flex:1">
                        <div class="car-brand">${car.brand} ${car.model}</div>
                        <div class="car-details">${car.cityBased || ''} · $${car.dailyPrice || 0}/day</div>
                        <div style="margin-top:0.5rem">
                            <button onclick="app.editCar(${car.id})" class="btn btn-outline" style="margin-right:0.5rem">Edit</button>
                            <button onclick="app.deleteCar(${car.id})" class="btn btn-secondary">Delete</button>
                        </div>
                    </div>
                </div>
            `).join('');
        } catch (err) {
            list.innerHTML = '<p class="status-error">Failed to load cars.</p>';
        }
    },

    editCar(id) {
        const car = this.cars.find(c => c.id === id) || {};
        document.getElementById('admin-car-id').value = id;
        document.getElementById('admin-brand').value = car.brand || '';
        document.getElementById('admin-model').value = car.model || '';
        document.getElementById('admin-vinCode').value = car.vinCode || '';
        document.getElementById('admin-cityBased').value = car.cityBased || '';
        document.getElementById('admin-productionYear').value = car.productionYear || '';
        document.getElementById('admin-dailyPrice').value = car.dailyPrice || '';
        document.getElementById('admin-imageUrl').value = car.imageUrl || '';
        document.getElementById('admin-description').value = car.description || '';
        document.getElementById('admin-latitude').value = car.latitude != null ? car.latitude : '';
        document.getElementById('admin-longitude').value = car.longitude != null ? car.longitude : '';

        const formWrap = document.querySelector('.admin-form-wrap');
        if (formWrap) {
            formWrap.scrollIntoView({ behavior: 'smooth', block: 'center' });
            formWrap.style.outline = '2px solid var(--primary-color)';
            setTimeout(() => formWrap.style.outline = 'none', 2000);
        }
    },

    async deleteCar(id) {
        if (!confirm('Delete this car?')) return;
        try {
            const r = await fetch(`${API_URL}/car/${id}`, {
                method: 'DELETE',
                headers: { 'X-Auth-Token': auth.token }
            });
            if (r.ok) { ui.showToast('Car deleted.'); this.loadAdminCars(); this.loadCars(); }
            else ui.showToast('Delete failed.', 'error');
        } catch (err) { ui.showToast('Delete failed.', 'error'); }
    },

    async saveCar(event) {
        event.preventDefault();
        const id = document.getElementById('admin-car-id').value;
        const payload = {
            brand: document.getElementById('admin-brand').value,
            model: document.getElementById('admin-model').value,
            vinCode: document.getElementById('admin-vinCode').value || null,
            cityBased: document.getElementById('admin-cityBased').value || null,
            productionYear: parseInt(document.getElementById('admin-productionYear').value, 10),
            dailyPrice: parseFloat(document.getElementById('admin-dailyPrice').value) || 0,
            imageUrl: document.getElementById('admin-imageUrl').value || null,
            description: document.getElementById('admin-description').value || null,
            latitude: document.getElementById('admin-latitude').value ? parseFloat(document.getElementById('admin-latitude').value) : null,
            longitude: document.getElementById('admin-longitude').value ? parseFloat(document.getElementById('admin-longitude').value) : null
        };
        const url = id ? `${API_URL}/car/${id}` : `${API_URL}/car`;
        const method = id ? 'PUT' : 'POST';
        try {
            const r = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json', 'X-Auth-Token': auth.token },
                body: JSON.stringify(payload)
            });
            if (r.ok) {
                ui.showToast('Car saved.');
                document.getElementById('admin-car-form').reset();
                document.getElementById('admin-car-id').value = '';
                this.loadAdminCars();
                this.loadCars();
            } else {
                ui.showToast('Save failed.', 'error');
            }
        } catch (err) {
            ui.showToast('Save failed.', 'error');
        }
    }
};

const ui = {
    showModal(id) {
        if (id === 'login-modal') ui.setAuthError('login-error', '');
        if (id === 'register-modal') ui.setAuthError('register-error', '');
        document.getElementById(id).style.display = 'block';
    },

    closeModal(id) {
        document.getElementById(id).style.display = 'none';
    },

    setAuthError(elementId, message) {
        const el = document.getElementById(elementId);
        if (!el) return;
        el.textContent = message;
        el.classList.toggle('hidden', !message);
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
        const navMyRentals = document.getElementById('nav-my-rentals');
        const navAdmin = document.getElementById('nav-admin');

        if (auth.token) {
            authLinks.classList.add('hidden');
            userProfile.classList.remove('hidden');
            const name = (auth.user && auth.user.username) ? auth.user.username : 'User';
            usernameDisplay.innerText = name;
            if (navMyRentals) navMyRentals.classList.remove('hidden');
            if (navAdmin) navAdmin.classList.toggle('hidden', !(auth.user && auth.user.role === 'ADMIN'));
        } else {
            authLinks.classList.remove('hidden');
            userProfile.classList.add('hidden');
            if (navMyRentals) navMyRentals.classList.add('hidden');
            if (navAdmin) navAdmin.classList.add('hidden');
        }
    }
};

// Load current user when token exists (e.g. after refresh)
async function initAuth() {
    if (auth.token) {
        try {
            const r = await fetch(`${API_URL}/auth/me`, { headers: { 'X-Auth-Token': auth.token } });
            if (r.ok) auth.user = await r.json();
            else auth.token = null;
        } catch (_) { auth.token = null; }
    }
    ui.updateAuthView();
}

// Tooltip/Init
document.addEventListener('DOMContentLoaded', () => {
    initAuth().then(() => { app.loadCars(); });
});
