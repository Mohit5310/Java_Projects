package com.vroomz.vehicle.controller;

import com.vroomz.vehicle.model.Vehicle;
import com.vroomz.vehicle.repository.VehicleRepository;
import com.vroomz.vehicle.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> addVehicle(
            @RequestPart("vehicle") Vehicle vehicle,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "extraImages", required = false) List<MultipartFile> extraImages) {
        try {
            MultipartFile cleanMainImage = (imageFile != null && imageFile.getSize() > 0) ? imageFile : null;
            
            List<MultipartFile> cleanExtraImages = new ArrayList<>();
            if (extraImages != null) {
                for (MultipartFile f : extraImages) {
                    if (f != null && f.getSize() > 0 && f.getOriginalFilename() != null && !f.getOriginalFilename().isEmpty()) {
                        cleanExtraImages.add(f);
                    }
                }
            }

            Vehicle savedVehicle = vehicleService.saveVehicleWithAllImages(vehicle, cleanMainImage, cleanExtraImages.isEmpty() ? null : cleanExtraImages);
            return ResponseEntity.ok(savedVehicle);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error: Photo upload execution failed. " + e.getMessage());
        }
    }

    @PutMapping(value = "/update/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateVehicle(
            @PathVariable Long id,
            @RequestPart("vehicle") Vehicle updatedVehicle,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "extraImages", required = false) List<MultipartFile> extraImages) {
        try {
            Optional<Vehicle> existingVehicleOpt = vehicleRepository.findById(id);
            if (existingVehicleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle asset not found inside the registry schema.");
            }

            Vehicle existingVehicle = existingVehicleOpt.get();

            // Basic Data fields
            existingVehicle.setBrand(updatedVehicle.getBrand());
            existingVehicle.setModel(updatedVehicle.getModel());
            existingVehicle.setBasePrice(updatedVehicle.getBasePrice());
            existingVehicle.setKmDriven(updatedVehicle.getKmDriven());
            existingVehicle.setMfgYear(updatedVehicle.getMfgYear());
            existingVehicle.setFuelType(updatedVehicle.getFuelType());
            existingVehicle.setRegNo(updatedVehicle.getRegNo());
            existingVehicle.setRcStatus(updatedVehicle.getRcStatus());
            existingVehicle.setYardLocation(updatedVehicle.getYardLocation());
            existingVehicle.setStatus(updatedVehicle.getStatus());
            
            // 🚀 फिक्स: यहाँ auctionEnd को अपडेट कर रहे हैं
            existingVehicle.setAuctionEnd(updatedVehicle.getAuctionEnd()); 
            
            existingVehicle.setVideoUrl(updatedVehicle.getVideoUrl() != null ? updatedVehicle.getVideoUrl().trim() : null);

            // Cover Image update
            if (imageFile != null && imageFile.getSize() > 0 && imageFile.getOriginalFilename() != null && !imageFile.getOriginalFilename().isEmpty()) {
                String uniqueMainName = vehicleService.uploadSingleFile(imageFile);
                if (uniqueMainName != null) {
                    existingVehicle.setImageName(uniqueMainName);
                    existingVehicle.setImageUrl("/api/vehicles/image/" + uniqueMainName);
                }
            } 

            // Extra Images update
            if (extraImages != null && !extraImages.isEmpty()) {
                List<String> newExtraImagesList = new ArrayList<>();
                for (MultipartFile file : extraImages) {
                    if (file != null && file.getSize() > 0 && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
                        String extraImageName = vehicleService.uploadSingleFile(file);
                        if (extraImageName != null) {
                            newExtraImagesList.add("/api/vehicles/image/" + extraImageName);
                        }
                    }
                }
                if (!newExtraImagesList.isEmpty()) {
                    existingVehicle.getExtraImages().clear();
                    existingVehicle.getExtraImages().addAll(newExtraImagesList);
                }
            }

            Vehicle savedVehicle = vehicleRepository.save(existingVehicle);
            return ResponseEntity.ok(savedVehicle);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error executing asset update operation: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/gallery")
    public ResponseEntity<?> deleteGalleryImage(@PathVariable Long id, @RequestParam("imageUrl") String imageUrl) {
        try {
            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(id);
            if (vehicleOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle asset not found.");
            
            Vehicle vehicle = vehicleOpt.get();
            boolean removed = vehicle.getExtraImages().remove(imageUrl);
            
            if (removed) {
                Vehicle savedVehicle = vehicleRepository.save(vehicle);
                return ResponseEntity.ok().body(savedVehicle);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Image reference not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleService.getVehicleById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getVehicleImage(@PathVariable String fileName) {
        try {
            Path path = Paths.get("C:/vroomz/images/" + fileName);
            byte[] image = Files.readAllBytes(path);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            if (vehicleRepository.existsById(id)) {
                vehicleRepository.deleteById(id);
                return ResponseEntity.ok().body("Vehicle purged successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vehicle not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}