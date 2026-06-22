package com.vroomz.bidding.controller;

import com.vroomz.bidding.model.Bid;
import com.vroomz.bidding.service.BiddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BiddingController {

    @Autowired
    private BiddingService biddingService;

    // 1. नई बोली लगाने का API (POST)
    @PostMapping("/place")
    public String placeBid(@RequestBody Bid bid) {
        return biddingService.placeBid(bid);
    }

    // 2. यह नया एंडपॉइंट चालू हो गया भाई (GET)
    @GetMapping("/vehicle/{carId}")
    public ResponseEntity<List<Bid>> getBidsByCar(@PathVariable Long carId) {
        List<Bid> bids = biddingService.getBidsByCarId(carId);
        return ResponseEntity.ok(bids);
    }

    // 🚀 3. महा-अपग्रेड: किसी गाड़ी की सारी बिड हिस्ट्री साफ़ करने का असली डिलीट API (DELETE)
    @DeleteMapping("/vehicle/{carId}")
    public ResponseEntity<String> resetBidsByCar(@PathVariable Long carId) {
        // तुम्हारे सर्विस लेयर के डिलीट मेथड को कॉल करेगा भाई
        biddingService.deleteBidsByCarId(carId); 
        return ResponseEntity.ok("Bidding history successfully purged from ledger.");
    }
}