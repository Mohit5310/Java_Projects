package com.vroomz.user.controller;

import com.vroomz.user.model.User;
import com.vroomz.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173") // CORS इशू से बचने के लिए
public class UserController {

    @Autowired 
    private UserRepository userRepository;

    @GetMapping("/me")
    public User getMyProfile(Principal principal) {
        // यह 'principal.getName()' JWT टोकन से ईमेल निकाल लेगा
        String email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}