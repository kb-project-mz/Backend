package fingertips.backend.member.login.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import fingertips.backend.exception.dto.JsonResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/member/login/google")
public class GoogleController {

    @Value("${google.client.id}")
    private String googleClientId;

    @GetMapping("/google-client-id")
    public ResponseEntity<JsonResponse<String>> getGoogleClientId() {
        return ResponseEntity.ok(JsonResponse.success(googleClientId));
    }

    @PostMapping("/")
    public ResponseEntity<JsonResponse<?>> googleLogin(@RequestBody Map<String, String> request) {
        String idTokenString = request.get("token");

        // 구글 토큰 검증 객체 생성
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userService.findByEmail(email);
                if (user == null) {
                    user = new User();
                    user.setEmail(email);
                    user.setName(name);
                    userService.save(user);

                    return ResponseEntity.ok(JsonResponse.success(Map.of("message", "Registration success")));
                }

                String jwtToken = jwtProvider.createToken(user.getEmail());
                return ResponseEntity.ok(JsonResponse.success(Map.of("message", "Login success", "token", jwtToken)));

            } else {
                return ResponseEntity.status(401).body(JsonResponse.error("Invalid ID token"));
            }
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(500).body(JsonResponse.error("Token verification failed"));
        }
    }
}
