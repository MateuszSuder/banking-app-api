package banking.application.controller;

import banking.application.Application;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@SpringBootApplication
@RequestMapping("/")
public class HealthController extends Application {
    /**
     * Return current time to show if service is alive.
     * @return Current time.
     */
    @GetMapping("health")
    public ResponseEntity<LocalDateTime> getHealth() {
        LocalDateTime localDate = LocalDateTime.now();
        return ResponseEntity.ok(localDate);
    }
}