package fingertips.backend.member.sociallogin.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.service.SocialLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/member/login/google")
@RequiredArgsConstructor
public class GoogleController {

    private final SocialLoginService socialLoginService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // Google 로그인 페이지로 리디렉션하는 URL을 반환
    @GetMapping("/oauth2")
    public ResponseEntity<JsonResponse<String>> loginUrlGoogle() {
        String reqUrl = "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + googleClientId
                + "&redirect_uri=http://localhost:8080/api/v1/member/login/google/callback"
                + "&response_type=code&scope=email%20profile%20openid&access_type=offline";
        return ResponseEntity.ok(JsonResponse.success(reqUrl));
    }

    @GetMapping("/google-client-id")
    public ResponseEntity<JsonResponse<String>> getGoogleClientId() {
        return ResponseEntity.ok(JsonResponse.success(googleClientId)); // 수정: 직접 사용
    }

    @PostMapping("/")
    public ResponseEntity<JsonResponse<?>> googleLogin(@RequestBody Map<String, String> request) {
        return socialLoginService.googleLogin(request);
    }

    // Google 로그인 응답 처리
    @GetMapping("/callback")
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(@RequestParam String code) {
        return socialLoginService.googleCallback(code);
    }
}
