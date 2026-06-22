package com.vroomz.bidding.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "VEHICLE-SERVICE") // Eureka में जो आपकी गाड़ी वाली सर्विस का नाम है
public interface VehicleClient {

    // यह मेथड सीधे व्हीकल सर्विस के कंट्रोलर को हिट करेगा
    @GetMapping("/api/vehicles/{id}")
    Map<String, Object> getVehicleById(@PathVariable("id") Long id);
}