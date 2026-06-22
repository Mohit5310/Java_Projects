package com.vroomz.vroomz.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long carId;
    private String userId;
    private Double bidAmount;

    @Column(updatable = false)
    private LocalDateTime placedAt;

    @PrePersist
    protected void onCreate() {
        this.placedAt = LocalDateTime.now();
    }
}
