package com.vroomz.vroomz.repository;

import com.vroomz.vroomz.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByYardLocationIgnoreCase(String yardLocation);
    List<Vehicle> findByStatus(String status);
}