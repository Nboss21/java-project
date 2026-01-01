import axios from 'axios';

const api = axios.create({
  baseURL: '/api', // Vite proxy will handle forwarding to Backend
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add a request interceptor to inject the User ID
api.interceptors.request.use((config) => {
    const user = localStorage.getItem('user');
    if (user) {
        const userData = JSON.parse(user);
        if (userData && userData.id) {
            config.headers['X-User-Id'] = userData.id;
        }
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export const getLostItems = () => api.get('/items?type=lost');
export const reportLostItem = (data) => api.post('/items/lost', data);

export const getFoundItems = () => api.get('/items?type=found');
export const reportFoundItem = (data) => api.post('/items/found', data);

export const searchItems = (params) => 
  api.get('/items/search', { params });

export default api;
