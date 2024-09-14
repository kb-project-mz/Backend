package fingertips.backend.security.controller;


import fingertips.backend.security.util.JwtProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProcessor jwtProcessor;

    public AuthController(JwtProcessor jwtProcessor) {
        this.jwtProcessor = jwtProcessor;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtProcessor.validateToken(refreshToken)) {
            String username = jwtProcessor.getUsername(refreshToken);
            List<String> roles = jwtProcessor.getUserRoles(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(username, roles);
            return ResponseEntity.ok(Collections.singletonMap("accessToken", newAccessToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }
}

