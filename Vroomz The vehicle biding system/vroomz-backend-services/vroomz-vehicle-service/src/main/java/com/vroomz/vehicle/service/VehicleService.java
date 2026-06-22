package com.vroomz.vehicle.service;

import com.vroomz.vehicle.model.Vehicle;
import com.vroomz.vehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    private final String UPLOAD_DIR = "C:/vroomz/images/";

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }

    // पुराना बैकअप मेथड
    public Vehicle saveVehicleWithImage(Vehicle vehicle, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String uniqueFileName = uploadSingleFile(imageFile);
            vehicle.setImageName(uniqueFileName);
        }
        return vehicleRepository.save(vehicle);
    }

    /**
     * 🚀 सिंगल फ़ाइल अपलोड करने का कोर मेथड
     */
    public String uploadSingleFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            originalFileName = "asset_image.jpg";
        }
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName.replaceAll("\\s+", "_");

        File destinationFile = new File(directory, uniqueFileName);
        file.transferTo(destinationFile);

        return uniqueFileName;
    }

    /**
     * 🚀 महा-फिक्स: यह मेथड अब NEW VEHICLE ADD करते समय वीडियो यूआरएल और एक्स-रे इमेजेस दोनों को डेटाबेस में परमानेंट लॉक करेगा!
     */
    public Vehicle saveVehicleWithAllImages(Vehicle vehicle, MultipartFile mainImage, List<MultipartFile> extraImages) throws IOException {
        // 1. मेन कवर फोटो प्रोसेस करो भाई
        if (mainImage != null && !mainImage.isEmpty()) {
            String uniqueMainName = uploadSingleFile(mainImage);
            if (uniqueMainName != null) {
                vehicle.setImageName(uniqueMainName);
                vehicle.setImageUrl("/api/vehicles/image/" + uniqueMainName);
            }
        }

        // 2. वीडियो यूआरएल को पक्का करो कि वो ऑब्जेक्ट में मौजूद रहे
        if (vehicle.getVideoUrl() != null) {
            vehicle.setVideoUrl(vehicle.getVideoUrl().trim());
        }

        // 3. एक्स्ट्रा एक्स-रे फ़ोटोज़ को लिस्ट में जोड़ो भाई
        if (extraImages != null && !extraImages.isEmpty()) {
            List<String> extraImagePaths = new ArrayList<>();
            for (MultipartFile file : extraImages) {
                if (file != null && !file.isEmpty() && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
                    String uniqueExtraName = uploadSingleFile(file);
                    if (uniqueExtraName != null) {
                        extraImagePaths.add("/api/vehicles/image/" + uniqueExtraName);
                    }
                }
            }
            if (!extraImagePaths.isEmpty()) {
                vehicle.setExtraImages(extraImagePaths);
            }
        }

        return vehicleRepository.save(vehicle);
    }
}