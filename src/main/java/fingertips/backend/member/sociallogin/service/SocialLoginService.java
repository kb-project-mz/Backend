package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.dto.TokenDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SocialLoginService {
    String getGoogleClientId();
    TokenDTO googleLogin(Map<String, String> request);
    TokenDTO googleCallback(String code);
    boolean googleMemberExists(String email);
    void googleMemberJoin(SocialLoginDTO socialLoginDTO);
    TokenDTO googleLoginWithTokens(SocialLoginDTO socialLoginDTO);
}
