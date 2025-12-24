import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // Vite proxy will handle forwarding to Backend
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getLostItems = () => api.get('/items?type=lost');
export const reportLostItem = (data) => api.post('/items/lost', data);

export const getFoundItems = () => api.get('/items?type=found');
export const reportFoundItem = (data) => api.post('/items/found', data);

export const searchItems = (params) => 
  api.get('/items/search', { params });

// Auth methods
export const login = (credentials) => api.post('/auth/login', credentials);
export const signup = (userData) => api.post('/auth/signup', userData);
export const logout = () => api.post('/auth/logout');
export const getMe = () => api.get('/auth/me');

export default api;
