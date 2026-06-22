import axios from 'axios';

// 🚀 यह सीधे तुम्हारे स्प्रिंग बूट के API-GATEWAY (8080) से कनेक्ट करेगा भाई
const API = axios.create({
    baseURL: 'http://localhost:8080', 
});

// 🔐 हर रिक्वेस्ट के साथ ऑटोमैटिकली JWT टोकन भेजने का लॉजिक
API.interceptors.request.use((config) => {
    const token = localStorage.getItem('vroomz_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

export default API;