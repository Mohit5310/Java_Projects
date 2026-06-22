package com.vroomz.vroomz.service;

import com.vroomz.vroomz.model.Bid;
import com.vroomz.vroomz.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public Bid placeBid(Bid bid) {
        return bidRepository.save(bid);
    }

    public List<Bid> getAllBids() {
        return bidRepository.findAll();
    }

    public List<Bid> getBidsByCarId(Long carId) {
        return bidRepository.findByCarId(carId);
    }
}