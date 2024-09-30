package fingertips.backend.member.sociallogin.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.dto.TokenDTO;
import fingertips.backend.member.sociallogin.service.SocialLoginService;
import fingertips.backend.member.sociallogin.service.SocialLoginServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/member/login/google")
@RequiredArgsConstructor
public class GoogleController {

    private static final Logger logger = LoggerFactory.getLogger(GoogleController.class);
    private final SocialLoginService socialLoginService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @GetMapping("")
    public ResponseEntity<JsonResponse<String>> googleLoginRedirect() {
        String authUrl = UriComponentsBuilder.fromHttpUrl("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("response_type", "code")
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", "http://localhost:8080/api/v1/member/login/google/callback")
                .queryParam("scope", "email profile")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .toUriString();
        System.out.println(URLEncoder.encode(authUrl, StandardCharsets.UTF_8));
        return ResponseEntity.ok(JsonResponse.success(authUrl));
    }

    @GetMapping("/callback")
    public RedirectView googleCallback(@RequestParam("code") String code) {
        TokenDTO token = socialLoginService.googleCallback(code);

        return new RedirectView("http://localhost:5173/google-callback?access_token=" + token.getAccessToken()
                + "&refresh_token=" + token.getRefreshToken()
                + "&member_id=" + token.getMemberId()
                + "&member_name=" + URLEncoder.encode(token.getMemberName(), Charset.defaultCharset()));
    }

    @PostMapping("/tokens")
    public ResponseEntity<JsonResponse<TokenDTO>> loginWithGoogleTokens(@RequestBody Map<String, String> request) {
        SocialLoginDTO socialLoginDTO = SocialLoginDTO.builder()
                .email(request.get("email"))
                .googleId(request.get("google_id"))
                .memberName(request.get("member_name"))
                .googleAccessToken(request.get("access_token"))
                .googleIdToken(request.get("id_token"))
                .googleRefreshToken(request.get("refresh_token"))
                .expiresIn(Integer.parseInt(request.get("expires_in")))
                .build();
        return ResponseEntity.ok(JsonResponse.success(socialLoginService.googleLoginWithTokens(socialLoginDTO)));
    }

    @GetMapping("/google-client-id")
    public ResponseEntity<JsonResponse<String>> getGoogleClientId() {
        return ResponseEntity.ok(JsonResponse.success(googleClientId));
    }
}