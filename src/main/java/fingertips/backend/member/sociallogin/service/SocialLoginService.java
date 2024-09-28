package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.dto.TokenDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SocialLoginService {
    String getGoogleClientId();
    TokenDto googleLogin(Map<String, String> request);
    TokenDto googleCallback(String code);
    boolean googleMemberExists(String email);
    void googleMemberJoin(SocialLoginDTO socialLoginDTO);
    TokenDto googleLoginWithTokens(SocialLoginDTO socialLoginDTO);
}
