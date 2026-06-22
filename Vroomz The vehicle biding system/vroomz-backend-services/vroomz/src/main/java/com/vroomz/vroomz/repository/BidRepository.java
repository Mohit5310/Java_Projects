package com.vroomz.vroomz.repository;

import com.vroomz.vroomz.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByCarId(Long carId);
}
