package com.vroomz.vroomz.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String regNo;
    private String brand;
    private String model;
    private int mfgYear;
    private String fuelType;
    private String kmDriven;
    
    private String yardLocation;   // E.g., Muzaffarpur
    private String rcStatus;       // Original/NOC
    private Double basePrice;      // Auction starting price
    
    private String imageUrl;       // Path of saved image
    private String videoUrl;       // Path of saved video
    
    private LocalDateTime auctionEnd;
    private String status;         // AVAILABLE, SOLD

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if(this.status == null) this.status = "AVAILABLE";
    }
 // Vehicle.java ke andar add karein
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
