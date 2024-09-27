package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SocialLoginService {
    String getGoogleClientId();
    ResponseEntity<JsonResponse<SocialLoginDTO>> googleLogin(Map<String, String> request);
    ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code);
    boolean googleMemberExists(String email);
    void googleMemberJoin(SocialLoginDTO socialLoginDTO);
    ResponseEntity<JsonResponse<SocialLoginDTO>> googleLoginWithTokens(SocialLoginDTO socialLoginDTO);
}
