package com.vroomz.vroomz.controller;

import com.vroomz.vroomz.model.Vehicle;
import com.vroomz.vroomz.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/add")
    public Vehicle addVehicle(
            @RequestPart("vehicle") Vehicle vehicle,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart(value = "video", required = false) MultipartFile video) {
        try {
            return vehicleService.saveVehicle(vehicle, image, video);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving vehicle: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Vehicle> getAll() {
        return vehicleService.getAllVehicles();
    }
}