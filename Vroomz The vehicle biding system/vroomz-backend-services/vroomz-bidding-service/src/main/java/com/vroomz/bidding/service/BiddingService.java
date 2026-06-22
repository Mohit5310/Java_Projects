package com.vroomz.bidding.service;

import com.vroomz.bidding.client.VehicleClient;
import com.vroomz.bidding.model.Bid;
import com.vroomz.bidding.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BiddingService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private VehicleClient vehicleClient;

   
    public String placeBid(Bid newBid) {
        try {
            Map<String, Object> vehicle = vehicleClient.getVehicleById(newBid.getCarId());
            if (vehicle == null || !vehicle.containsKey("basePrice")) {
                return "Error: VEHICLE-SERVICE से गाड़ी का डेटा या बेस प्राइस नहीं मिल पाया!";
            }
            Double basePrice = Double.valueOf(vehicle.get("basePrice").toString());
            if (newBid.getBidAmount() < basePrice) {
                return "Error: Aapki boli gaadi ke base price (₹" + basePrice + ") se kam hai! Kam se kam base price jitni boli lagayein.";
            }
            Optional<Bid> highestBid = bidRepository.findTopByCarIdOrderByBidAmountDesc(newBid.getCarId());
            if (highestBid.isPresent() && newBid.getBidAmount() <= highestBid.get().getBidAmount()) {
                return "Error: Aapki boli sabse unchi boli (₹" + highestBid.get().getBidAmount() + ") se zyada honi chahiye!";
            }
            bidRepository.save(newBid);
            return "Success: Aapki boli ₹" + newBid.getBidAmount() + " successfully lag gayi hai!";
        } catch (Exception e) {
            return "Error: Vehicle Service से कनेक्शन फेल हो गया! कृपया थोड़ी देर बाद प्रयास करें।";
        }
    }

    // 2. 🔥 यह नया मेथड जोड़ा भाई: किसी गाड़ी की सभी बोलियाँ निकालने के लिए
    public List<Bid> getBidsByCarId(Long carId) {
        // आपकी रिपॉजिटरी में जो भी मेथड नाम हो (जैसे findByCarId या findByVehicleId) 
        // चूँकि आपकी सर्विस में findTopByCarId है, तो रिपॉजिटरी में findByCarId होना चाहिए।
        return bidRepository.findByCarId(carId); 
    }

    // 🚀 3. महा-फिक्स: कंट्रोलर की रीसेट रिक्वेस्ट को रिपॉजिटरी के डिलीट क्वैरी तक पहुँचाने वाला सर्विस मेथड भाई
    public void deleteBidsByCarId(Long carId) {
        bidRepository.deleteByCarId(carId);
    }
}