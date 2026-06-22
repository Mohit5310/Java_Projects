import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Car, PlusCircle, Trash2, ArrowLeft, FileImage, Video, Images, Edit3, XCircle, X, Gavel } from 'lucide-react';

/**
 * 🏎️ अपग्रेड: टेबल और गैलरी के अंदर टूटी हुई इमेजेस (Broken Images) को ऑथेंटिकेशन टोकन 
 * के साथ सुरक्षित स्ट्रीम करने का बुलेटप्रूफ कॉम्पोनेंट भाई
 */
const SecureTableAssetImage = ({ imageName, altText, className }) => {
    const [imageSrc, setImageSrc] = useState("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=200");

    useEffect(() => {
        const fetchImageWithAuth = async () => {
            if (!imageName) return;
            try {
                const cleanName = imageName.includes('/') ? imageName.substring(imageName.lastIndexOf('/') + 1) : imageName;
                
                const token = localStorage.getItem('vroomz_token');
                const response = await axios.get(`http://localhost:8080/api/vehicles/image/${cleanName}`, {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                });
                
                const blobUrl = URL.createObjectURL(response.data);
                setImageSrc(blobUrl);
            } catch (err) {
                console.error("Manager table asset image streaming fault:", err);
            }
        };
        fetchImageWithAuth();
    }, [imageName]);

    return <img src={imageSrc} alt={altText} className={className} />;
};

const AdminVehicleManager = () => {
    const navigate = useNavigate();
    const [isAdmin, setIsAdmin] = useState(false);
    const [loading, setLoading] = useState(true);
    const [vehicles, setVehicles] = useState([]);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const [editingVehicleId, setEditingVehicleId] = useState(null);
    const [activeGallery, setActiveGallery] = useState([]);

    const [vehicleForm, setVehicleForm] = useState({
        brand: '', model: '', basePrice: '', fuelType: 'Diesel',
        kmDriven: '', mfgYear: '', status: 'AVAILABLE', yardLocation: '',
        regNo: '', rcStatus: 'Original', videoUrl: '', auctionEndTime: '' // यहाँ जोड़ी है
    });
    
    const [selectedFile, setSelectedFile] = useState(null);
    const [extraFiles, setExtraFiles] = useState([]);

    useEffect(() => {
        const checkAuthAndFetchData = async () => {
            try {
                const token = localStorage.getItem('vroomz_token');
                if (token) {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    if (payload.role === 'ROLE_ADMIN' || payload.roles?.includes('ROLE_ADMIN')) {
                        setIsAdmin(true);
                        const response = await axios.get('http://localhost:8080/api/vehicles/all', {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        setVehicles(response.data);
                    }
                }
                setLoading(false);
            } catch (err) {
                console.error(err);
                setIsAdmin(false);
                setLoading(false);
            }
        };
        checkAuthAndFetchData();
    }, []);

    const handleInputChange = (e) => {
        setVehicleForm({ ...vehicleForm, [e.target.name]: e.target.value });
    };

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleExtraFilesChange = (e) => {
        setExtraFiles(Array.from(e.target.files));
    };

    const handleEditClick = (vehicle) => {
        window.scrollTo({ top: 0, behavior: 'smooth' }); 
        setEditingVehicleId(vehicle.id);
        setActiveGallery(vehicle.extraImages || []);
        setVehicleForm({
            brand: vehicle.brand || '',
            model: vehicle.model || '',
            basePrice: vehicle.basePrice || '',
            fuelType: vehicle.fuelType || 'Diesel',
            kmDriven: vehicle.kmDriven || '',
            mfgYear: vehicle.mfgYear || '',
            status: vehicle.status || 'AVAILABLE',
            yardLocation: vehicle.yardLocation || '',
            regNo: vehicle.regNo || '',
            rcStatus: vehicle.rcStatus || 'Original',
            videoUrl: vehicle.videoUrl || '',
            auctionEndTime: vehicle.auctionEnd ? vehicle.auctionEnd.substring(0, 16) : '' // एडिट में लोड किया
        });
        setSelectedFile(null);
        setExtraFiles([]);
    };

    const handleCancelEdit = () => {
        setEditingVehicleId(null);
        setActiveGallery([]);
        setVehicleForm({
            brand: '', model: '', basePrice: '', fuelType: 'Diesel',
            kmDriven: '', mfgYear: '', status: 'AVAILABLE', yardLocation: '',
            regNo: '', rcStatus: 'Original', videoUrl: '', auctionEndTime: ''
        });
        setSelectedFile(null);
        setExtraFiles([]);
    };

    const handleDeleteGalleryImage = async (imgUrl) => {
        if (!window.confirm('Are you sure you want to delete this X-Ray image?')) return;
        try {
            const token = localStorage.getItem('vroomz_token');
            const response = await axios.delete(`http://localhost:8080/api/vehicles/${editingVehicleId}/gallery`, {
                headers: { Authorization: `Bearer ${token}` },
                params: { imageUrl: imgUrl }
            });
            
            setActiveGallery(response.data.extraImages || []);
            setVehicles(vehicles.map(v => v.id === editingVehicleId ? response.data : v));
            setSuccess('X-Ray image deleted successfully!');
        } catch (err) {
            setError('Failed to delete gallery image.');
        }
    };

    const handleResetHighestBid = async (vehicleId, vehicleName) => {
        if (!window.confirm(`⚠️ WARNING: Are you absolutely sure you want to completely RESET all live bids for "${vehicleName}"? This will wipe the highest bid records forever.`)) return;
        
        setError('');
        setSuccess('');
        try {
            const token = localStorage.getItem('vroomz_token');
            await axios.delete(`http://localhost:8080/api/bids/vehicle/${vehicleId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            
            setSuccess(`Bidding ledger successfully reset for ${vehicleName}! Buyers can now place fresh bids. 🔨✨`);
            
            const response = await axios.get('http://localhost:8080/api/vehicles/all', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setVehicles(response.data);
        } catch (err) {
            console.error("Bid registry reset crash trace:", err);
            const errorMsg = err.response?.data?.message || err.response?.data || err.message;
            setError(`Failed to flush bidding schema records: ${errorMsg}`);
        }
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (!editingVehicleId && !selectedFile) {
            setError('Please upload a valid vehicle asset cover image.');
            return;
        }

        try {
            const token = localStorage.getItem('vroomz_token');
            const formData = new FormData();
            
            const sanitizedVehicleForm = {
                ...vehicleForm,
                basePrice: vehicleForm.basePrice ? parseFloat(vehicleForm.basePrice) : 0.0,
                mfgYear: vehicleForm.mfgYear ? parseInt(vehicleForm.mfgYear, 10) : 2024,
                kmDriven: vehicleForm.kmDriven ? String(vehicleForm.kmDriven) : '0',
                auctionEnd: vehicleForm.auctionEndTime || null // बैकएंड के लिए मैप किया
            };
            
            formData.append('vehicle', new Blob([JSON.stringify(sanitizedVehicleForm)], { type: 'application/json' }));
            
            if (selectedFile) formData.append('image', selectedFile);
            
            if (extraFiles && extraFiles.length > 0) {
                extraFiles.forEach((file) => {
                    if (file) formData.append('extraImages', file);
                });
            }

            if (editingVehicleId) {
                const response = await axios.put(`http://localhost:8080/api/vehicles/update/${editingVehicleId}`, formData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                });
                
                setSuccess('Vehicle assets successfully updated in the system schema! 🔧🎉');
                setVehicles(vehicles.map(v => v.id === editingVehicleId ? response.data : v));
                handleCancelEdit();
            } else {
                const response = await axios.post('http://localhost:8080/api/vehicles/add', formData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                });

                setSuccess('Vehicle lot successfully logged with full X-Ray assets! 🎉');
                setVehicles([...vehicles, response.data]);
                handleCancelEdit();
            }

            if(document.getElementById('imageFileInput')) document.getElementById('imageFileInput').value = '';
            if(document.getElementById('extraImagesInput')) document.getElementById('extraImagesInput').value = '';
        } catch (err) {
            console.error(err);
            setError(editingVehicleId ? 'Failed to update vehicle payload.' : 'Failed to dispatch vehicle payload.');
        }
    };

    const handleDeleteVehicle = async (id) => {
        if (!window.confirm('Are you sure you want to delete this vehicle?')) return;
        setError('');
        setSuccess('');
        try {
            const token = localStorage.getItem('vroomz_token');
            await axios.delete(`http://localhost:8080/api/vehicles/${id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSuccess('Vehicle purged successfully from the database.');
            setVehicles(vehicles.filter(v => v.id !== id));
            if (editingVehicleId === id) handleCancelEdit();
        } catch (err) {
            setError('Failed to delete asset.');
        }
    };

    if (loading) return <div className="min-h-screen bg-gray-950 flex items-center justify-center font-mono text-gray-500">Loading Console...</div>;
    if (!isAdmin) return <div className="min-h-screen bg-gray-950 text-white flex items-center justify-center font-mono">Access Denied</div>;

    return (
        <div className="min-h-screen bg-gray-950 text-white select-none pb-12">
            <nav className="bg-gray-900 border-b border-gray-800 px-6 py-4">
                <div className="max-w-7xl mx-auto flex justify-between items-center">
                    <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-xs font-mono text-gray-400 hover:text-blue-400 bg-transparent border-none cursor-pointer transition uppercase tracking-wider"><ArrowLeft className="w-4 h-4" /> Back to Yard</button>
                    <div className="font-mono text-xs font-bold bg-blue-950 text-blue-400 border border-blue-900/50 px-3 py-1.5 rounded-lg">AUCTIONEER CONSOLE</div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-6 mt-10">
                <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
                    
                    {/* Form Block */}
                    <div className="lg:col-span-5 bg-gray-900 border border-gray-800 p-6 rounded-2xl shadow-xl text-left">
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="text-lg font-bold tracking-tight flex items-center gap-2">
                                {editingVehicleId ? (
                                    <><Edit3 className="w-5 h-5 text-amber-500" /> Update Fleet Asset <span className="text-amber-500 font-mono text-sm">#{editingVehicleId}</span></>
                                ) : (
                                    <><PlusCircle className="w-5 h-5 text-blue-500" /> Log New Fleet Asset</>
                                )}
                            </h2>
                            {editingVehicleId && (
                                <button type="button" onClick={handleCancelEdit} className="flex items-center gap-1 text-[10px] font-mono text-gray-400 hover:text-red-400 bg-gray-950 border border-gray-800 px-2 py-1 rounded-md transition cursor-pointer">
                                    <XCircle className="w-3.5 h-3.5" /> Cancel Edit
                                </button>
                            )}
                        </div>
                        
                        {error && <div className="bg-red-950/40 border border-red-900/50 text-red-400 p-3 rounded-xl text-xs mb-4 font-mono">⚡ {error}</div>}
                        {success && <div className="bg-emerald-950/40 border border-emerald-900/50 text-emerald-400 p-3 rounded-xl text-xs mb-4 font-mono">✓ {success}</div>}

                        <form onSubmit={handleFormSubmit} className="space-y-4">
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Brand</label>
                                    <input type="text" name="brand" value={vehicleForm.brand} onChange={handleInputChange} placeholder="e.g., Toyota" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition" required />
                                </div>
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Model</label>
                                    <input type="text" name="model" value={vehicleForm.model} onChange={handleInputChange} placeholder="e.g., Fortuner" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition" required />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Base Price (₹)</label>
                                    <input type="number" name="basePrice" value={vehicleForm.basePrice} onChange={handleInputChange} placeholder="e.g., 1500000" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" required />
                                </div>
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">KM Driven</label>
                                    <input type="number" name="kmDriven" value={vehicleForm.kmDriven} onChange={handleInputChange} placeholder="e.g., 35000" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" required />
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Mfg Year</label>
                                    <input type="number" name="mfgYear" value={vehicleForm.mfgYear} onChange={handleInputChange} placeholder="e.g., 2022" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" required />
                                </div>
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Fuel Type</label>
                                    <select name="fuelType" value={vehicleForm.fuelType} onChange={handleInputChange} className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-gray-400 text-xs focus:outline-none focus:border-blue-500 transition font-mono">
                                        <option value="Diesel">Diesel</option>
                                        <option value="Petrol">Petrol</option>
                                        <option value="EV">EV</option>
                                    </select>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Reg No</label>
                                    <input type="text" name="regNo" value={vehicleForm.regNo} onChange={handleInputChange} placeholder="e.g., BR-06-AA-1234" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" required />
                                </div>
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">RC Status</label>
                                    <select name="rcStatus" value={vehicleForm.rcStatus} onChange={handleInputChange} className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-gray-400 text-xs focus:outline-none focus:border-blue-500 transition font-mono">
                                        <option value="Original">Original</option>
                                        <option value="Duplicate">Duplicate</option>
                                    </select>
                                </div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Yard Location</label>
                                    <input type="text" name="yardLocation" value={vehicleForm.yardLocation} onChange={handleInputChange} placeholder="e.g., Patna" className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition" required />
                                </div>
                                <div>
                                    <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono">Lot Status</label>
                                    <select name="status" value={vehicleForm.status} onChange={handleInputChange} className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-gray-400 text-xs focus:outline-none focus:border-blue-500 transition font-mono">
                                        <option value="AVAILABLE">AVAILABLE</option>
                                        <option value="SOLD">SOLD</option>
                                    </select>
                                </div>
                            </div>
                            
                            {/* AUCTION END TIME INPUT */}
                            <div>
                                <label className="block text-amber-400 text-[10px] font-bold uppercase mb-1.5 font-mono">Auction End Time</label>
                                <input type="datetime-local" name="auctionEndTime" value={vehicleForm.auctionEndTime} onChange={handleInputChange} className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" />
                            </div>

                            <div>
                                <label className="block text-gray-400 text-[10px] font-bold uppercase tracking-wider mb-1.5 font-mono flex items-center gap-1"><Video className="w-3 h-3 text-red-500" /> Inspection Video URL</label>
                                <input type="text" name="videoUrl" value={vehicleForm.videoUrl} onChange={handleInputChange} placeholder="e.g., https://www.youtube.com/watch?v=..." className="w-full px-3 py-2.5 bg-gray-950 border border-gray-800 rounded-xl text-white text-xs focus:outline-none focus:border-blue-500 transition font-mono" />
                            </div>

                            {editingVehicleId && activeGallery.length > 0 && (
                                <div className="bg-gray-950 p-3 rounded-xl border border-gray-800">
                                    <label className="block text-amber-500 text-[10px] font-bold uppercase font-mono mb-2">Active X-Ray Gallery Lots (Click X to delete)</label>
                                    <div className="grid grid-cols-4 gap-2">
                                        {activeGallery.map((img, index) => (
                                            <div key={index} className="relative aspect-video rounded-md overflow-hidden border border-gray-800 group flex items-center justify-center bg-gray-900">
                                                <SecureTableAssetImage imageName={img} altText={`Gallery asset ${index}`} className="w-full h-full object-cover" />
                                                <button type="button" onClick={() => handleDeleteGalleryImage(img)} className="absolute top-1 right-1 bg-red-600 hover:bg-red-500 text-white p-0.5 rounded-full transition cursor-pointer z-10">
                                                    <X className="w-3.5 h-3.5" />
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            )}

                            <div className="border border-dashed border-gray-800 bg-gray-950/60 p-3 rounded-xl text-center">
                                <label className="block text-gray-500 text-[9px] font-mono mb-1 flex items-center justify-center gap-1"><FileImage className="w-3.5 h-3.5" /> {editingVehicleId ? 'Replace Cover Image (Optional)' : 'Cover Main Image'}</label>
                                <input id="imageFileInput" type="file" accept="image/*" onChange={handleFileChange} className="block w-full text-xs text-gray-500 file:cursor-pointer" required={!editingVehicleId} />
                            </div>

                            <div className="border border-dashed border-gray-800 bg-gray-950/60 p-3 rounded-xl text-center">
                                <label className="block text-gray-500 text-[9px] font-mono mb-1 flex items-center justify-center gap-1"><Images className="w-3.5 h-3.5 text-emerald-500" /> {editingVehicleId ? 'Add More X-Ray Images' : 'Additional X-Ray Images (Max 6)'}</label>
                                <input id="extraImagesInput" type="file" accept="image/*" multiple onChange={handleExtraFilesChange} className="block w-full text-xs text-gray-500 file:cursor-pointer" />
                                {extraFiles.length > 0 && <p className="text-[10px] text-emerald-400 font-mono mt-1">{extraFiles.length} files selected</p>}
                            </div>

                            <button type="submit" className={`w-full text-white font-bold font-mono text-xs py-3.5 rounded-xl uppercase tracking-widest transition mt-2 shadow-lg ${editingVehicleId ? 'bg-amber-600 hover:bg-amber-500' : 'bg-blue-600 hover:bg-blue-500'}`}>{editingVehicleId ? 'Update Asset Setup 🔧' : 'Commit Asset Data ⚡'}</button>
                        </form>
                    </div>

                    {/* Registry List Table */}
                    <div className="lg:col-span-7 bg-gray-900 border border-gray-800 p-6 rounded-2xl shadow-xl text-left">
                        <h2 className="text-lg font-bold mb-6 flex items-center gap-2"><Car className="w-5 h-5 text-emerald-500" /> Active Inventory Registry Table</h2>
                        <div className="space-y-3 max-h-[750px] overflow-y-auto pr-2">
                            {vehicles.map((v) => (
                                <div key={v.id} className={`bg-gray-950 p-4 rounded-xl border flex items-center justify-between transition ${editingVehicleId === v.id ? 'border-amber-500/60 shadow-lg shadow-amber-500/5' : 'border-gray-800/60'}`}>
                                    <div className="flex items-center gap-4">
                                        <div className="w-16 h-10 bg-gray-900 rounded-md overflow-hidden border border-gray-800 flex items-center justify-center">
                                            <SecureTableAssetImage imageName={v.imageName} altText={v.model} className="w-full h-full object-cover" />
                                        </div>
                                        <div>
                                            <h4 className="text-xs font-bold text-white m-0">{v.brand} {v.model} ({v.mfgYear})</h4>
                                            <p className="text-[9px] font-mono text-gray-500 m-0 mt-1 uppercase">₹{v.basePrice?.toLocaleString('en-IN')} | {v.regNo || 'NO REG'} | RC: {v.rcStatus || 'N/A'} | Yard: {v.yardLocation || 'Global'}</p>
                                        </div>
                                    </div>
                                    
                                    <div className="flex items-center gap-2">
                                        <button onClick={() => handleResetHighestBid(v.id, `${v.brand} ${v.model}`)} className="p-2 bg-gray-900 hover:bg-red-950/40 text-gray-500 hover:text-red-500 border border-gray-800 rounded-xl transition" title="Reset Highest Bid Ledger">
                                            <Gavel className="w-3.5 h-3.5" />
                                        </button>
                                        <button onClick={() => handleEditClick(v)} className={`p-2 border rounded-xl transition ${editingVehicleId === v.id ? 'bg-amber-950/40 text-amber-400 border-amber-900/50' : 'bg-gray-900 text-gray-400 hover:text-amber-400 border-gray-800'}`}><Edit3 className="w-3.5 h-3.5" /></button>
                                        <button onClick={() => handleDeleteVehicle(v.id)} className="p-2 bg-gray-900 hover:bg-red-950/40 text-gray-500 hover:text-red-400 border border-gray-800 rounded-xl transition"><Trash2 className="w-3.5 h-3.5" /></button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default AdminVehicleManager;