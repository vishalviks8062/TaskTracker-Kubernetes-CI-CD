package com.tasktracker.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Plain, dependency-free 200 OK — this is what the Kubernetes liveness
// and readiness probes on the backend Deployment hit (see
// k8s/06-backend-deployment.yaml). It deliberately does NOT check the
// database, so a DB outage doesn't get the backend Pods killed too.
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
