package com.vroomz.vehicle.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(name = "reg_no") 
    private String regNo;
    
    private String brand;
    private String model;
    
    @Column(name = "mfg_year")
    private int mfgYear;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column(name = "km_driven")
    private String kmDriven;
    
    @Column(name = "yard_location")
    private String yardLocation;   
    
    @Column(name = "rc_status")
    private String rcStatus;       
    
    @Column(name = "base_price")
    private Double basePrice;      
    
    @Column(name = "image_url")
    private String imageUrl;       
    
    @Column(name = "video_url")
    private String videoUrl;       
    
    @Column(name = "image_name")
    private String imageName; 

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vehicle_images", joinColumns = @JoinColumn(name = "vehicle_id"))
    @Column(name = "image_name")
    private List<String> extraImages = new ArrayList<>();

    // यह फ़ील्ड ऑक्शन के लिए है
    @Column(name = "auction_end")
    private LocalDateTime auctionEnd;
    
    private String status;        

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if(this.status == null) this.status = "AVAILABLE";
    }
}