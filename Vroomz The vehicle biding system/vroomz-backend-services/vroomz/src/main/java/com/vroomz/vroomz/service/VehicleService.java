package com.vroomz.vroomz.service;

import com.vroomz.vroomz.model.Vehicle;
import com.vroomz.vroomz.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    // Windows ke liye path sahi format mein
    private final String UPLOAD_DIR = "C:/vroomz/uploads/";

    public Vehicle saveVehicle(Vehicle vehicle, MultipartFile image, MultipartFile video) throws IOException {
        
        // 1. Folder check aur create karna
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. Image handle karna
        if (image != null && !image.isEmpty()) {
            String imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path imagePath = Paths.get(UPLOAD_DIR).resolve(imageName);
            Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            vehicle.setImageUrl(imageName);
        }

        // 3. Video handle karna
        if (video != null && !video.isEmpty()) {
            String videoName = UUID.randomUUID() + "_" + video.getOriginalFilename();
            Path videoPath = Paths.get(UPLOAD_DIR).resolve(videoName);
            Files.copy(video.getInputStream(), videoPath, StandardCopyOption.REPLACE_EXISTING);
            vehicle.setVideoUrl(videoName);
        }

        // 4. Database mein save karna
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }
}