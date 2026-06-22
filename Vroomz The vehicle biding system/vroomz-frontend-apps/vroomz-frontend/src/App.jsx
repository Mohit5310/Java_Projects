import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import AdminVehicleManager from './pages/AdminVehicleManager';
import ProfilePage from './pages/ProfilePage';

// प्रोटेक्टेड राउट कॉम्पोनेंट
const ProtectedRoute = ({ children }) => {
    const token = localStorage.getItem('vroomz_token');
    return token ? children : <Navigate to="/" />;
};

function App() {
  return (
    <Router>
      <Routes>
        {/* 1. लॉगिन पेज (Default path) */}
        <Route path="/" element={<Login />} />
        
        {/* 2. डैशबोर्ड (Protected) */}
        <Route path="/dashboard" element={
            <ProtectedRoute>
                <Dashboard />
            </ProtectedRoute>
        } />

        {/* 3. एडमिन व्हीकल मैनेजमेंट */}
        <Route path="/admin/vehicles" element={
            <ProtectedRoute>
                <AdminVehicleManager />
            </ProtectedRoute>
        } />

        {/* 4. प्रोफाइल पेज (Protected) */}
        <Route path="/profile" element={
            <ProtectedRoute>
                <ProfilePage />
            </ProtectedRoute>
        } />

        {/* गलत यूआरएल डालने पर वापस लॉगिन पर भेज देगा */}
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  );
}

export default App;