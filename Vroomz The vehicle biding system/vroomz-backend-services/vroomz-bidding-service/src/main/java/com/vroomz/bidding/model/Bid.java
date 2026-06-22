package com.vroomz.bidding.model; // आपका जो भी सही पैकेज नेम हो

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Data // यह अपने आप getCarId() और getBidAmount() बना देता है
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carId; // स्पेलिंग ध्यान से देखें: c-a-r-I-d
    private String userId;
    private Double bidAmount;

    @Column(updatable = false)
    private LocalDateTime placedAt;

    @PrePersist
    protected void onCreate() {
        this.placedAt = LocalDateTime.now();
    }

    // अगर लोम्बोक काम नहीं कर रहा है, तो हाथ से गेटर-सेटर डाल देते हैं (सेफ्टी के लिए)
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(Double bidAmount) {
        this.bidAmount = bidAmount;
    }
}