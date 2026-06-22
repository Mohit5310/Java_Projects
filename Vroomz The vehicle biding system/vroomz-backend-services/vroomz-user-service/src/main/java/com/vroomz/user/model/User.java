package com.vroomz.user.model;



import jakarta.persistence.*;

import lombok.Data;



@Entity

@Table(name = "users")

@Data

public class User {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;



    @Column(unique = true, nullable = false)

    private String email;

   

    private String password;



    @Column(name = "full_name") // डेटाबेस के कॉलम से मैप किया

    private String fullName;



    private String mobile;



    private String role;



    @Column(name = "pan_card")   // डेटाबेस के कॉलम से मैप किया

    private String panCard;



    @Column(name = "aadhar_no")  // डेटाबेस के कॉलम से मैप किया

    private String aadharNo;



    @Column(name = "is_verified") // डेटाबेस के कॉलम से मैप किया

    private boolean isVerified = false;



    @Column(name = "wallet_balance") // डेटाबेस के कॉलम से मैप किया

    private Double walletBalance = 0.0;



    // Getter-Setter लिखने की ज़रूरत नहीं है क्योंकि ऊपर @Data लगा हुआ है,

    // लेकिन अगर पहले से लिखे हैं तो उन्हें ऐसे ही रहने दे सकते हैं।

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

}