import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    // State to toggle between Login (true) and Signup (false) views
    const [isLogin, setIsLogin] = useState(true);
    
    // Comprehensive form state covering both login and registration inputs
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        role: 'ROLE_USER' // Default role for registering users
    });
    
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const navigate = useNavigate();

    // Dynamically update individual form values inside the state object
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // Toggle form type and reset messaging states
    const toggleFormMode = () => {
        setIsLogin(!isLogin);
        setError('');
        setSuccess('');
        setFormData({ name: '', email: '', password: '', role: 'ROLE_USER' });
    };

    // Form submit controller dealing with discrete API routes based on active state
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (isLogin) {
            // LOGIN FLOW: Dispatching credentials to the API Gateway Auth endpoint
            try {
                const payload = { email: formData.email, password: formData.password };
                const response = await axios.post('http://localhost:8080/api/auth/login', payload);
                const token = response.data.token;
                
                localStorage.setItem('vroomz_token', token);
                alert('Access granted! Welcome to the bidding arena. 🏎️');
                navigate('/dashboard');
            } catch (err) {
                setError('Authentication failed. Invalid email identifier or security password.');
            }
        } else {
            // SIGNUP FLOW: Dispatching structured entity mapping to USER-SERVICE registration endpoint
            try {
                const response = await axios.post('http://localhost:8080/api/auth/register', formData);
                setSuccess('Registration completed successfully! You can now log in. 🎉');
                setIsLogin(true); // Auto-route user back to login view on success
            } catch (err) {
                setError('Registration failed. The email address might already be registered.');
            }
        }
    };

    return (
        <div className="relative flex items-center justify-center min-h-screen bg-gray-950 px-4 w-full overflow-hidden select-none">
            
            {/* Premium Matrix Grid Background */}
            <div className="absolute inset-0 bg-[linear-gradient(to_right,#0f172a_1px,transparent_1px),linear-gradient(to_bottom,#0f172a_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_50%,#000_70%,transparent_100%)] opacity-40"></div>
            
            {/* Ambient Supercar Neon Lighting Glows */}
            <div className="absolute top-1/3 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[550px] h-[550px] bg-gradient-to-tr from-blue-600/10 to-transparent rounded-full blur-[140px] pointer-events-none"></div>

            {/* Glassmorphic Ignition Bidding Panel */}
            <div className="relative bg-gray-900/90 backdrop-blur-2xl p-10 rounded-[32px] shadow-[0_25px_70px_-15px_rgba(0,0,0,0.7)] max-w-md w-full border border-gray-800 text-left z-10 transition-all duration-300">
                
                {/* Top Racing Accent Border */}
                <div className="absolute top-0 left-6 right-6 h-[2px] bg-gradient-to-r from-transparent via-blue-500 to-transparent opacity-80"></div>

                {/* Branding Section */}
                <div className="text-center mb-8 relative">
                    <div className="inline-flex items-center gap-2 px-3 py-1 rounded-md text-[10px] font-bold tracking-widest bg-blue-950 text-blue-400 border border-blue-900/50 mb-4 uppercase font-mono">
                        <span className="w-1.5 h-1.5 rounded-full bg-blue-500 animate-ping"></span>
                        {isLogin ? 'Secure Authorization Gateway' : 'Create Bidding Credentials'}
                    </div>
                    <h1 className="text-4xl font-extrabold text-white tracking-tight m-0">
                        VROOMZ<span className="text-blue-500 font-black">.</span>
                    </h1>
                </div>

                {/* Alert Notifications UI Handler */}
                {error && (
                    <div className="bg-red-950/40 border border-red-900/50 text-red-400 px-4 py-3.5 rounded-xl text-xs font-medium mb-6 flex items-center gap-2 font-mono">
                        <span className="text-red-500">⚡</span> {error}
                    </div>
                )}
                {success && (
                    <div className="bg-emerald-950/40 border border-emerald-900/50 text-emerald-400 px-4 py-3.5 rounded-xl text-xs font-medium mb-6 flex items-center gap-2 font-mono">
                        <span className="text-emerald-500">✓</span> {success}
                    </div>
                )}

                {/* Interactive Dynamic Form Layer */}
                <form onSubmit={handleSubmit} className="space-y-5" autoComplete="off">
                    
                    {/* CONDITIONAL SIGNUP FIELD: Full Name */}
                    {!isLogin && (
                        <div>
                            <label className="block text-gray-400 text-[11px] font-bold uppercase tracking-widest mb-2 font-mono">
                                Full Name
                            </label>
                            <input 
                                type="text" 
                                name="name"
                                value={formData.name}
                                onChange={handleChange}
                                className="w-full px-4 py-3.5 bg-gray-950/80 border border-gray-800 rounded-xl text-white placeholder-gray-700 font-medium text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition duration-200"
                                placeholder="Enter your full name" 
                                required
                            />
                        </div>
                    )}

                    {/* Unified User Identifier Field */}
                    <div>
                        <label className="block text-gray-400 text-[11px] font-bold uppercase tracking-widest mb-2 font-mono">
                            Email Address
                        </label>
                        <input 
                            type="email" 
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className="w-full px-4 py-3.5 bg-gray-950/80 border border-gray-800 rounded-xl text-white placeholder-gray-700 font-medium text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition duration-200"
                            placeholder="Enter your registered email" 
                            autoComplete="new-username"
                            required
                        />
                    </div>

                    {/* CONDITIONAL SIGNUP FIELD: Role Access Selector */}
                    {!isLogin && (
                        <div>
                            <label className="block text-gray-400 text-[11px] font-bold uppercase tracking-widest mb-2 font-mono">
                                Account Access Tier
                            </label>
                            <select 
                                name="role"
                                value={formData.role}
                                onChange={handleChange}
                                className="w-full px-4 py-3.5 bg-gray-950/80 border border-gray-800 rounded-xl text-gray-400 font-medium text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition duration-200 font-mono"
                            >
                                <option value="ROLE_USER">STANDARD BIDDER</option>
                                <option value="ROLE_ADMIN">YARD AUCTIONEER</option>
                            </select>
                        </div>
                    )}

                    {/* Secure Password Input Field */}
                    <div>
                        <label className="block text-gray-400 text-[11px] font-bold uppercase tracking-widest mb-2 font-mono">
                            Security Password
                        </label>
                        <input 
                            type="password" 
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            className="w-full px-4 py-3.5 bg-gray-950/80 border border-gray-800 rounded-xl text-white placeholder-gray-700 font-medium text-sm focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition duration-200"
                            placeholder="••••••••••••" 
                            autoComplete="new-password"
                            required
                        />
                    </div>

                    {/* Dynamic Action Submission Engine Trigger */}
                    <div className="pt-2">
                        <button 
                            type="submit" 
                            className="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold text-xs py-4 px-4 rounded-xl transition duration-200 shadow-xl shadow-blue-600/10 tracking-widest uppercase font-mono transform active:scale-[0.99]"
                        >
                            {isLogin ? 'Start Engine & Enter 🏁' : 'Register Vehicle Asset Account 🛠️'}
                        </button>
                    </div>
                </form>

                {/* Switch Form Strategy Controller Button */}
                <div className="mt-6 text-center">
                    <button 
                        onClick={toggleFormMode}
                        className="text-xs font-mono text-gray-500 hover:text-blue-400 bg-transparent border-none cursor-pointer transition focus:outline-none uppercase tracking-wider"
                    >
                        {isLogin ? "Don't have an account? Sign Up" : "Already registered? Sign In"}
                    </button>
                </div>

                {/* System Technical Metadata Footer */}
                <div className="mt-8 pt-4 border-t border-gray-800/40 flex justify-between items-center text-[10px] text-gray-600 font-mono tracking-wider">
                    <span>STATUS: ONLINE</span>
                    <span>VROOMZ CORE SECURE V2.0</span>
                </div>
            </div>
        </div>
    );
};

export default Login;