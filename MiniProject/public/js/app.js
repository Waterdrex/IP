/**
 * Application Controller for TCET Project Management System.
 * Manages UI interactions, DOM updates, drag-and-drop Kanban, and REST synchronization.
 */

// Global State
let projects = [];
let tasks = [];
let users = [];
let currentProjectId = null;
let editingTaskId = null;
let isBackendOnline = false;

// ========== INITIALIZATION ==========
document.addEventListener('DOMContentLoaded', async () => {
  setupDateDisplay();
  setupEventListeners();
  setupDragAndDrop();
  await checkBackendStatus();
  await refreshAllData();
  showView('dashboard');

  // Periodic health check every 10 seconds
  setInterval(checkBackendStatus, 10000);
});

function setupDateDisplay() {
  const dateEl = document.getElementById('current-date');
  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString('en-IN', {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}

async function checkBackendStatus() {
  const statusPill = document.getElementById('backend-status');
  const statusText = document.getElementById('backend-status-text');
  const statusDot = document.getElementById('backend-status-dot');

  isBackendOnline = await API.checkHealth();
  if (isBackendOnline) {
    if (statusText) statusText.textContent = 'Java Backend: Connected (Port 8080)';
    if (statusDot) {
      statusDot.className = 'w-2.5 h-2.5 rounded-full bg-emerald-500 status-pulse';
    }
  } else {
    if (statusText) statusText.textContent = 'Backend Offline - Start Java Server';
    if (statusDot) {
      statusDot.className = 'w-2.5 h-2.5 rounded-full bg-red-500';
    }
  }
}

async function refreshAllData() {
  try {
    const [fetchedProjects, fetchedUsers] = await Promise.all([
      API.getProjects(),
      API.getUsers()
    ]);
    projects = fetchedProjects || [];
    users = fetchedUsers || [];
    populateUserSelects();

    if (currentProjectId) {
      tasks = await API.getTasks(currentProjectId);
    } else {
      tasks = await API.getTasks();
    }
  } catch (err) {
    console.error('Error fetching data:', err);
    showToast('Failed to connect to Java server. Check terminal.', 'error');
  }
}

// ========== NAVIGATION & VIEWS ==========
function showView(view) {
  ['dashboard', 'projects', 'kanban', 'users'].forEach(v => {
    const el = document.getElementById(`view-${v}`);
    const nav = document.getElementById(`nav-${v}`);
    if (el) el.classList.add('hidden');
    if (nav) nav.classList.remove('active');
  });

  const activeEl = document.getElementById(`view-${view}`);
  const activeNav = document.getElementById(`nav-${view}`);
  if (activeEl) activeEl.classList.remove('hidden');
  if (activeNav) activeNav.classList.add('active');

  const titles = {
    dashboard: ['Dashboard', 'High-level metrics and system overview'],
    projects: ['Projects Directory', 'Manage your team workspaces and milestones'],
    kanban: ['Kanban Board', 'Interactive workflow & task tracking'],
    users: ['Team Directory', 'Manage engineering team members and assignments']
  };

  const titleEl = document.getElementById('page-title');
  const subEl = document.getElementById('page-subtitle');
  if (titleEl && titles[view]) titleEl.textContent = titles[view][0];
  if (subEl && titles[view]) subEl.textContent = titles[view][1];

  if (view === 'dashboard') renderDashboard();
  if (view === 'projects') renderProjects();
  if (view === 'kanban') renderKanban();
  if (view === 'users') renderUsers();
}

// ========== DASHBOARD VIEW ==========
async function renderDashboard() {
  try {
    const stats = await API.getStats();
    document.getElementById('stat-projects').textContent = stats.totalProjects || 0;
    document.getElementById('stat-active').textContent = stats.activeTasks || 0;
    document.getElementById('stat-completed').textContent = stats.completedTasks || 0;
    document.getElementById('stat-users').textContent = stats.totalUsers || 0;

    projects = await API.getProjects();
    tasks = await API.getTasks();

    const container = document.getElementById('recent-projects');
    if (!container) return;

    if (projects.length === 0) {
      container.innerHTML = `<div class="px-6 py-12 text-center text-slate-400 text-sm">No projects yet. Create your first project from the Projects tab.</div>`;
      return;
    }

    const recent = [...projects].slice(0, 5);
    container.innerHTML = recent.map(p => {
      const pTasks = tasks.filter(t => t.projectId === p.id);
      const done = pTasks.filter(t => t.status === 'completed').length;
      const pct = pTasks.length > 0 ? Math.round((done / pTasks.length) * 100) : 0;

      return `
        <div class="px-6 py-4 flex items-center justify-between hover:bg-slate-50 transition cursor-pointer" onclick="openKanban('${p.id}')">
          <div class="flex-1 min-w-0 pr-4">
            <div class="flex items-center gap-2">
              <p class="font-semibold text-slate-800 truncate">${escapeHtml(p.name)}</p>
              ${priorityBadge(p.priority)}
            </div>
            <p class="text-xs text-slate-500 mt-1">${pTasks.length} tasks · ${done} completed · Owner: ${escapeHtml(p.owner || 'Unassigned')}</p>
            <div class="w-full bg-slate-100 rounded-full h-1.5 mt-2 max-w-md">
              <div class="bg-primary-600 h-1.5 rounded-full transition-all duration-500" style="width: ${pct}%"></div>
            </div>
          </div>
          <div class="flex items-center gap-3">
            <span class="text-xs font-semibold text-slate-600">${pct}%</span>
            <svg class="w-5 h-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/></svg>
          </div>
        </div>`;
    }).join('');
  } catch (e) {
    console.error('Error rendering dashboard:', e);
  }
}

// ========== PROJECTS VIEW ==========
async function renderProjects() {
  try {
    projects = await API.getProjects();
    tasks = await API.getTasks();

    const list = document.getElementById('projects-list');
    if (!list) return;

    if (projects.length === 0) {
      list.innerHTML = `<div class="px-6 py-12 text-center text-slate-400 text-sm">No projects created yet. Use the form above to initialize one.</div>`;
      return;
    }

    list.innerHTML = projects.map(p => {
      const pTasks = tasks.filter(t => t.projectId === p.id);
      const done = pTasks.filter(t => t.status === 'completed').length;
      return `
        <div class="px-6 py-5 flex flex-wrap items-center justify-between gap-4 hover:bg-slate-50 transition">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2">
              <p class="font-bold text-slate-800 text-base truncate">${escapeHtml(p.name)}</p>
              ${priorityBadge(p.priority)}
              <span class="text-xs bg-slate-100 text-slate-600 px-2 py-0.5 rounded-md font-mono">ID: ${p.id}</span>
            </div>
            <p class="text-sm text-slate-500 mt-1">${escapeHtml(p.description || 'No description provided.')}</p>
            <div class="flex items-center gap-4 mt-2 text-xs text-slate-400">
              <span>📋 ${pTasks.length} tasks (${done} done)</span>
              <span>👤 Lead: <strong class="text-slate-600">${escapeHtml(p.owner || 'Unassigned')}</strong></span>
              <span>📅 ${new Date(p.createdAt).toLocaleDateString()}</span>
            </div>
          </div>
          <div class="flex items-center gap-2">
            <button onclick="openKanban('${p.id}')" class="px-4 py-2 rounded-xl bg-primary-600 hover:bg-primary-700 text-white text-sm font-medium transition shadow-sm flex items-center gap-1.5">
              <span>Open Kanban</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"/></svg>
            </button>
            <button onclick="deleteProject('${p.id}')" class="p-2 rounded-xl text-red-500 hover:bg-red-50 transition" title="Delete Project">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
            </button>
          </div>
        </div>`;
    }).join('');
  } catch (e) {
    console.error('Error rendering projects:', e);
  }
}

async function handleProjectSubmit(e) {
  e.preventDefault();
  const name = document.getElementById('project-name').value.trim();
  const description = document.getElementById('project-desc').value.trim();
  const priority = document.getElementById('project-priority').value;
  const owner = document.getElementById('project-owner').value;

  if (!name) return;

  try {
    await API.createProject({ name, description, priority, owner });
    document.getElementById('project-form').reset();
    showToast('Project created successfully on Java Backend');
    await renderProjects();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteProject(id) {
  if (!confirm('Are you sure you want to delete this project? All associated tasks will be removed in accordance with Java backend cascade logic.')) return;
  try {
    await API.deleteProject(id);
    showToast('Project removed');
    if (currentProjectId === id) {
      currentProjectId = null;
      showView('projects');
    } else {
      await renderProjects();
    }
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ========== KANBAN VIEW ==========
async function openKanban(projectId) {
  currentProjectId = projectId;
  if (projects.length === 0) {
    projects = await API.getProjects();
  }
  const project = projects.find(p => p.id === projectId);
  if (!project) {
    showToast('Project not found', 'error');
    return;
  }

  document.getElementById('kanban-project-name').textContent = project.name;
  document.getElementById('kanban-project-desc').textContent = project.description || 'No description';
  showView('kanban');
  await renderKanban();
}

async function renderKanban() {
  if (!currentProjectId) {
    if (projects.length > 0) {
      currentProjectId = projects[0].id;
    } else {
      showView('projects');
      return;
    }
  }

  try {
    tasks = await API.getTasks(currentProjectId);
    const statuses = ['todo', 'inprogress', 'completed'];

    statuses.forEach(status => {
      const col = document.getElementById(`col-${status}`);
      const projectTasks = tasks.filter(t => (t.status || 'todo') === status);
      const countEl = document.getElementById(`count-${status}`);
      if (countEl) countEl.textContent = projectTasks.length;

      if (!col) return;

      if (projectTasks.length === 0) {
        col.innerHTML = `
          <div class="h-32 border-2 border-dashed border-slate-200 rounded-xl flex items-center justify-center text-slate-400 text-xs font-medium">
            Drop tasks here
          </div>`;
        return;
      }

      col.innerHTML = projectTasks.map(t => `
        <div id="task-${t.id}" class="task-card bg-white rounded-xl border border-slate-200 p-4 shadow-sm" draggable="true" data-id="${t.id}">
          <div class="flex items-start justify-between gap-2 mb-2">
            <h5 class="font-semibold text-slate-800 text-sm leading-snug">${escapeHtml(t.title)}</h5>
            ${priorityBadge(t.priority)}
          </div>
          ${t.description ? `<p class="text-xs text-slate-500 mb-3 line-clamp-2 leading-relaxed">${escapeHtml(t.description)}</p>` : ''}
          <div class="flex items-center justify-between mt-2 pt-2 border-t border-slate-100">
            <div class="flex items-center gap-1.5">
              <span class="w-2 h-2 rounded-full ${t.assignee ? 'bg-primary-500' : 'bg-slate-300'}"></span>
              <span class="text-xs font-medium text-slate-600">${escapeHtml(t.assignee || 'Unassigned')}</span>
            </div>
            <div class="flex items-center gap-1">
              <button onclick="editTask('${t.id}')" class="p-1 rounded-md text-slate-400 hover:text-primary-600 hover:bg-primary-50 transition" title="Edit">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
              </button>
              <button onclick="deleteTask('${t.id}')" class="p-1 rounded-md text-slate-400 hover:text-red-600 hover:bg-red-50 transition" title="Delete">
                <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
              </button>
            </div>
          </div>
          <!-- Quick Move Controls -->
          <div class="mt-2.5 pt-2 border-t border-slate-100 flex gap-1">
            ${status !== 'todo' ? `<button onclick="moveTask('${t.id}','todo')" class="flex-1 text-[10px] font-semibold py-1 rounded bg-slate-100 text-slate-600 hover:bg-slate-200 transition">← To Do</button>` : ''}
            ${status !== 'inprogress' ? `<button onclick="moveTask('${t.id}','inprogress')" class="flex-1 text-[10px] font-semibold py-1 rounded bg-amber-50 text-amber-700 hover:bg-amber-100 transition">${status === 'todo' ? 'Progress →' : '← Progress'}</button>` : ''}
            ${status !== 'completed' ? `<button onclick="moveTask('${t.id}','completed')" class="flex-1 text-[10px] font-semibold py-1 rounded bg-emerald-50 text-emerald-700 hover:bg-emerald-100 transition">Done ✓</button>` : ''}
          </div>
        </div>`).join('');
    });

    // Reattach Drag Events
    bindTaskDragEvents();
  } catch (e) {
    console.error('Error rendering kanban:', e);
  }
}

async function moveTask(id, newStatus) {
  try {
    await API.updateTask(id, { status: newStatus });
    await renderKanban();
    showToast(`Task moved to ${newStatus === 'todo' ? 'To Do' : newStatus === 'inprogress' ? 'In Progress' : 'Completed'}`);
  } catch (err) {
    showToast('Failed to update task status: ' + err.message, 'error');
  }
}

async function deleteTask(id) {
  if (!confirm('Delete this task?')) return;
  try {
    await API.deleteTask(id);
    await renderKanban();
    showToast('Task removed from board');
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ========== HTML5 DRAG & DROP API ==========
let draggedTaskId = null;

function setupDragAndDrop() {
  const columns = ['todo', 'inprogress', 'completed'];
  columns.forEach(colId => {
    const col = document.getElementById(`col-${colId}`);
    if (!col) return;

    col.addEventListener('dragover', (e) => {
      e.preventDefault();
      e.dataTransfer.dropEffect = 'move';
      col.classList.add('drag-over');
    });

    col.addEventListener('dragleave', () => {
      col.classList.remove('drag-over');
    });

    col.addEventListener('drop', async (e) => {
      e.preventDefault();
      col.classList.remove('drag-over');
      if (!draggedTaskId) return;

      const targetStatus = colId;
      await moveTask(draggedTaskId, targetStatus);
      draggedTaskId = null;
    });
  });
}

function bindTaskDragEvents() {
  document.querySelectorAll('.task-card').forEach(card => {
    card.addEventListener('dragstart', (e) => {
      draggedTaskId = card.getAttribute('data-id');
      card.classList.add('dragging');
      e.dataTransfer.effectAllowed = 'move';
      e.dataTransfer.setData('text/plain', draggedTaskId);
    });

    card.addEventListener('dragend', () => {
      card.classList.remove('dragging');
    });
  });
}

// ========== TASK MODAL ==========
function openTaskModal(taskId = null) {
  editingTaskId = taskId;
  const modal = document.getElementById('task-modal');
  const form = document.getElementById('task-form');
  form.reset();
  document.getElementById('task-id').value = '';

  populateUserSelects();

  if (taskId) {
    const t = tasks.find(x => x.id === taskId);
    if (!t) return;
    document.getElementById('modal-title').textContent = 'Edit Task';
    document.getElementById('task-id').value = t.id;
    document.getElementById('task-title').value = t.title;
    document.getElementById('task-desc').value = t.description || '';
    document.getElementById('task-priority').value = t.priority || 'medium';
    document.getElementById('task-assignee').value = t.assignee || '';
    document.getElementById('task-status').value = t.status || 'todo';
  } else {
    document.getElementById('modal-title').textContent = 'Create New Task';
    document.getElementById('task-status').value = 'todo';
  }
  modal.classList.remove('hidden');
}

function closeTaskModal() {
  document.getElementById('task-modal').classList.add('hidden');
  editingTaskId = null;
}

function editTask(id) {
  openTaskModal(id);
}

async function handleTaskSubmit(e) {
  e.preventDefault();
  const title = document.getElementById('task-title').value.trim();
  if (!title || !currentProjectId) return;

  const taskData = {
    projectId: currentProjectId,
    title,
    description: document.getElementById('task-desc').value.trim(),
    priority: document.getElementById('task-priority').value,
    assignee: document.getElementById('task-assignee').value.trim(),
    status: document.getElementById('task-status').value
  };

  const existingId = document.getElementById('task-id').value;

  try {
    if (existingId) {
      await API.updateTask(existingId, taskData);
      showToast('Task updated successfully');
    } else {
      await API.createTask(taskData);
      showToast('Task added to board');
    }
    closeTaskModal();
    await renderKanban();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

// ========== USERS VIEW ==========
async function renderUsers() {
  try {
    users = await API.getUsers();
    const list = document.getElementById('users-list');
    if (!list) return;

    if (users.length === 0) {
      list.innerHTML = `<div class="px-6 py-12 text-center text-slate-400 text-sm">No team members registered yet.</div>`;
      return;
    }

    list.innerHTML = users.map(u => `
      <div class="px-6 py-4 flex items-center justify-between hover:bg-slate-50 transition">
        <div class="flex items-center gap-3">
          <div class="w-10 h-10 rounded-full bg-primary-100 text-primary-700 font-bold flex items-center justify-center text-sm">
            ${escapeHtml(u.name.charAt(0))}
          </div>
          <div>
            <p class="font-semibold text-slate-800 text-sm">${escapeHtml(u.name)}</p>
            <p class="text-xs text-slate-500">${escapeHtml(u.email)} · <span class="text-slate-600 font-medium">${escapeHtml(u.role)}</span></p>
          </div>
        </div>
        <div class="flex items-center gap-3">
          <span class="text-xs bg-slate-100 text-slate-600 px-2.5 py-1 rounded-md font-medium">${escapeHtml(u.department || 'IT')}</span>
          <button onclick="deleteUser('${u.id}')" class="p-1.5 rounded-lg text-red-500 hover:bg-red-50 transition" title="Delete member">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
          </button>
        </div>
      </div>`).join('');
  } catch (e) {
    console.error('Error rendering users:', e);
  }
}

async function handleUserSubmit(e) {
  e.preventDefault();
  const name = document.getElementById('user-name').value.trim();
  const email = document.getElementById('user-email').value.trim();
  const role = document.getElementById('user-role').value;
  const department = document.getElementById('user-dept').value.trim();

  if (!name || !email) return;

  try {
    await API.createUser({ name, email, role, department });
    document.getElementById('user-form').reset();
    showToast('Team member registered');
    await refreshAllData();
    await renderUsers();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

async function deleteUser(id) {
  if (!confirm('Remove this member?')) return;
  try {
    await API.deleteUser(id);
    showToast('Member removed');
    await refreshAllData();
    await renderUsers();
  } catch (err) {
    showToast(err.message, 'error');
  }
}

function populateUserSelects() {
  const projectOwnerSelect = document.getElementById('project-owner');
  const taskAssigneeSelect = document.getElementById('task-assignee');

  const options = users.map(u => `<option value="${escapeHtml(u.name)}">${escapeHtml(u.name)} (${escapeHtml(u.role)})</option>`).join('');

  if (projectOwnerSelect) {
    projectOwnerSelect.innerHTML = `<option value="">Select Project Owner</option>` + options;
  }
  if (taskAssigneeSelect) {
    taskAssigneeSelect.innerHTML = `<option value="">Unassigned</option>` + options;
  }
}

// ========== EVENT LISTENERS ==========
function setupEventListeners() {
  const projectForm = document.getElementById('project-form');
  if (projectForm) projectForm.addEventListener('submit', handleProjectSubmit);

  const taskForm = document.getElementById('task-form');
  if (taskForm) taskForm.addEventListener('submit', handleTaskSubmit);

  const userForm = document.getElementById('user-form');
  if (userForm) userForm.addEventListener('submit', handleUserSubmit);
}

// ========== HELPERS & UTILITIES ==========
function priorityBadge(p) {
  const priority = (p || 'medium').toLowerCase();
  const map = {
    high: 'bg-red-100 text-red-700 border-red-200',
    medium: 'bg-amber-100 text-amber-700 border-amber-200',
    low: 'bg-emerald-100 text-emerald-700 border-emerald-200'
  };
  return `<span class="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full border ${map[priority] || map.medium}">${priority}</span>`;
}

function showToast(msg, type = 'success') {
  const t = document.getElementById('toast');
  const msgEl = document.getElementById('toast-msg');
  if (!t || !msgEl) return;

  msgEl.textContent = msg;
  t.classList.remove('hidden');

  if (type === 'error') {
    t.firstElementChild.className = 'bg-red-600 text-white px-5 py-3 rounded-xl shadow-lg flex items-center gap-3 text-sm font-medium';
  } else {
    t.firstElementChild.className = 'bg-slate-800 text-white px-5 py-3 rounded-xl shadow-lg flex items-center gap-3 text-sm font-medium';
  }

  requestAnimationFrame(() => {
    t.classList.remove('translate-y-4', 'opacity-0');
  });

  setTimeout(() => {
    t.classList.add('translate-y-4', 'opacity-0');
    setTimeout(() => t.classList.add('hidden'), 300);
  }, 2400);
}

function escapeHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
