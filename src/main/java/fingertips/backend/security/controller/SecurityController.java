package fingertips.backend.security.controller;

import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.util.JwtProcessor;
import fingertips.backend.security.service.UserService;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Log4j
@RequestMapping("/api/security")
@RestController
public class SecurityController {

    private final UserService userService;
    private final JwtProcessor jwtProcessor;

    public SecurityController(UserService userService, JwtProcessor jwtProcessor) {
        this.userService = userService;
        this.jwtProcessor = jwtProcessor;
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtProcessor.validateToken(refreshToken)) {
            String username = jwtProcessor.getUsername(refreshToken);
            List<String> roles = jwtProcessor.getUserRoles(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(username, roles);
            String newRefreshToken = jwtProcessor.generateRefreshToken(username); // 새로운 리프레시 토큰 생성
            return ResponseEntity.ok(
                    AuthDTO.builder()
                            .userId(username)
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }


    @GetMapping("/all")
    public ResponseEntity<String> doAll() {
        log.info("do all can access everybody");
        return ResponseEntity.ok("All can access everybody");
    }


    @GetMapping("/admin")
    public ResponseEntity<String> doAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            log.info("Admin access granted to " + userDetails.getUsername());
            return ResponseEntity.ok("Admin resource accessed");
        } else {
            log.info("Access denied for " + userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    @GetMapping("/member")
    public ResponseEntity<String> doMember(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        log.info("username = " + userDetails.getUsername());
        return ResponseEntity.ok(userDetails.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.authenticate(loginDTO.getUsername(), loginDTO.getPassword());
            String refreshToken = jwtProcessor.generateRefreshToken(loginDTO.getUsername());
            return ResponseEntity.ok(
                    AuthDTO.builder()
                            .userId(loginDTO.getUsername())
                            .accessToken(token)
                            .refreshToken(refreshToken)
                            .build()
            );
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }


    @GetMapping("/logout")
    public void logout() {
        log.info("logout page");
    }
}
