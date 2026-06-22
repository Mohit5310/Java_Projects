import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, User, Wallet, ShieldCheck, CreditCard, Loader2, AlertTriangle, Mail, Phone, FileText } from 'lucide-react';

const ProfilePage = () => {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUserData = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('vroomz_token');
                if (!token) {
                    navigate('/');
                    return;
                }
                
                // ProfilePage.jsx में यह लाइन बदलें:
const res = await axios.get('http://localhost:8080/api/user-service/api/user/me', {
    headers: { Authorization: `Bearer ${token}` }
});
                setUser(res.data);
            } catch (err) {
                console.error("Profile Fetch Error:", err);
                setError("Failed to load profile. Please ensure the User Service is running at port 8080.");
            } finally {
                setLoading(false);
            }
        };
        fetchUserData();
    }, [navigate]);

    if (loading) return (
        <div className="min-h-screen bg-gray-950 text-white flex flex-col items-center justify-center">
            <Loader2 className="animate-spin w-10 h-10 text-blue-500 mb-4" />
            <p className="font-mono text-sm">Syncing user ledger...</p>
        </div>
    );

    if (error) return (
        <div className="min-h-screen bg-gray-950 text-red-500 flex flex-col items-center justify-center p-6 text-center">
            <AlertTriangle className="w-16 h-16 mb-4" />
            <p className="font-mono text-lg">{error}</p>
            <button onClick={() => navigate('/dashboard')} className="mt-6 bg-gray-800 px-6 py-2 rounded-xl text-white">Return to Dashboard</button>
        </div>
    );

    // Aadhaar number रेडैक्शन लॉजिक (सेंसिटिव डेटा सुरक्षा के लिए)
    const redactAadhaar = (val) => {
        if (!val) return "Not Provided";
        return "**** **** " + val.slice(-4);
    };

    return (
        <div className="min-h-screen bg-gray-950 p-6 md:p-10 text-white">
            <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-400 hover:text-white mb-8 font-mono text-xs uppercase transition">
                <ArrowLeft className="w-4 h-4" /> Back to Dashboard
            </button>
            
            <header className="mb-10">
                <h1 className="text-4xl font-black tracking-tight">My Account Settings</h1>
                <p className="text-gray-500 mt-2 font-mono text-sm">Manage your personal identification and wallet preferences.</p>
            </header>
            
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
                {/* 👤 Personal Details Card */}
                <div className="bg-gray-900 p-8 rounded-3xl border border-gray-800 shadow-xl">
                    <h2 className="text-xl font-bold mb-8 flex items-center gap-3"><User className="text-blue-500"/> Personal Identity</h2>
                    <div className="space-y-6">
                        <Field label="Full Name" value={user.fullName} icon={<User className="w-4 h-4" />} />
                        <Field label="Email Address" value={user.email} icon={<Mail className="w-4 h-4" />} />
                        <Field label="Mobile Number" value={user.mobile} icon={<Phone className="w-4 h-4" />} />
                    </div>
                </div>

                {/* 🛡️ KYC & Wallet Card */}
                <div className="bg-gray-900 p-8 rounded-3xl border border-gray-800 shadow-xl">
                    <h2 className="text-xl font-bold mb-8 flex items-center gap-3"><ShieldCheck className="text-emerald-500"/> Financial & KYC Status</h2>
                    <div className="space-y-6">
                        <Field label="PAN Card Details" value={user.panCard || "Not Provided"} icon={<FileText className="w-4 h-4" />} />
                        <Field label="Aadhaar Verification" value={redactAadhaar(user.aadharNo)} icon={<ShieldCheck className="w-4 h-4" />} />
                        
                        <div className="bg-gray-950 p-6 rounded-2xl border border-gray-800 flex justify-between items-center">
                            <div>
                                <span className="text-gray-400 text-[10px] uppercase font-mono block mb-1">Available Wallet Balance</span>
                                <span className="text-3xl font-black text-emerald-400">₹{user.walletBalance?.toLocaleString()}</span>
                            </div>
                            <CreditCard className="w-8 h-8 text-emerald-700" />
                        </div>

                        <div className={`p-4 rounded-xl text-center font-bold text-xs uppercase tracking-widest border ${user.isVerified ? 'bg-emerald-950/30 text-emerald-400 border-emerald-900/50' : 'bg-red-950/30 text-red-400 border-red-900/50'}`}>
                            {user.isVerified ? '✓ Account Verified' : '⚠ KYC Pending - Upload Documents'}
                        </div>
                    </div>
                </div>
            </div>
            
            <footer className="mt-12 text-center text-gray-600 font-mono text-[10px]">
                VROOMZ FLEET INFRA // SECURE DATA PORTAL // SESSION ID: {localStorage.getItem('vroomz_token')?.slice(-10)}
            </footer>
        </div>
    );
};

const Field = ({ label, value, icon }) => (
    <div className="group">
        <label className="text-[10px] text-gray-500 uppercase font-mono tracking-wider flex items-center gap-2 mb-2">
            {icon} {label}
        </label>
        <div className="p-4 bg-gray-950 rounded-2xl border border-gray-800 group-hover:border-blue-900/50 transition font-mono text-sm">
            {value}
        </div>
    </div>
);

export default ProfilePage;