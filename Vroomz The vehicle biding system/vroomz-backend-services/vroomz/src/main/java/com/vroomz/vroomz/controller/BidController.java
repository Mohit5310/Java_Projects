package com.vroomz.vroomz.controller;

import com.vroomz.vroomz.model.Bid;
import com.vroomz.vroomz.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping
    public Bid placeBid(@RequestBody Bid bid) {
        return bidService.placeBid(bid);
    }

    @GetMapping
    public List<Bid> getAllBids() {
        return bidService.getAllBids();
    }

    @GetMapping("/car/{carId}")
    public List<Bid> getBidsByCar(@PathVariable Long carId) {
        return bidService.getBidsByCarId(carId);
    }
}
