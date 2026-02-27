

// Mock Database i create in order to test the app for now. we can keep those guys in the db latter on
//also for the student in the db, s for the sef de grupe, we need to know his group, bc basically thats how on the time table page we see 
//our group`s schedule + contact data for sef de grupe
const mockDB = {
    users: [
        {
            id: 1,
            email: 'admin@unimate.ro',
            password: 'admin123',
            name: 'Admin User',
            group: 'ADMIN',
            type: 'admin',
            photo: 'https://via.placeholder.com/150' //we will put here the photo or easier will have no photo for proile - just user emojy
        },
        {
            id: 2,
            email: 'student@unimate.ro',
            password: 'student123',
            name: 'John Student',
            group: '1231EB',
            type: 'student',
            photo: 'https://via.placeholder.com/150',
            sefEmail: 'sef@unimate.ro'
        },
        {
            id: 3,
            email: 'sef@unimate.ro',
            password: 'sef123',
            name: 'Maria Sef',
            group: '1231EB',
            type: 'sef',
            photo: 'https://via.placeholder.com/150',
            sefEmail: 'sef@unimate.ro'
        }
    ],
    notes: [
        { id: 1, userId: 2, title: 'Math Notes', content: 'Integration techniques...', collection: 'Math' },
        { id: 2, userId: 2, title: 'Physics Lab', content: 'Experiment results...', collection: 'Physics' }
    ],
    tasks: [
        { id: 1, userId: 2, title: 'Study for Exam', column: 'todo', date: '2026-03-15' },
        { id: 2, userId: 2, title: 'Complete Project', column: 'inprogress', date: '2026-03-20' }
    ],
    quotes: [
        "The only way to do great work is to love what you do. - Prof. Popescu",
        "Success is not final, failure is not fatal. - Prof. Ionescu",
        "Education is the most powerful weapon. - Prof. Georgescu"
    ]
};


let currentUser = null;
let currentLang = 'en';
let currentTheme = 'light';


const translations = {
    en: {
        login: 'Login', signup: 'Sign Up', profile: 'My Profile', logout: 'Logout',
        faculty: 'FILS', links: 'Links', professors: 'Professors', timetable: 'Timetable',
        notes: 'Notes', kanban: 'Kanban', admin: 'Admin Panel', weather: 'Weather',
        quote: 'Quote of the Day', welcome: 'Welcome to UniMate',
        description: 'Your all-in-one student assistant platform',
        campusMap: 'Campus Map', news: 'Faculty News', email: 'Email', password: 'Password',
        group: 'Group', name: 'Name', save: 'Save', cancel: 'Cancel', delete: 'Delete',
        edit: 'Edit', add: 'Add', todo: 'To Do', inprogress: 'In Progress', done: 'Done'
    },
    de: {
        login: 'Anmelden', signup: 'Registrieren', profile: 'Mein Profil', logout: 'Abmelden',
        faculty: 'FILS', links: 'Links', professors: 'Professoren', timetable: 'Stundenplan',
        notes: 'Notizen', kanban: 'Kanban', admin: 'Admin-Panel', weather: 'Wetter',
        quote: 'Zitat des Tages', welcome: 'Willkommen bei UniMate',
        description: 'Ihre All-in-One-Studentenplattform',
        campusMap: 'Campus-Karte', news: 'Fakultätsnachrichten', email: 'E-Mail', password: 'Passwort',
        group: 'Gruppe', name: 'Name', save: 'Speichern', cancel: 'Abbrechen', delete: 'Löschen',
        edit: 'Bearbeiten', add: 'Hinzufügen', todo: 'Zu Erledigen', inprogress: 'In Bearbeitung', done: 'Erledigt'
    },
    fr: {
        login: 'Connexion', signup: "S'inscrire", profile: 'Mon Profil', logout: 'Déconnexion',
        faculty: 'FILS', links: 'Liens', professors: 'Professeurs', timetable: 'Emploi du Temps',
        notes: 'Notes', kanban: 'Kanban', admin: 'Panneau Admin', weather: 'Météo',
        quote: 'Citation du Jour', welcome: 'Bienvenue sur UniMate',
        description: 'Votre plateforme étudiante tout-en-un',
        campusMap: 'Plan du Campus', news: 'Nouvelles de la Faculté', email: 'Email', password: 'Mot de Passe',
        group: 'Groupe', name: 'Nom', save: 'Enregistrer', cancel: 'Annuler', delete: 'Supprimer',
        edit: 'Modifier', add: 'Ajouter', todo: 'À Faire', inprogress: 'En Cours', done: 'Terminé'
    }
};


document.addEventListener('DOMContentLoaded', () => {
    initializeAuth();
    initializeTheme();
    initializeLanguage();
    initializeSidebar(); 
    initializeBottomPanel();
    fetchWeather();
    protectAuthLinks();
    updateUI();
});


function initializeAuth() {
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
    }
}

function login(email, password) {
    const user = mockDB.users.find(u => u.email === email && u.password === password);
    if (user) {
        currentUser = user;
        if (window.UniMate) window.UniMate.currentUser = currentUser;
        localStorage.setItem('currentUser', JSON.stringify(user));
        updateUI();
        window.location.href = 'index.html';
        return true;
    }
    return false;
}

function logout() {
    currentUser = null;
    if (window.UniMate) window.UniMate.currentUser = null;
    localStorage.removeItem('currentUser');
    updateUI();
    window.location.href = 'index.html';
}


function initializeTheme() {
    const savedTheme = localStorage.getItem('theme') || 'light';
    currentTheme = savedTheme;
    document.documentElement.setAttribute('data-theme', savedTheme);
}

function toggleTheme() {
    currentTheme = currentTheme === 'light' ? 'dark' : 'light';
    document.documentElement.setAttribute('data-theme', currentTheme);
    localStorage.setItem('theme', currentTheme);
}


function initializeLanguage() {
    const savedLang = localStorage.getItem('language') || 'en';
    currentLang = savedLang;
    const langSelect = document.getElementById('langSelect');
    if (langSelect) langSelect.value = savedLang;
}

function changeLanguage(lang) {
    currentLang = lang;
    localStorage.setItem('language', lang);
    updateUIText();
}

function t(key) {
    return translations[currentLang][key] || key;
}

function updateUIText() {
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        if (element.tagName === 'INPUT' || element.tagName === 'BUTTON') {
            element.value = t(key);
        } else {
            element.textContent = t(key);
        }
    });
}


function initializeSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    const menuToggle = document.querySelector('.menu-toggle');
    const sidebarClose = document.querySelector('.sidebar-close');

 
    if (sidebar) sidebar.classList.remove('active');
    if (overlay) overlay.classList.remove('active');

  
    if (menuToggle) {
        menuToggle.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (sidebar) sidebar.classList.add('active');
            if (overlay) overlay.classList.add('active');
        });
    }


    if (sidebarClose) {
        sidebarClose.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            if (sidebar) sidebar.classList.remove('active');
            if (overlay) overlay.classList.remove('active');
        });
    }

  
    if (overlay) {
        overlay.addEventListener('click', () => {
            if (sidebar) sidebar.classList.remove('active');
            overlay.classList.remove('active');
        });
    }

    
    if (sidebar) {
        sidebar.querySelectorAll('a[href]').forEach(link => {
            link.addEventListener('click', () => {
                if (sidebar) sidebar.classList.remove('active');
                if (overlay) overlay.classList.remove('active');
            });
        });
    }
}


function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (sidebar) sidebar.classList.toggle('active');
    if (overlay) overlay.classList.toggle('active');
}


function initializeBottomPanel() {
    const panel = document.querySelector('.bottom-panel');
    const toggle = document.querySelector('.panel-toggle');
    
    if (toggle) {
        toggle.addEventListener('click', () => {
            panel.classList.toggle('collapsed');
            const arrow = toggle.querySelector('.arrow');
            if (arrow) {
                arrow.textContent = panel.classList.contains('collapsed') ? '▼' : '▲';
            }
        });
    }
}

// api for weather
function fetchWeather() {
    const url = 'https://api.open-meteo.com/v1/forecast?latitude=44.4268&longitude=26.1025&current_weather=true';
    fetch(url)
        .then(resp => resp.json())
        .then(data => {
            if (data && data.current_weather) {
                const temp = data.current_weather.temperature;
                const el = document.getElementById('weatherTemp');
                if (el) el.textContent = temp;
                const updatedEl = document.getElementById('weatherUpdated');
                if (updatedEl) {
                    const now = new Date();
                    updatedEl.textContent = 'Updated: ' + now.toLocaleTimeString();
                }
            }
        })
        .catch(err => console.error('Weather fetch failed', err));
}


function protectAuthLinks() {
    document.querySelectorAll('a.requires-auth').forEach(link => {
        link.addEventListener('click', e => {
            if (!currentUser) {
                e.preventDefault();
                window.location.href = 'login.html';
            }
        });
    });
}

function updateUI() {
    updateAuthButtons();
    updateSidebar();
    updateAdminAccess();
    updateSefAccess();
    updateAuthLinks();
}

function updateAuthLinks() {
    const authItems = document.querySelectorAll('.auth-only');
    authItems.forEach(el => {
        if (currentUser) {
            el.classList.remove('hidden');
        } else {
            el.classList.add('hidden');
        }
    });
}

function updateAuthButtons() {
    const loginBtn = document.getElementById('loginBtn');
    const profileBtn = document.getElementById('profileBtn');
    const userNameDisplay = document.getElementById('userNameDisplay');
    
    if (currentUser) {
        if (loginBtn) loginBtn.classList.add('hidden');
        if (profileBtn) {
            profileBtn.classList.remove('hidden');
            profileBtn.textContent = t('profile');
        }
        if (userNameDisplay) {
            userNameDisplay.textContent = currentUser.name;
            userNameDisplay.classList.remove('hidden');
        }
    } else {
        if (loginBtn) loginBtn.classList.remove('hidden');
        if (profileBtn) profileBtn.classList.add('hidden');
        if (userNameDisplay) userNameDisplay.classList.add('hidden');
    }
}

function updateSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const overlay = document.querySelector('.sidebar-overlay');
    if (!currentUser && sidebar) {
        sidebar.classList.remove('active');
    }
    if (!currentUser && overlay) {
        overlay.classList.remove('active');
    }
}

function updateAdminAccess() {
    const adminElements = document.querySelectorAll('.admin-only');
    adminElements.forEach(el => {
        if (currentUser && currentUser.type === 'admin') {
            el.classList.remove('hidden');
        } else {
            el.classList.add('hidden');
        }
    });
}

function updateSefAccess() {
    const sefElements = document.querySelectorAll('.sef-only');
    sefElements.forEach(el => {
        if (currentUser && (currentUser.type === 'sef' || currentUser.type === 'admin')) {
            el.classList.remove('hidden');
        } else {
            el.classList.add('hidden');
        }
    });
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 2rem;
        background-color: ${type === 'success' ? '#28a745' : type === 'error' ? '#dc3545' : '#4a90e2'};
        color: white;
        border-radius: 5px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
        z-index: 9999;
        animation: slideIn 0.3s ease-out;
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.remove();
    }, 3000);
}

function initKanban() {
    const taskCards = document.querySelectorAll('.task-card');
    const columns = document.querySelectorAll('.kanban-column');
    
    taskCards.forEach(card => {
        card.addEventListener('dragstart', () => {
            card.classList.add('dragging');
        });
        
        card.addEventListener('dragend', () => {
            card.classList.remove('dragging');
        });
    });
    
    columns.forEach(column => {
        column.addEventListener('dragover', e => {
            e.preventDefault();
            const afterElement = getDragAfterElement(column, e.clientY);
            const draggable = document.querySelector('.dragging');
            if (afterElement == null) {
                column.appendChild(draggable);
            } else {
                column.insertBefore(draggable, afterElement);
            }
        });
    });
}

function getDragAfterElement(container, y) {
    const draggableElements = [...container.querySelectorAll('.task-card:not(.dragging)')];
    
    return draggableElements.reduce((closest, child) => {
        const box = child.getBoundingClientRect();
        const offset = y - box.top - box.height / 2;
        
        if (offset < 0 && offset > closest.offset) {
            return { offset: offset, element: child };
        } else {
            return closest;
        }
    }, { offset: Number.NEGATIVE_INFINITY }).element;
}


function initNotes() {
    const notesGrid = document.querySelector('.notes-grid');
    if (!notesGrid) return;
    
    const userNotes = mockDB.notes.filter(n => n.userId === currentUser?.id);
    
    userNotes.forEach(note => {
        const noteCard = createNoteCard(note);
        notesGrid.appendChild(noteCard);
    });
}

function createNoteCard(note) {
    const card = document.createElement('div');
    card.className = 'note-card';
    card.innerHTML = `
        <div class="note-actions">
            <button class="btn btn-sm btn-secondary" onclick="editNote(${note.id})">✏️</button>
            <button class="btn btn-sm btn-danger" onclick="deleteNote(${note.id})">🗑️</button>
        </div>
        <div class="note-title">${note.title}</div>
        <div class="note-preview">${note.content}</div>
        <div style="margin-top: 0.5rem; font-size: 0.85rem; color: var(--text-muted);">
            📁 ${note.collection}
        </div>
    `;
    return card;
}

function deleteNote(id) {
    if (confirm('Delete this note?')) {
        const index = mockDB.notes.findIndex(n => n.id === id);
        if (index > -1) {
            mockDB.notes.splice(index, 1);
            location.reload();
        }
    }
}

function editNote(id) {
    showNotification('Edit functionality - coming soon!');
}


function rotateQuote() {
    const quoteElement = document.getElementById('quoteText');
    if (!quoteElement) return;
    
    const randomIndex = Math.floor(Math.random() * mockDB.quotes.length);
    quoteElement.textContent = mockDB.quotes[randomIndex];
}

if (document.querySelector('.kanban-board')) {
    initKanban();
}

if (document.querySelector('.notes-grid')) {
    initNotes();
}

// qoute each 10sec
setInterval(rotateQuote, 10000);
rotateQuote();


window.UniMate = {
    login,
    logout,
    toggleTheme,
    changeLanguage,
    t,
    toggleSidebar, 
    showNotification,
    get currentUser() { return currentUser; },
    mockDB
};