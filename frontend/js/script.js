// ==========================================================
// API CONFIGURATION (Multi-Port & Live Server Compatibility)
// ==========================================================
function getApiUrl(endpoint) {
    if (!endpoint) return '';
    if (endpoint.startsWith('http://') || endpoint.startsWith('https://')) return endpoint;
    if (!endpoint.startsWith('/')) endpoint = '/' + endpoint;
    // If running on port 8080 (served by Java backend), use relative path
    if (window.location.protocol === 'http:' && window.location.port === '8080') {
        return endpoint;
    }
    // If opened via Live Server (:5500), file://, or other port, route to backend on :8080
    return 'http://localhost:8080' + endpoint;
}

const DEFAULT_DEPARTMENTS = [
    { deptId: 1, deptCode: 'CSE-AI', deptName: 'Computer Science & Engineering (Artificial Intelligence)' },
    { deptId: 2, deptCode: 'CSE', deptName: 'Computer Science & Engineering' },
    { deptId: 3, deptCode: 'IT', deptName: 'Information Technology' },
    { deptId: 4, deptCode: 'ECE', deptName: 'Electronics & Communication Engineering' },
    { deptId: 5, deptCode: 'MECH', deptName: 'Mechanical Engineering' }
];

/**
 * SMART ALUMNI NETWORK SYSTEM - CLIENT SCRIPT
 * Zero-framework Vanilla ES6+ JavaScript SPA Controller
 */

// ==========================================================
// APPLICATION STATE
// ==========================================================
const state = {
    currentUser: null,
    activeTab: 'dashboard',
    departments: [],
    alumniList: [],
    jobsList: [],
    endowmentList: [],
    mentorshipList: []
};

// ==========================================================
// INITIALIZATION
// ==========================================================
document.addEventListener('DOMContentLoaded', async () => {
    // Restore session from localStorage if available
    const savedUser = localStorage.getItem('smart_alumni_user');
    if (savedUser) {
        try {
            state.currentUser = JSON.parse(savedUser);
        } catch (e) {
            localStorage.removeItem('smart_alumni_user');
        }
    }

    updateAuthUI();
    await loadDepartments();
    await loadDashboardStats();
    await loadDirectory();
    await loadJobs();
    await loadEndowments();

    if (state.currentUser) {
        await loadMentorshipSessions();
        await loadNotifications();
    }

    // Set today's date as min for mentorship date picker
    const bookDateInput = document.getElementById('bookDate');
    if (bookDateInput) {
        const today = new Date().toISOString().split('T')[0];
        bookDateInput.min = today;
    }
});

// ==========================================================
// SPA TAB NAVIGATION
// ==========================================================
function switchTab(tabId) {
    state.activeTab = tabId;

    // Update active nav class
    document.querySelectorAll('.nav-item').forEach(item => {
        if (item.dataset.tab === tabId) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    // Update active view section
    document.querySelectorAll('.view-section').forEach(sec => {
        sec.classList.remove('active');
    });

    const target = document.getElementById('view' + tabId.charAt(0).toUpperCase() + tabId.slice(1));
    if (target) {
        target.classList.add('active');
    }

    // Trigger tab-specific refresh
    if (tabId === 'directory') loadDirectory();
    if (tabId === 'mentorship') loadMentorshipSessions();
    if (tabId === 'jobs') loadJobs();
    if (tabId === 'endowment') loadEndowments();
    if (tabId === 'notifications') loadNotifications();
    if (tabId === 'dashboard') loadDashboardStats();

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ==========================================================
// AUTHENTICATION & SESSION MANAGEMENT
// ==========================================================
function updateAuthUI() {
    const loggedOutControls = document.getElementById('loggedOutControls');
    const loggedInControls = document.getElementById('loggedInControls');
    const roleActionPanel = document.getElementById('roleActionPanel');
    const rolePanelTitle = document.getElementById('rolePanelTitle');
    const roleBadge = document.getElementById('roleBadge');
    const rolePanelBody = document.getElementById('rolePanelBody');
    const btnPostJob = document.getElementById('btnPostJobTrigger');
    const myJobsToggle = document.getElementById('myJobsToggleContainer');
    const myPledgesCard = document.getElementById('myPledgesCard');

    if (state.currentUser) {
        loggedOutControls.style.display = 'none';
        loggedInControls.style.display = 'flex';

        document.getElementById('navUserName').textContent = state.currentUser.name || state.currentUser.email;
        document.getElementById('navUserRole').textContent = state.currentUser.role;
        document.getElementById('navAvatar').textContent = (state.currentUser.name || 'U').charAt(0).toUpperCase();

        // Configure role action panel
        const role = state.currentUser.role;
        roleBadge.textContent = role + ' View';
        roleBadge.className = 'badge ' + (role === 'Alumni' ? 'badge-primary' : (role === 'Admin' ? 'badge-gold' : 'badge-emerald'));

        if (role === 'Student') {
            rolePanelTitle.textContent = `Welcome, ${state.currentUser.name} (Student)`;
            rolePanelBody.innerHTML = `
                <p>Explore the <strong>Alumni Directory</strong> to discover mentors in top tech firms, request 1-on-1 career guidance sessions in the <strong>Mentorship Hub</strong>, and apply for exclusive referrals.</p>
                <div class="mt-3">
                    <button class="btn btn-sm btn-primary" onclick="switchTab('directory')">Find a Mentor</button>
                    <button class="btn btn-sm btn-outline" onclick="switchTab('jobs')">Browse Jobs</button>
                </div>
            `;
            if (btnPostJob) btnPostJob.style.display = 'none';
            if (myJobsToggle) myJobsToggle.style.display = 'none';
            if (myPledgesCard) myPledgesCard.style.display = 'block';

        } else if (role === 'Alumni') {
            rolePanelTitle.textContent = `Welcome back, ${state.currentUser.name} (${state.currentUser.designation || 'Alumnus'})`;
            rolePanelBody.innerHTML = `
                <p>Manage incoming student mentorship inquiries, publish verified job opportunities for your company, and support institutional endowment campaigns.</p>
                <div class="mt-3">
                    <button class="btn btn-sm btn-primary" onclick="openPostJobModal()">+ Post a Job Referral</button>
                    <button class="btn btn-sm btn-outline" onclick="switchTab('mentorship')">Review Session Requests</button>
                    <button class="btn btn-sm btn-gold" onclick="switchTab('endowment')">Pledge Support</button>
                </div>
            `;
            if (btnPostJob) btnPostJob.style.display = 'inline-flex';
            if (myJobsToggle) myJobsToggle.style.display = 'block';
            if (myPledgesCard) myPledgesCard.style.display = 'block';

        } else if (role === 'Admin') {
            rolePanelTitle.textContent = "Administrative Audit Dashboard";
            rolePanelBody.innerHTML = `
                <p>Live administrative overview of college alumni relations, graduate verifications, mentorship engagements, and departmental endowment goals.</p>
                <div class="mt-3">
                    <button class="btn btn-sm btn-primary" onclick="switchTab('directory')">Audit Alumni</button>
                    <button class="btn btn-sm btn-outline" onclick="switchTab('jobs')">Audit Jobs</button>
                    <button class="btn btn-sm btn-gold" onclick="switchTab('endowment')">Audit Pledges</button>
                </div>
            `;
            if (btnPostJob) btnPostJob.style.display = 'inline-flex';
            if (myJobsToggle) myJobsToggle.style.display = 'none';
            if (myPledgesCard) myPledgesCard.style.display = 'block';
        }

    } else {
        loggedOutControls.style.display = 'flex';
        loggedInControls.style.display = 'none';
        roleBadge.textContent = 'Guest Mode';
        roleBadge.className = 'badge badge-role';
        rolePanelTitle.textContent = 'Welcome to Smart Alumni Network System';
        rolePanelBody.innerHTML = `
            <p>Sign in with your academic credentials to book mentorship sessions, post or apply for job referrals, and pledge towards departmental excellence.</p>
            <div class="mt-3">
                <button class="btn btn-sm btn-primary" onclick="openModal('loginModal')">Sign In</button>
                <button class="btn btn-sm btn-outline" onclick="openModal('registerModal')">Create Account</button>
            </div>
        `;
        if (btnPostJob) btnPostJob.style.display = 'none';
        if (myJobsToggle) myJobsToggle.style.display = 'none';
        if (myPledgesCard) myPledgesCard.style.display = 'none';
    }
}

async function handleLoginSubmit(e) {
    e.preventDefault();
    const role = document.getElementById('loginRole').value;
    const email = document.getElementById('loginEmail').value.trim();
    const password = document.getElementById('loginPassword').value;

    const btn = document.getElementById('loginSubmitBtn');
    btn.disabled = true;
    btn.textContent = 'Authenticating...';

    try {
        const res = await fetch(getApiUrl('/api/auth/login'), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password, role })
        });
        const data = await res.json();

        if (res.ok && data.success) {
            state.currentUser = data;
            localStorage.setItem('smart_alumni_user', JSON.stringify(data));
            closeModal('loginModal');
            showToast(data.message || 'Login successful!', 'success');
            updateAuthUI();
            await loadMentorshipSessions();
            await loadNotifications();
            await loadDashboardStats();
        } else {
            showToast(data.message || 'Invalid credentials.', 'error');
        }
    } catch (err) {
        showToast('Server connection error: ' + err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Sign In';
    }
}

async function quickLogin(type) {
    let email = '', pass = '', role = '';
    if (type === 'student') {
        email = 'kiruthi@gmail.com';
        pass = 'student123';
        role = 'Student';
    } else if (type === 'alumni') {
        email = 'shiela@gmail.com';
        pass = 'alumni123';
        role = 'Alumni';
    } else if (type === 'admin') {
        email = 'admin@gmail.com';
        pass = 'admin123';
        role = 'Admin';
    }

    try {
        const res = await fetch(getApiUrl('/api/auth/login'), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password: pass, role })
        });
        const data = await res.json();
        if (res.ok && data.success) {
            state.currentUser = data;
            localStorage.setItem('smart_alumni_user', JSON.stringify(data));
            showToast(`Logged in as ${role}: ${data.name || email}`, 'success');
            updateAuthUI();
            await loadMentorshipSessions();
            await loadNotifications();
            await loadDashboardStats();
        } else {
            showToast('Quick login failed: ' + data.message, 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

function handleLogout() {
    state.currentUser = null;
    localStorage.removeItem('smart_alumni_user');
    showToast('Signed out successfully.', 'info');
    updateAuthUI();
    switchTab('dashboard');
}

// ==========================================================
// REGISTRATION & VERIFICATION (FR-01)
// ==========================================================
function toggleRegisterFields() {
    const role = document.getElementById('regRole').value;
    const studentFields = document.getElementById('studentFields');
    const alumniFields = document.getElementById('alumniFields');
    const labelRegNo = document.getElementById('labelRegisterNo');

    if (role === 'Alumni') {
        studentFields.style.display = 'none';
        alumniFields.style.display = 'block';
        labelRegNo.textContent = 'Graduation Register / Roll Number (Verified against College Records)';
    } else {
        studentFields.style.display = 'block';
        alumniFields.style.display = 'none';
        labelRegNo.textContent = 'Student Register / Roll Number';
    }
}

async function handleRegisterSubmit(e) {
    e.preventDefault();
    const role = document.getElementById('regRole').value;
    const name = document.getElementById('regName').value.trim();
    const email = document.getElementById('regEmail').value.trim();
    const password = document.getElementById('regPassword').value;
    const phone = document.getElementById('regPhone').value.trim();
    const deptId = parseInt(document.getElementById('regDept').value);
    const registerNo = document.getElementById('regRegisterNo').value.trim();

    const payload = { role, name, email, password, phone, deptId, registerNo };

    if (role === 'Alumni') {
        payload.batchYear = parseInt(document.getElementById('regBatchYear').value);
        payload.company = document.getElementById('regCompany').value.trim();
        payload.designation = document.getElementById('regDesignation').value.trim();
        payload.location = document.getElementById('regLocation').value.trim();
        payload.linkedinUrl = document.getElementById('regLinkedin').value.trim();
        payload.isMentorAvailable = document.getElementById('regMentorAvailable').checked;

        if (!payload.company || !payload.designation) {
            showToast('Please enter your current company and designation.', 'warning');
            return;
        }
    } else {
        payload.currentYear = parseInt(document.getElementById('regCurrentYear').value);
        payload.bio = document.getElementById('regBio').value.trim();
    }

    const btn = document.getElementById('regSubmitBtn');
    btn.disabled = true;
    btn.textContent = 'Registering & Verifying...';

    try {
        const res = await fetch(getApiUrl('/api/auth/register'), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const data = await res.json();

        if (res.ok && data.success) {
            closeModal('registerModal');
            showToast(data.message, 'success');
            // Auto open login modal
            openModal('loginModal');
            document.getElementById('loginEmail').value = email;
            document.getElementById('loginRole').value = role;
        } else {
            showToast(data.message || 'Registration failed.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    } finally {
        btn.disabled = false;
        btn.textContent = 'Complete Registration';
    }
}

// ==========================================================
// DASHBOARD METRICS & HIGHLIGHTS
// ==========================================================
async function loadDashboardStats() {
    try {
        const res = await fetch(getApiUrl('/api/stats'));
        if (res.ok) {
            const stats = await res.json();
            document.getElementById('statAlumniCount').textContent = stats.totalAlumni || 0;
            document.getElementById('statStudentsCount').textContent = stats.totalStudents || 0;
            document.getElementById('statJobsCount').textContent = stats.totalJobs || 0;
            document.getElementById('statEndowmentAmount').textContent = '₹' + Number(stats.totalEndowmentRaised || 0).toLocaleString('en-IN');
        }
    } catch (e) {
        console.error('Error loading stats:', e);
    }
}

// ==========================================================
// DEPARTMENTS
// ==========================================================
async function loadDepartments() {
    const dirSelect = document.getElementById('dirDeptSelect');
    const regSelect = document.getElementById('regDept');

    function populateSelects(depts) {
        if (!depts || depts.length === 0) depts = DEFAULT_DEPARTMENTS;
        let optionsHtml = '';
        depts.forEach(d => {
            optionsHtml += `<option value="${d.deptId}">${d.deptName} (${d.deptCode})</option>`;
        });
        if (dirSelect) dirSelect.innerHTML = '<option value="">All Departments</option>' + optionsHtml;
        if (regSelect) regSelect.innerHTML = '<option value="" disabled selected>-- Select Academic Department --</option>' + optionsHtml;
    }

    // Immediately pre-populate with default departments so dropdown is NEVER blank
    if (regSelect && regSelect.options.length <= 1) {
        populateSelects(DEFAULT_DEPARTMENTS);
    }

    try {
        const res = await fetch(getApiUrl('/api/departments'));
        if (res.ok) {
            state.departments = await res.json();
            populateSelects(state.departments);
        } else {
            populateSelects(DEFAULT_DEPARTMENTS);
        }
    } catch (e) {
        console.warn('Backend /api/departments unreachable, fallback to default departments:', e);
        populateSelects(DEFAULT_DEPARTMENTS);
    }
}

// ==========================================================
// ALUMNI DIRECTORY (FR-02)
// ==========================================================
async function loadDirectory() {
    const container = document.getElementById('alumniCardsGrid');
    const search = document.getElementById('dirSearchInput').value.trim();
    const deptId = document.getElementById('dirDeptSelect').value;
    const batchYear = document.getElementById('dirBatchSelect').value;
    const mentorOnly = document.getElementById('dirMentorOnly').checked;

    let url = `/api/alumni?search=${encodeURIComponent(search)}`;
    if (deptId) url += `&deptId=${deptId}`;
    if (batchYear) url += `&batchYear=${batchYear}`;
    if (mentorOnly) url += `&mentorOnly=true`;

    try {
        const res = await fetch(getApiUrl(url));
        if (res.ok) {
            state.alumniList = await res.json();
            document.getElementById('dirCount').textContent = state.alumniList.length;

            if (state.alumniList.length === 0) {
                container.innerHTML = '<div class="loading-spinner">No alumni matched your search criteria.</div>';
                return;
            }

            container.innerHTML = state.alumniList.map(a => renderAlumniCard(a)).join('');

            // Also populate featured mentors on dashboard
            const featuredContainer = document.getElementById('featuredMentorsContainer');
            if (featuredContainer) {
                const mentors = state.alumniList.filter(a => a.mentorAvailable).slice(0, 3);
                if (mentors.length > 0) {
                    featuredContainer.innerHTML = mentors.map(a => `
                        <div class="alumni-card-top mb-3" style="border-bottom: 1px solid var(--border-light); padding-bottom: 12px;">
                            <div class="alumni-avatar-circle">${a.fullName.charAt(0)}</div>
                            <div class="alumni-main-info">
                                <h4>${a.fullName}</h4>
                                <div class="alumni-designation">${a.designation} &bull; ${a.company}</div>
                                <div class="alumni-company">${a.deptName} (Batch ${a.batchYear})</div>
                            </div>
                        </div>
                    `).join('');
                } else {
                    featuredContainer.innerHTML = '<p class="text-muted">No mentors available.</p>';
                }
            }
        }
    } catch (e) {
        container.innerHTML = '<div class="loading-spinner text-danger">Error loading alumni directory.</div>';
    }
}

function handleDirectoryFilter() {
    clearTimeout(window.dirFilterTimer);
    window.dirFilterTimer = setTimeout(() => {
        loadDirectory();
    }, 250);
}

function renderAlumniCard(a) {
    const isAvailable = a.mentorAvailable;
    return `
        <div class="alumni-card">
            <div>
                <div class="alumni-card-top">
                    <div class="alumni-avatar-circle">${a.fullName.charAt(0)}</div>
                    <div class="alumni-main-info">
                        <h4>${escapeHtml(a.fullName)}</h4>
                        <div class="alumni-designation">${escapeHtml(a.designation)}</div>
                        <div class="alumni-company">${escapeHtml(a.company)}</div>
                    </div>
                </div>

                <div class="alumni-details-list">
                    <div class="alumni-detail-row">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 10v6M2 10l10-5 10 5-10 5z"/><path d="M6 12v5c3 3 9 3 12 0v-5"/></svg>
                        <span>${escapeHtml(a.deptName)} &bull; Batch ${a.batchYear}</span>
                    </div>
                    ${a.location ? `
                    <div class="alumni-detail-row">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
                        <span>${escapeHtml(a.location)}</span>
                    </div>` : ''}
                    ${a.linkedinUrl ? `
                    <div class="alumni-detail-row">
                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"/><rect x="2" y="9" width="4" height="12"/><circle cx="4" cy="4" r="2"/></svg>
                        <a href="${escapeHtml(a.linkedinUrl)}" target="_blank" rel="noopener">LinkedIn Profile &rarr;</a>
                    </div>` : ''}
                </div>
            </div>

            <div class="alumni-card-footer">
                <span class="badge ${isAvailable ? 'badge-emerald' : 'badge-role'}">
                    ${isAvailable ? '&bull; Open for Mentorship' : 'Mentorship Busy'}
                </span>
                ${isAvailable ? `
                    <button class="btn btn-sm btn-primary" onclick="openBookMentorshipModal(${a.alumniId}, '${escapeHtml(a.fullName)}', '${escapeHtml(a.company)} - ${escapeHtml(a.designation)}')">
                        Book Session
                    </button>
                ` : `
                    <button class="btn btn-sm btn-outline" disabled>Unavailable</button>
                `}
            </div>
        </div>
    `;
}

// ==========================================================
// MENTORSHIP SCHEDULING (FR-03)
// ==========================================================
function openBookMentorshipModal(alumniId, name, company) {
    if (!state.currentUser) {
        showToast('Please sign in to schedule a mentorship session.', 'warning');
        openModal('loginModal');
        return;
    }
    if (state.currentUser.role !== 'Student') {
        showToast('Only registered students can schedule mentorship sessions.', 'warning');
        return;
    }

    document.getElementById('bookAlumniId').value = alumniId;
    document.getElementById('bookAlumniName').textContent = name;
    document.getElementById('bookAlumniCompany').textContent = company;

    openModal('bookMentorshipModal');
}

async function handleBookMentorshipSubmit(e) {
    e.preventDefault();
    if (!state.currentUser) return;

    const alumniId = parseInt(document.getElementById('bookAlumniId').value);
    const topic = document.getElementById('bookTopic').value.trim();
    const preferredDate = document.getElementById('bookDate').value;
    const preferredTime = document.getElementById('bookTime').value;
    const message = document.getElementById('bookMessage').value.trim();

    try {
        const res = await fetch(getApiUrl('/api/mentorship'), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            },
            body: JSON.stringify({ alumniId, topic, preferredDate, preferredTime, message })
        });
        const data = await res.json();

        if (res.ok && data.success) {
            closeModal('bookMentorshipModal');
            showToast(data.message, 'success');
            document.getElementById('bookMentorshipForm').reset();
            switchTab('mentorship');
        } else {
            showToast(data.message || 'Failed to book session.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function loadMentorshipSessions() {
    if (!state.currentUser) return;
    const tableBody = document.getElementById('mentorshipTableBody');
    const badge = document.getElementById('mentorshipCountBadge');

    try {
        const res = await fetch(getApiUrl('/api/mentorship'), {
            headers: {
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            }
        });
        if (res.ok) {
            state.mentorshipList = await res.json();
            badge.textContent = state.mentorshipList.length + ' Sessions';

            if (state.mentorshipList.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted">No mentorship requests found.</td></tr>`;
                return;
            }

            const isAlumni = state.currentUser.role === 'Alumni';

            tableBody.innerHTML = state.mentorshipList.map(s => {
                const statusClass = 'status-' + s.status.toLowerCase();
                return `
                    <tr>
                        <td>
                            <strong>${isAlumni ? escapeHtml(s.studentName) : escapeHtml(s.alumniName)}</strong><br>
                            <small class="text-muted">${isAlumni ? escapeHtml(s.studentDept || '') : escapeHtml(s.alumniCompany || '')}</small>
                        </td>
                        <td>
                            <strong>${escapeHtml(s.topic)}</strong><br>
                            <small class="text-muted">${escapeHtml(s.message)}</small>
                        </td>
                        <td>
                            <span>${s.preferredDate}</span><br>
                            <small class="text-muted">${s.preferredTime}</small>
                        </td>
                        <td><span class="badge ${statusClass}">${s.status}</span></td>
                        <td><small>${escapeHtml(s.responseNotes || '—')}</small></td>
                        <td>
                            ${isAlumni && s.status === 'Pending' ? `
                                <button class="btn btn-xs btn-primary" onclick="respondMentorship(${s.sessionId}, 'Accepted')">Accept</button>
                                <button class="btn btn-xs btn-outline-danger" onclick="respondMentorship(${s.sessionId}, 'Rejected')">Reject</button>
                            ` : (isAlumni && s.status === 'Accepted' ? `
                                <button class="btn btn-xs btn-outline" onclick="respondMentorship(${s.sessionId}, 'Completed')">Mark Completed</button>
                            ` : `
                                <span class="text-muted text-xs">—</span>
                            `)}
                        </td>
                    </tr>
                `;
            }).join('');
        }
    } catch (e) {
        console.error('Error loading mentorship:', e);
    }
}

async function respondMentorship(sessionId, status) {
    const notes = prompt(`Enter notes / meeting details for student (${status}):`, status === 'Accepted' ? 'Confirmed! Google Meet / Classroom link sent.' : 'Unavailable during this slot.');
    if (notes === null) return;

    try {
        const res = await fetch(getApiUrl(`/api/mentorship/${sessionId}/status`), {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            },
            body: JSON.stringify({ status, responseNotes: notes })
        });
        const data = await res.json();
        if (res.ok && data.success) {
            showToast(`Session marked as ${status}.`, 'success');
            await loadMentorshipSessions();
            await loadNotifications();
        } else {
            showToast(data.message || 'Status update failed.', 'error');
        }
    } catch (e) {
        showToast('Error: ' + e.message, 'error');
    }
}

// ==========================================================
// JOB REFERRAL BULLETIN (FR-04)
// ==========================================================
async function loadJobs() {
    const container = document.getElementById('jobsContainer');
    const search = document.getElementById('jobSearchInput').value.trim();
    const location = document.getElementById('jobLocationInput').value.trim();
    const myJobsOnly = document.getElementById('myJobsOnly')?.checked;

    let url = `/api/jobs?search=${encodeURIComponent(search)}&location=${encodeURIComponent(location)}`;
    if (myJobsOnly && state.currentUser) {
        url += '&my=true';
    }

    try {
        const headers = {};
        if (state.currentUser) {
            headers['X-User-Id'] = state.currentUser.userId;
            headers['X-User-Role'] = state.currentUser.role;
        }

        const res = await fetch(getApiUrl(url), { headers });
        if (res.ok) {
            state.jobsList = await res.json();
            if (state.jobsList.length === 0) {
                container.innerHTML = '<div class="loading-spinner">No job referral postings match your query.</div>';
                return;
            }

            container.innerHTML = state.jobsList.map(j => renderJobCard(j)).join('');

            // Also populate featured on dashboard
            const featured = document.getElementById('featuredJobsContainer');
            if (featured) {
                featured.innerHTML = state.jobsList.slice(0, 3).map(j => `
                    <div class="mb-3" style="border-bottom: 1px solid var(--border-light); padding-bottom: 12px;">
                        <h4 style="font-size: 15px; color: var(--navy-deep);">${escapeHtml(j.jobTitle)}</h4>
                        <div class="text-muted" style="font-size: 13px;">${escapeHtml(j.company)} &bull; ${escapeHtml(j.location)}</div>
                        <a href="${escapeHtml(j.applicationLink)}" target="_blank" rel="noopener" class="text-link" style="font-size: 12px;">Apply / Details &rarr;</a>
                    </div>
                `).join('');
            }
        }
    } catch (e) {
        container.innerHTML = '<div class="loading-spinner text-danger">Error loading job postings.</div>';
    }
}

function handleJobsFilter() {
    clearTimeout(window.jobFilterTimer);
    window.jobFilterTimer = setTimeout(() => {
        loadJobs();
    }, 250);
}

function renderJobCard(j) {
    const isAuthor = state.currentUser && (
        (state.currentUser.role === 'Alumni' && state.currentUser.email === j.alumniEmail) ||
        state.currentUser.role === 'Admin'
    );

    const skills = j.requiredSkills.split(',').map(s => `<span class="skill-tag">${escapeHtml(s.trim())}</span>`).join(' ');

    return `
        <div class="job-card">
            <div class="job-main">
                <div class="job-title-row">
                    <h3>${escapeHtml(j.jobTitle)}</h3>
                    <span class="badge badge-primary">${escapeHtml(j.jobType || 'Full-Time')}</span>
                </div>
                <div class="job-meta-row">
                    <strong>${escapeHtml(j.company)}</strong>
                    <span>&bull; ${escapeHtml(j.location)}</span>
                    <span>&bull; ${escapeHtml(j.salaryRange || 'Competitive')}</span>
                    <span class="text-muted">&bull; Posted by ${escapeHtml(j.alumniName)}</span>
                </div>
                <p class="job-desc">${escapeHtml(j.description)}</p>
                <div class="job-skills">${skills}</div>
            </div>
            <div class="job-actions">
                <a href="${escapeHtml(j.applicationLink)}" target="_blank" rel="noopener" class="btn btn-sm btn-primary">
                    Apply Now &rarr;
                </a>
                ${isAuthor ? `
                    <button class="btn btn-sm btn-outline" onclick="openEditJobModal(${j.jobId})">Edit</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteJob(${j.jobId})">Delete</button>
                ` : ''}
            </div>
        </div>
    `;
}

function openPostJobModal() {
    if (!state.currentUser || (state.currentUser.role !== 'Alumni' && state.currentUser.role !== 'Admin')) {
        showToast('Only verified alumni can publish job postings.', 'warning');
        return;
    }
    document.getElementById('jobModalTitle').textContent = 'Post Job Referral';
    document.getElementById('jobForm').reset();
    document.getElementById('editJobId').value = '';
    document.getElementById('jobSubmitBtn').textContent = 'Publish Job Opportunity';
    openModal('jobModal');
}

function openEditJobModal(jobId) {
    const job = state.jobsList.find(j => j.jobId === jobId);
    if (!job) return;

    document.getElementById('jobModalTitle').textContent = 'Edit Job Referral';
    document.getElementById('editJobId').value = job.jobId;
    document.getElementById('jobTitle').value = job.jobTitle;
    document.getElementById('jobCompany').value = job.company;
    document.getElementById('jobLocation').value = job.location;
    document.getElementById('jobType').value = job.jobType || 'Full-Time';
    document.getElementById('jobSalary').value = job.salaryRange || '';
    document.getElementById('jobLink').value = job.applicationLink;
    document.getElementById('jobSkills').value = job.requiredSkills;
    document.getElementById('jobDesc').value = job.description;

    document.getElementById('jobSubmitBtn').textContent = 'Save Changes';
    openModal('jobModal');
}

async function handleJobSubmit(e) {
    e.preventDefault();
    if (!state.currentUser) return;

    const editId = document.getElementById('editJobId').value;
    const payload = {
        jobTitle: document.getElementById('jobTitle').value.trim(),
        company: document.getElementById('jobCompany').value.trim(),
        location: document.getElementById('jobLocation').value.trim(),
        jobType: document.getElementById('jobType').value,
        salaryRange: document.getElementById('jobSalary').value.trim(),
        applicationLink: document.getElementById('jobLink').value.trim(),
        requiredSkills: document.getElementById('jobSkills').value.trim(),
        description: document.getElementById('jobDesc').value.trim()
    };

    const isEdit = editId !== '';
    const url = isEdit ? `/api/jobs/${editId}` : '/api/jobs';
    const method = isEdit ? 'PUT' : 'POST';

    try {
        const res = await fetch(getApiUrl(url), {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            },
            body: JSON.stringify(payload)
        });
        const data = await res.json();

        if (res.ok && data.success) {
            closeModal('jobModal');
            showToast(data.message, 'success');
            await loadJobs();
            await loadDashboardStats();
        } else {
            showToast(data.message || 'Job submission failed.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function deleteJob(jobId) {
    if (!confirm('Are you sure you want to permanently delete this job posting?')) return;

    try {
        const res = await fetch(getApiUrl(`/api/jobs/${jobId}`), {
            method: 'DELETE',
            headers: {
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            }
        });
        const data = await res.json();

        if (res.ok && data.success) {
            showToast(data.message, 'success');
            await loadJobs();
            await loadDashboardStats();
        } else {
            showToast(data.message || 'Delete failed.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

// ==========================================================
// DEPARTMENT ENDOWMENTS & PLEDGES (FR-05)
// ==========================================================
async function loadEndowments() {
    const container = document.getElementById('endowmentProjectsContainer');
    try {
        const res = await fetch(getApiUrl('/api/endowments'));
        if (res.ok) {
            state.endowmentList = await res.json();
            let totalPledged = 0;

            container.innerHTML = state.endowmentList.map(p => {
                totalPledged += p.currentAmount;
                return `
                    <div class="project-card">
                        <div>
                            <div class="project-dept">${escapeHtml(p.deptName)}</div>
                            <h3>${escapeHtml(p.title)}</h3>
                            <p class="project-desc">${escapeHtml(p.description)}</p>
                        </div>
                        <div>
                            <div class="progress-wrap">
                                <div class="progress-bar">
                                    <div class="progress-fill" style="width: ${p.percentageFunded}%;"></div>
                                </div>
                                <div class="progress-labels">
                                    <span class="progress-raised">₹${Number(p.currentAmount).toLocaleString('en-IN')} Raised (${p.percentageFunded}%)</span>
                                    <span class="progress-target">Goal: ₹${Number(p.targetAmount).toLocaleString('en-IN')}</span>
                                </div>
                            </div>
                            <button class="btn btn-gold btn-block" onclick="openPledgeModal(${p.projectId}, '${escapeHtml(p.title)}', ${p.currentAmount}, ${p.targetAmount})">
                                Make a Pledge &rarr;
                            </button>
                        </div>
                    </div>
                `;
            }).join('');

            document.getElementById('endowmentAggregateHeader').textContent = 'Total Raised: ₹' + totalPledged.toLocaleString('en-IN');
        }

        // Load user pledges if logged in
        if (state.currentUser) {
            loadUserPledges();
        }
    } catch (e) {
        container.innerHTML = '<div class="loading-spinner text-danger">Error loading endowment projects.</div>';
    }
}

function openPledgeModal(projectId, title, current, target) {
    if (!state.currentUser) {
        showToast('Please sign in to record a pledge commitment.', 'warning');
        openModal('loginModal');
        return;
    }

    document.getElementById('pledgeProjectId').value = projectId;
    document.getElementById('pledgeProjectTitle').textContent = title;
    const pct = Math.min(100, Math.round((current / target) * 100));
    document.getElementById('pledgeModalProgress').style.width = pct + '%';
    document.getElementById('pledgeModalProgressLabel').textContent = `₹${current.toLocaleString('en-IN')} / ₹${target.toLocaleString('en-IN')} (${pct}%)`;

    openModal('pledgeModal');
}

async function handlePledgeSubmit(e) {
    e.preventDefault();
    if (!state.currentUser) return;

    const projectId = parseInt(document.getElementById('pledgeProjectId').value);
    const pledgeAmount = parseFloat(document.getElementById('pledgeAmount').value);
    const notes = document.getElementById('pledgeNotes').value.trim();

    try {
        const res = await fetch(getApiUrl('/api/pledges'), {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            },
            body: JSON.stringify({ projectId, pledgeAmount, notes })
        });
        const data = await res.json();

        if (res.ok && data.success) {
            closeModal('pledgeModal');
            showToast(data.message, 'success');
            document.getElementById('pledgeForm').reset();
            await loadEndowments();
            await loadDashboardStats();
            await loadNotifications();
        } else {
            showToast(data.message || 'Pledge submission failed.', 'error');
        }
    } catch (err) {
        showToast('Error: ' + err.message, 'error');
    }
}

async function loadUserPledges() {
    const tableBody = document.getElementById('myPledgesTableBody');
    const badge = document.getElementById('myPledgesBadge');

    try {
        const res = await fetch(getApiUrl('/api/pledges/my'), {
            headers: {
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            }
        });
        if (res.ok) {
            const pledges = await res.json();
            badge.textContent = pledges.length + ' Pledges';

            if (pledges.length === 0) {
                tableBody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">You have not recorded any pledges yet.</td></tr>`;
                return;
            }

            tableBody.innerHTML = pledges.map(p => `
                <tr>
                    <td><strong>${escapeHtml(p.projectTitle)}</strong></td>
                    <td class="text-gold font-bold">₹${Number(p.pledgeAmount).toLocaleString('en-IN')}</td>
                    <td>${new Date(p.pledgeDate).toLocaleDateString()}</td>
                    <td><span class="badge badge-emerald">${p.paymentStatus}</span></td>
                    <td><small class="text-muted">${escapeHtml(p.notes || '—')}</small></td>
                </tr>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading pledges:', e);
    }
}

// ==========================================================
// NOTIFICATIONS
// ==========================================================
async function loadNotifications() {
    if (!state.currentUser) return;
    const container = document.getElementById('notificationListContainer');
    const dot = document.getElementById('notifDot');

    try {
        const res = await fetch(getApiUrl('/api/notifications'), {
            headers: {
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            }
        });
        if (res.ok) {
            const list = await res.json();
            const unread = list.filter(n => !n.read).length;

            if (dot) dot.style.display = unread > 0 ? 'inline-block' : 'none';

            if (list.length === 0) {
                container.innerHTML = '<p class="text-muted text-center py-4">No notifications at this time.</p>';
                return;
            }

            container.innerHTML = list.map(n => `
                <div class="notification-item ${!n.read ? 'unread' : ''}" onclick="markNotifRead(${n.notificationId})">
                    <div class="notification-icon">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/><path d="M13.73 21a2 2 0 0 1-3.46 0"/></svg>
                    </div>
                    <div class="notification-text">
                        <h5>${escapeHtml(n.title)}</h5>
                        <p>${escapeHtml(n.message)}</p>
                        <div class="notification-time">${new Date(n.createdAt).toLocaleString()}</div>
                    </div>
                </div>
            `).join('');
        }
    } catch (e) {
        console.error('Error loading notifications:', e);
    }
}

async function markNotifRead(notifId) {
    if (!state.currentUser) return;
    try {
        await fetch(getApiUrl(`/api/notifications/${notifId}/read`), {
            method: 'PUT',
            headers: {
                'X-User-Id': state.currentUser.userId,
                'X-User-Role': state.currentUser.role
            }
        });
        loadNotifications();
    } catch (e) {}
}

// ==========================================================
// PROFILE MODAL
// ==========================================================
function openModal(id) {
    if (id === 'profileModal') {
        renderProfileModal();
    }
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('open');
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('open');
}

function switchModal(from, to) {
    closeModal(from);
    openModal(to);
}

function renderProfileModal() {
    const view = document.getElementById('profileDetailsView');
    if (!state.currentUser) {
        view.innerHTML = '<p>Please log in to view your profile.</p>';
        return;
    }

    const u = state.currentUser;
    view.innerHTML = `
        <div class="text-center mb-4">
            <div class="user-avatar" style="width: 64px; height: 64px; font-size: 24px; margin: 0 auto 10px;">${(u.name || 'U').charAt(0)}</div>
            <h4>${escapeHtml(u.name || 'User')}</h4>
            <span class="badge badge-primary">${u.role}</span>
        </div>
        <div class="alumni-details-list">
            <div class="alumni-detail-row"><strong>Email:</strong> <span>${escapeHtml(u.email)}</span></div>
            ${u.registerNo ? `<div class="alumni-detail-row"><strong>Register No:</strong> <span>${escapeHtml(u.registerNo)}</span></div>` : ''}
            ${u.deptName ? `<div class="alumni-detail-row"><strong>Department:</strong> <span>${escapeHtml(u.deptName)}</span></div>` : ''}
            ${u.company ? `<div class="alumni-detail-row"><strong>Company:</strong> <span>${escapeHtml(u.company)}</span></div>` : ''}
            ${u.designation ? `<div class="alumni-detail-row"><strong>Designation:</strong> <span>${escapeHtml(u.designation)}</span></div>` : ''}
            ${u.currentYear ? `<div class="alumni-detail-row"><strong>Year of Study:</strong> <span>${u.currentYear} Year</span></div>` : ''}
        </div>
        <div class="mt-4 text-center">
            <button class="btn btn-outline btn-block" onclick="closeModal('profileModal')">Close</button>
        </div>
    `;
}

// ==========================================================
// TOAST NOTIFICATIONS
// ==========================================================
function showToast(message, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => toast.remove(), 250);
    }, 4000);
}

// ==========================================================
// UTILITY
// ==========================================================
function escapeHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
