package ar.com.aeb.alquileres.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PingController {

    /**
     * Health check endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Alquileres API is running");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.11");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simple ping endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
