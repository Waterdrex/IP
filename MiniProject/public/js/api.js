/**
 * REST API Client for TCET Project Management System.
 * Connects frontend DOM actions to the Java HttpServer backend.
 */
const API_BASE = window.location.origin.includes('http') ? '/api' : 'http://localhost:8080/api';

const API = {
  // Check backend server health
  async checkHealth() {
    try {
      const res = await fetch(`${API_BASE}/health`, { method: 'GET' });
      return res.ok;
    } catch (e) {
      return false;
    }
  },

  // Dashboard Stats
  async getStats() {
    const res = await fetch(`${API_BASE}/stats`);
    if (!res.ok) throw new Error('Failed to load stats');
    return res.json();
  },

  // Projects API
  async getProjects() {
    const res = await fetch(`${API_BASE}/projects`);
    if (!res.ok) throw new Error('Failed to fetch projects');
    return res.json();
  },

  async createProject(project) {
    const res = await fetch(`${API_BASE}/projects`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(project)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to create project');
    }
    return res.json();
  },

  async deleteProject(id) {
    const res = await fetch(`${API_BASE}/projects/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete project');
    return res.json();
  },

  // Tasks API
  async getTasks(projectId = '') {
    const url = projectId ? `${API_BASE}/tasks?projectId=${encodeURIComponent(projectId)}` : `${API_BASE}/tasks`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('Failed to fetch tasks');
    return res.json();
  },

  async createTask(task) {
    const res = await fetch(`${API_BASE}/tasks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(task)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to create task');
    }
    return res.json();
  },

  async updateTask(id, task) {
    const res = await fetch(`${API_BASE}/tasks/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(task)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to update task');
    }
    return res.json();
  },

  async deleteTask(id) {
    const res = await fetch(`${API_BASE}/tasks/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete task');
    return res.json();
  },

  // Users API
  async getUsers() {
    const res = await fetch(`${API_BASE}/users`);
    if (!res.ok) throw new Error('Failed to fetch users');
    return res.json();
  },

  async createUser(user) {
    const res = await fetch(`${API_BASE}/users`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(user)
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to create user');
    }
    return res.json();
  },

  async deleteUser(id) {
    const res = await fetch(`${API_BASE}/users/${id}`, { method: 'DELETE' });
    if (!res.ok) throw new Error('Failed to delete user');
    return res.json();
  }
};
