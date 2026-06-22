import React, { useState, useEffect } from 'react';
import axiosOriginal from 'axios'; 
import { useNavigate } from 'react-router-dom';
import { LogOut, Car, Timer, Gavel, Fuel, Gauge, Sliders, AlertCircle, Settings, MapPin, X, Trophy, CreditCard, Video, ChevronLeft, ChevronRight, User } from 'lucide-react';

/**
 * ⏱️ LIVE AUCTION TIMER ENGINE
 */
const LiveAuctionTimer = ({ endTime }) => {
    const [timeLeft, setTimeLeft] = useState('...');
    useEffect(() => {
        if (!endTime) { setTimeLeft("No Time"); return; }
        const interval = setInterval(() => {
            const diff = new Date(endTime) - new Date();
            if (diff <= 0) { setTimeLeft("🔥 CLOSED"); clearInterval(interval); }
            else {
                const h = Math.floor(diff / (1000 * 60 * 60));
                const m = Math.floor((diff / (1000 * 60)) % 60);
                const s = Math.floor((diff / 1000) % 60);
                setTimeLeft(`${h}h ${m}m ${s}s`);
            }
        }, 1000);
        return () => clearInterval(interval);
    }, [endTime]);
    return <span className="font-mono">{timeLeft}</span>;
};

/**
 * 🏎️ सुरक्षित तरीके से इमेज स्ट्रीम करने का कॉम्पोनेंट (टोकन के साथ)
 */
const SecureVehicleImage = ({ imageName, altText, className }) => {
    const [imageSrc, setImageSrc] = useState("https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&q=80&w=800");

    useEffect(() => {
        const fetchImageWithAuth = async () => {
            if (!imageName) return;
            try {
                const cleanName = imageName.includes('/') ? imageName.substring(imageName.lastIndexOf('/') + 1) : imageName;
                const token = localStorage.getItem('vroomz_token');
                const response = await axiosOriginal.get(`http://localhost:8080/api/vehicles/image/${cleanName}`, {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                });
                const blobUrl = URL.createObjectURL(response.data);
                setImageSrc(blobUrl);
            } catch (err) {
                console.error("Secure image streaming execution fault:", err);
            }
        };
        fetchImageWithAuth();
    }, [imageName]);

    return <img src={imageSrc} alt={altText} className={className} />;
};

/**
 * 🔨 Component to track the highest recorded bid
 */
const LiveHighestBid = ({ vehicleId, refreshTrigger, onHighestBidFetch }) => {
    const [highestBid, setHighestBid] = useState(null);

    useEffect(() => {
        const fetchHighestBid = async () => {
            try {
                const token = localStorage.getItem('vroomz_token');
                const response = await axiosOriginal.get(`http://localhost:8080/api/bids/vehicle/${vehicleId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                if (response.data && response.data.length > 0) {
                    const maxBid = response.data.reduce((max, bid) => bid.bidAmount > max ? bid.bidAmount : max, 0);
                    setHighestBid(maxBid);
                    if (onHighestBidFetch) onHighestBidFetch(maxBid);
                } else {
                    setHighestBid(null);
                    if (onHighestBidFetch) onHighestBidFetch(0);
                }
            } catch (err) {
                console.error(err);
            }
        };
        fetchHighestBid();
    }, [vehicleId, refreshTrigger]);

    return (
        <div className="bg-blue-950/40 border border-blue-900/40 rounded-xl p-2.5 flex items-center justify-between mt-3">
            <span className="text-[11px] font-mono font-bold text-blue-400 flex items-center gap-1"><Trophy className="w-3.5 h-3.5 text-amber-400" /> CURRENT HIGHEST BID:</span>
            <span className="text-sm font-black font-mono text-amber-400">{highestBid ? `₹${highestBid.toLocaleString('en-IN')}` : 'No Active Bids'}</span>
        </div>
    );
};

/**
 * 📸 गाड़ियों का पूरा एक्सरे करने के लिए मल्टी-इमेज गैलरी स्लाइडर (Carousel)
 */
const VehicleImageGallery = ({ mainImage, extraImages, altText }) => {
    const allImages = [mainImage, ...(extraImages || [])].filter(Boolean);
    const [currentIndex, setCurrentIndex] = useState(0);

    const handlePrev = (e) => {
        e.stopPropagation();
        setCurrentIndex((prev) => (prev === 0 ? allImages.length - 1 : prev - 1));
    };

    const handleNext = (e) => {
        e.stopPropagation();
        setCurrentIndex((prev) => (prev === allImages.length - 1 ? 0 : prev + 1));
    };

    return (
        <div className="relative w-full h-full bg-gray-950 group/gallery">
            <SecureVehicleImage imageName={allImages[currentIndex]} altText={altText} className="w-full h-full object-cover transition duration-500" />
            {allImages.length > 1 && (
                <>
                    <button onClick={handlePrev} className="absolute left-2 top-1/2 -translate-y-1/2 bg-gray-950/70 hover:bg-gray-900 text-white p-1 rounded-full border border-gray-800 opacity-0 group-hover/gallery:opacity-100 transition cursor-pointer z-10"><ChevronLeft className="w-4 h-4" /></button>
                    <button onClick={handleNext} className="absolute right-2 top-1/2 -translate-y-1/2 bg-gray-950/70 hover:bg-gray-900 text-white p-1 rounded-full border border-gray-800 opacity-0 group-hover/gallery:opacity-100 transition cursor-pointer z-10"><ChevronRight className="w-4 h-4" /></button>
                    <div className="absolute bottom-2 left-1/2 -translate-x-1/2 flex gap-1 z-10">
                        {allImages.map((_, idx) => <div key={idx} className={`w-1.5 h-1.5 rounded-full transition ${currentIndex === idx ? 'bg-blue-500 w-3' : 'bg-gray-600'}`} />)}
                    </div>
                </>
            )}
        </div>
    );
};

const Dashboard = () => {
    const [vehicles, setVehicles] = useState([]);
    const [filteredVehicles, setFilteredVehicles] = useState([]); 
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [isAdmin, setIsAdmin] = useState(false);
    const [currentUserEmail, setCurrentUserEmail] = useState('');
    const [refreshBids, setRefreshBids] = useState(0); 
    
    const [showFilterDropdown, setShowFilterDropdown] = useState(false);
    const [selectedYard, setSelectedYard] = useState('ALL');
    const [availableYards, setAvailableYards] = useState([]);

    const navigate = useNavigate();

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [currentVehicleHighestBid, setCurrentVehicleHighestBid] = useState(0); 
    const [bidAmount, setBidAmount] = useState('');
    const [bidError, setBidError] = useState('');
    const [bidSuccess, setBidSuccess] = useState('');
    const [submittingBid, setSubmittingBid] = useState(false);
    const [paymentProcessing, setPaymentProcessing] = useState(false);

    const [isVideoModalOpen, setIsVideoModalOpen] = useState(false);
    const [activeVideoUrl, setActiveVideoUrl] = useState('');
    const [activeVehicleName, setActiveVehicleName] = useState('');

    useEffect(() => {
        const fetchLiveVehicles = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem('vroomz_token');
                if (token) {
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    setCurrentUserEmail(payload.sub || payload.email);
                    if (payload.role === 'ROLE_ADMIN' || payload.roles?.includes('ROLE_ADMIN')) {
                        setIsAdmin(true);
                    }
                }
                const response = await axiosOriginal.get('http://localhost:8080/api/vehicles/all', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setVehicles(response.data);
                setFilteredVehicles(response.data); 

                const yards = response.data
                    .map(v => v.yardLocation)
                    .filter(Boolean)
                    .map(y => y.trim());
                const uniqueYards = [...new Set(yards)];
                setAvailableYards(uniqueYards);

                setLoading(false);
            } catch (err) {
                setError('Failed to pull system inventory ledger records.');
                setLoading(false);
            }
        };
        fetchLiveVehicles();
    }, []);

    useEffect(() => {
        if (selectedYard === 'ALL') {
            setFilteredVehicles(vehicles);
        } else {
            const result = vehicles.filter(vehicle => 
                vehicle.yardLocation && 
                vehicle.yardLocation.trim().toLowerCase().includes(selectedYard.toLowerCase().trim())
            );
            setFilteredVehicles(result);
        }
    }, [selectedYard, vehicles]);

    const handleLogout = () => {
        localStorage.removeItem('vroomz_token');
        window.location.href = '/';
    };

    const openBidModal = (vehicle) => {
        setSelectedVehicle(vehicle);
        setBidAmount(''); 
        setBidError('');
        setBidSuccess('');
        setIsModalOpen(true);
    };

    const getEmbeddableYoutubeUrl = (url) => {
        if (!url) return '';
        if (url.includes('.mp4') || url.startsWith('/') || url.includes('/api/vehicles/video/')) return url;
        if (url.includes('youtube.com/embed/')) return url;
        let videoId = '';
        const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
        const match = url.match(regExp);
        if (match && match[2].length === 11) {
            videoId = match[2];
            return `https://www.youtube.com/embed/${videoId}?autoplay=1&rel=0`;
        }
        return url;
    };

    const openVideoPlayer = (videoUrl, brand, model) => {
        if (!videoUrl || videoUrl.trim() === "") {
            alert("⚠️ This vehicle doesn't have an inspection video uploaded yet.");
            return;
        }
        setActiveVideoUrl(getEmbeddableYoutubeUrl(videoUrl));
        setActiveVehicleName(`${brand} ${model}`);
        setIsVideoModalOpen(true);
    };

    const handlePlaceBidSubmit = async (e) => {
        e.preventDefault();
        setBidError('');
        setBidSuccess('');

        const inputBid = parseFloat(bidAmount);
        const baseValue = selectedVehicle.basePrice || 0;

        if (!bidAmount || inputBid <= baseValue) {
            setBidError(`Bidding amount must be strictly greater than the Base Value of ₹${baseValue.toLocaleString('en-IN')}.`);
            return;
        }

        if (currentVehicleHighestBid > 0 && inputBid <= currentVehicleHighestBid) {
            setBidError(`Bidding Failed! Your bid must be higher than the current highest bid of ₹${currentVehicleHighestBid.toLocaleString('en-IN')}.`);
            return;
        }

        try {
            setSubmittingBid(true);
            const token = localStorage.getItem('vroomz_token');
            const bidPayload = { carId: selectedVehicle.id, userId: currentUserEmail || 'user@vroomz.com', bidAmount: inputBid };
            
            const response = await axiosOriginal.post('http://localhost:8080/api/bids/place', bidPayload, { headers: { Authorization: `Bearer ${token}` } });
            
            if (typeof response.data === 'string' && response.data.includes("Error")) {
                setBidError(response.data);
            } else {
                setBidSuccess('Success! Your higher bid has been registered into cluster ledger.');
                setRefreshBids(prev => prev + 1);
                setTimeout(() => setIsModalOpen(false), 1500);
            }
        } catch (err) {
            setBidError('Gateway edge rejected the live bidding request sequence.');
        } finally { setSubmittingBid(false); }
    };

    const initSecureCheckoutPayment = async (vehicle) => {
        try {
            setPaymentProcessing(true);
            const token = localStorage.getItem('vroomz_token');
            let finalPriceInRupees = vehicle.basePrice ? parseFloat(vehicle.basePrice) : 3000000.0;
            
            try {
                const bidResponse = await axiosOriginal.get(`http://localhost:8080/api/bids/vehicle/${vehicle.id}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                
                if (bidResponse.data && bidResponse.data.length > 0) {
                    const maxBid = bidResponse.data.reduce((max, bid) => bid.bidAmount > max ? bid.bidAmount : max, 0);
                    if (maxBid > 0) {
                        finalPriceInRupees = maxBid;
                    }
                }
            } catch (bidErr) {
                console.error("Highest bid fetch failed:", bidErr);
            }

            let finalAmountInPaise = finalPriceInRupees * 100;

            const options = {
                key: "rzp_test_T0QfAmUyD2FIb7", 
                amount: finalAmountInPaise, 
                currency: "INR",
                name: "VROOMZ FLEET INFRA",
                description: `Secure Escrow Clearing for ${vehicle.brand} ${vehicle.model}`,
                handler: async function (checkoutResult) {
                    alert("✓ Verification Status: Mock Sandbox Payment Successfully Logged into Registry!");
                },
                prefill: {
                    email: currentUserEmail || "bidder@vroomz.com",
                    contact: "9999999999"
                },
                theme: { color: "#2563eb" } 
            };

            const rzpWindow = new window.Razorpay(options);
            rzpWindow.open();
        } catch (err) {
            console.error("Razorpay init error:", err);
        } finally {
            setPaymentProcessing(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-950 text-white select-none">
            <nav className="sticky top-0 bg-gray-900/80 backdrop-blur-md border-b border-gray-800/60 px-6 py-4 z-50">
                <div className="max-w-7xl mx-auto flex justify-between items-center">
                    <div className="flex items-center gap-2">
                        <Car className="w-6 h-6 text-blue-500" /><span className="text-2xl font-black tracking-tight">VROOMZ<span className="text-blue-500">.</span></span>
                    </div>
                    <div className="flex items-center gap-4">
                        {/* प्रोफाइल बटन */}
                        <button 
    onClick={() => {
        console.log("Navigating to profile...");
        navigate('/profile');
    }} 
    className="bg-blue-600 px-4 py-2 rounded-xl text-xs font-mono hover:bg-blue-500 transition cursor-pointer"
>
    Go to Profile
</button>
                        
                        {isAdmin && <button onClick={() => navigate('/admin/vehicles')} className="flex items-center gap-1.5 bg-blue-600 hover:bg-blue-500 text-white font-mono text-xs font-bold py-2.5 px-4 rounded-xl border border-blue-500/20 transition shadow-lg"><Settings className="w-3.5 h-3.5" /> Manager Console</button>}
                        <span className="text-xs font-mono bg-blue-950 text-blue-400 border border-blue-900 px-3 py-1.5 rounded-md uppercase tracking-wider">Bidder Portal // Active</span>
                        <button onClick={handleLogout} className="flex items-center gap-2 bg-gray-800 hover:bg-red-950/40 font-mono text-xs py-2 px-4 rounded-xl border border-gray-700/60 transition"><LogOut className="w-4 h-4" /> Exit Yard</button>
                    </div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-6 py-10">
                {/* 100% ओरिजिनल डैशबोर्ड कोड का बाकी हिस्सा यहाँ है... */}
                {/* (बाकी का कोड जो तुमने मुझे दिया था, वो सब यहीं रहेगा) */}
                <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-10 gap-4 relative">
                    <div>
                        <h1 className="text-3xl font-extrabold tracking-tight m-0">Live Auction Showroom</h1>
                        <p className="text-gray-500 text-sm mt-1">Place strategic bids on premium verified fleet assets running across global yards.</p>
                    </div>
                    
                    <div className="relative">
                        <button onClick={() => setShowFilterDropdown(!showFilterDropdown)} className="flex items-center gap-2 bg-gray-900 hover:bg-gray-800 border border-gray-800 text-white px-4 py-3 rounded-xl text-xs font-mono tracking-wider uppercase transition"><Sliders className="w-4 h-4" /> {selectedYard === 'ALL' ? 'Filter Lots' : `Yard: ${selectedYard}`}</button>

                        {showFilterDropdown && (
                            <div className="absolute right-0 mt-2 w-48 bg-gray-900 border border-gray-800 rounded-xl shadow-2xl z-50 overflow-hidden font-mono text-xs">
                                <button onClick={() => { setSelectedYard('ALL'); setShowFilterDropdown(false); }} className={`w-full text-left px-4 py-3 hover:bg-gray-800 transition ${selectedYard === 'ALL' ? 'text-blue-400 font-bold bg-blue-950/20' : 'text-gray-400'}`}>🌐 Show All Yards</button>
                                
                                {availableYards.map((yardName) => (
                                    <button key={yardName} onClick={() => { setSelectedYard(yardName); setShowFilterDropdown(false); }} className={`w-full text-left px-4 py-3 hover:bg-gray-800 transition ${selectedYard.toLowerCase() === yardName.toLowerCase() ? 'text-blue-400 font-bold bg-blue-950/20' : 'text-gray-400'}`}>📍 {yardName} Yard</button>
                                ))}
                            </div>
                        )}
                    </div>
                </div>

                {loading ? (
                    <div className="flex flex-col items-center justify-center min-h-[400px] font-mono text-gray-500 gap-3">
                        <div className="w-8 h-8 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                        <span className="text-xs uppercase tracking-widest">Synchronizing Showroom Ledger...</span>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
                        {filteredVehicles.map((vehicle) => (
                            <div key={vehicle.id} className="group bg-gray-900 rounded-2xl overflow-hidden border border-gray-800/80 shadow-xl flex flex-col">
                                <div className="relative aspect-[16/10] w-full overflow-hidden bg-gray-950">
                                    <VehicleImageGallery mainImage={vehicle.imageName} extraImages={vehicle.extraImages} altText={vehicle.model} />
                                    <div className="absolute top-3 right-3 bg-gray-950/90 border border-gray-800 text-[10px] px-2.5 py-1 rounded-md font-mono text-blue-400 z-10">{vehicle.regNo || "NO REG"}</div>
                                    
                                    <div className="absolute bottom-3 left-3 bg-gray-950/80 backdrop-blur-md border border-gray-800 px-2.5 py-1.5 rounded-lg flex items-center gap-1.5 text-[11px] font-mono text-amber-400 z-10">
                                        <Timer className="w-3.5 h-3.5 animate-pulse" /> 
                                        <LiveAuctionTimer endTime={vehicle.auctionEnd} />
                                    </div>
                                    
                                    {vehicle.videoUrl && <button onClick={() => openVideoPlayer(vehicle.videoUrl, vehicle.brand, vehicle.model)} className="absolute bottom-3 right-3 bg-red-600/90 hover:bg-red-500 text-white p-2 rounded-xl border border-red-500/20 text-[10px] font-mono font-bold z-10 cursor-pointer transform active:scale-95 transition"><Video className="w-3.5 h-3.5 animate-pulse" /> VIDEO</button>}
                                </div>

                                <div className="p-6 flex flex-col flex-grow text-left">
                                    <h3 className="text-lg font-bold text-white tracking-tight m-0">{vehicle.brand} {vehicle.model}</h3>
                                    <div className="flex items-center gap-1 text-gray-500 text-xs mt-1.5 font-medium"><MapPin className="w-3.5 h-3.5 text-gray-600" /> Yard: {vehicle.yardLocation || "Global"}</div>
                                    <div className="grid grid-cols-3 gap-2 my-5 text-gray-400 text-xs font-mono font-medium">
                                        <div className="bg-gray-950/60 p-2 rounded-lg flex flex-col items-center justify-center border border-gray-800/40"><Fuel className="w-3.5 h-3.5" /><span>{vehicle.fuelType}</span></div>
                                        <div className="bg-gray-950/60 p-2 rounded-lg flex flex-col items-center justify-center border border-gray-800/40"><Gauge className="w-3.5 h-3.5" /><span>{vehicle.kmDriven} km</span></div>
                                        <div className="bg-gray-950/60 p-2 rounded-lg flex flex-col items-center justify-center border border-gray-800/40"><Car className="w-3.5 h-3.5" /><span>{vehicle.mfgYear}</span></div>
                                    </div>
                                    
                                    <LiveHighestBid 
                                        vehicleId={vehicle.id} 
                                        refreshTrigger={refreshBids} 
                                        onHighestBidFetch={(maxBid) => {
                                            if (selectedVehicle && selectedVehicle.id === vehicle.id) {
                                                setCurrentVehicleHighestBid(maxBid);
                                            }
                                        }}
                                    />
                                    
                                    <div className="mt-5 pt-4 border-t border-gray-800/60 flex flex-col gap-3">
                                        <div className="flex items-center justify-between">
                                            <div>
                                                <p className="text-[10px] uppercase font-mono text-gray-500 m-0">Base Value</p>
                                                <p className="text-xl font-black text-white font-mono mt-0.5 m-0">₹{vehicle.basePrice?.toLocaleString('en-IN')}</p>
                                            </div>
                                            <button onClick={() => {
                                                setSelectedVehicle(vehicle);
                                                openBidModal(vehicle);
                                            }} className="bg-blue-600 hover:bg-blue-500 text-white text-xs font-bold font-mono py-3 px-4 rounded-xl flex items-center gap-1.5 uppercase tracking-wider transition transform active:scale-95"><Gavel className="w-3.5 h-3.5" /> Place Bid</button>
                                        </div>

                                        <button 
                                            onClick={() => initSecureCheckoutPayment(vehicle)}
                                            disabled={paymentProcessing}
                                            className="w-full bg-emerald-600 hover:bg-emerald-500 disabled:opacity-50 text-white font-mono text-xs font-bold py-2.5 rounded-xl border border-emerald-500/20 transition flex items-center justify-center gap-1.5 uppercase tracking-wider shadow-lg shadow-emerald-600/10 transform active:scale-95"
                                        >
                                            <CreditCard className="w-4 h-4" /> 
                                            {paymentProcessing ? "Opening Secure Gate..." : "Buy Lot via Razorpay 💳"}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>

            {/* बिडिंग मोडल और वीडियो प्लेयर यहाँ जुड़ेंगे... */}
            {isModalOpen && selectedVehicle && (
                <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-[100]">
                    <div className="bg-gray-900 border border-gray-800 w-full max-w-md rounded-2xl shadow-2xl p-6 text-left relative">
                        <div className="flex justify-between items-center border-b border-gray-800 pb-4 mb-5">
                            <div className="flex items-center gap-2 text-blue-400 font-mono text-xs font-bold uppercase tracking-wider"><Gavel className="w-4 h-4 text-blue-500" /> Live Auction Submission</div>
                            <button onClick={() => setIsModalOpen(false)} className="p-1 bg-gray-950 hover:bg-gray-800 border border-gray-800 text-gray-400 hover:text-white rounded-lg transition cursor-pointer"><X className="w-4 h-4" /></button>
                        </div>
                        {bidError && <div className="bg-red-950/40 border border-red-900/50 text-red-400 p-3 rounded-xl text-xs mb-4 font-mono">⚡ {bidError}</div>}
                        {bidSuccess && <div className="bg-emerald-950/40 border border-emerald-900/50 text-emerald-400 p-3 rounded-xl text-xs mb-4 font-mono">✓ {bidSuccess}</div>}
                        <form onSubmit={handlePlaceBidSubmit} className="space-y-4">
                            <div>
                                <label className="block text-gray-400 text-[10px] font-mono mb-2 uppercase tracking-wider">Your Bidding Value (INR)</label>
                                <input type="number" value={bidAmount} onChange={(e) => setBidAmount(e.target.value)} placeholder={currentVehicleHighestBid > 0 ? `Must be > ₹${currentVehicleHighestBid}` : "Enter bid amount"} className="w-full px-4 py-3 bg-gray-950 border border-gray-800 rounded-xl text-white font-mono focus:outline-none focus:border-blue-500 text-sm" required />
                            </div>
                            <button type="submit" className="w-full bg-blue-600 hover:bg-blue-500 text-white font-mono text-xs font-bold py-3.5 rounded-xl uppercase tracking-widest transition" disabled={submittingBid || bidSuccess}>
                                {submittingBid ? "Registering Bid..." : "Confirm Live Lot Bid ⚡"}
                            </button>
                        </form>
                    </div>
                </div>
            )}
            
            {isVideoModalOpen && activeVideoUrl && (
                <div className="fixed inset-0 bg-black/90 backdrop-blur-md flex items-center justify-center p-4 z-[200]">
                    <div className="bg-gray-900 border border-gray-800 w-full max-w-3xl rounded-3xl shadow-2xl overflow-hidden text-left relative">
                        <div className="flex justify-between items-center bg-gray-900 px-6 py-4 border-b border-gray-800">
                            <div className="flex items-center gap-2 text-red-400 font-mono text-xs font-bold uppercase tracking-wider">
                                <Video className="w-4 h-4 text-red-500" /> 360° Mechanical Inspection // {activeVehicleName}
                            </div>
                            <button onClick={() => { setIsVideoModalOpen(false); setActiveVideoUrl(''); }} className="p-1.5 bg-gray-950 hover:bg-gray-800 border border-gray-800 text-gray-400 hover:text-white rounded-xl transition cursor-pointer"><X className="w-4 h-4" /></button>
                        </div>
                        <div className="aspect-video w-full bg-black flex items-center justify-center">
                            {activeVideoUrl.includes('.mp4') || activeVideoUrl.startsWith('/') || activeVideoUrl.includes('/api/vehicles/video/') ? (
                                <video src={activeVideoUrl} controls autoPlay className="w-full h-full object-contain" />
                            ) : (
                                <iframe src={activeVideoUrl} title="Vehicle Video Player" className="w-full h-full border-0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen></iframe>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};
export default Dashboard;