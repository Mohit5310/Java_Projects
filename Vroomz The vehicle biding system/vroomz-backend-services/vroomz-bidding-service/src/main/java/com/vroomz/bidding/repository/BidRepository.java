package com.vroomz.bidding.repository;

import com.vroomz.bidding.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    
    // यह लाइन जोड़ो भाई, ताकि स्प्रिंग डेटा जेपीए अपने आप क्वेरी बना सके
    List<Bid> findByCarId(Long carId);

    // आपका पुराना मेथड जो पहले से होगा
    Optional<Bid> findTopByCarIdOrderByBidAmountDesc(Long carId);

    // 🚀 महा-फिक्स: कार आईडी के बेस पर सारे बिड्स को साफ करने का कड़क डेटाबेस मैकेनिज्म भाई
    @Transactional
    @Modifying
    @Query("DELETE FROM Bid b WHERE b.carId = :carId")
    void deleteByCarId(Long carId);
}