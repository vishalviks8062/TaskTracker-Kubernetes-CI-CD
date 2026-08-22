package com.tasktracker.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Dependency-free 200 OK — this is what the Kubernetes liveness and
// readiness probes on the backend Deployment hit (see
// k8s/06-backend-deployment.yaml). It deliberately does NOT check the
// database, so a DB outage doesn't get the backend Pods killed too. K8s
// probes only care about the HTTP status code, not the body, so changing
// the body here (as we're doing to validate the CI/CD pipeline end to
// end) can't break a rollout.
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        return ResponseEntity.ok(new HealthStatus("UP", "1.1.0"));
    }

    private record HealthStatus(String status, String version) {
    }
}
