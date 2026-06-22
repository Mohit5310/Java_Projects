package com.vroomz.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RootController {

    @GetMapping("/")
    public Mono<ResponseEntity<String>> root() {
        return Mono.just(ResponseEntity.ok("Vroomz Gateway is running. Use /actuator/info for health and metadata."));
    }
}
