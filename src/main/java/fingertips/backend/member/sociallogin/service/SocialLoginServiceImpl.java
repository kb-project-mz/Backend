package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.mapper.SocialLoginMapper;
import fingertips.backend.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    private final RestTemplate restTemplate;
    private final SocialLoginMapper socialLoginMapper;
    private final JwtProcessor jwtProcessor;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    private final JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation("https://accounts.google.com");

    @Override
    public String getGoogleClientId() {
        return googleClientId;
    }

    @Override
    public ResponseEntity<JsonResponse<?>> googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
        String googleAccessToken = request.get("access_token");
        String googleRefreshToken = request.get("refresh_token");
        String expiresIn = request.get("expires_in");

        if (googleIdToken == null || googleIdToken.isEmpty()) {
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);

        if (memberInfo == null) {
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        int memberCount = socialLoginMapper.checkMemberExists(memberInfo.getEmail());

        if (memberCount == 0) {
            memberInfo.setGoogleAccessToken(googleAccessToken);
            memberInfo.setGoogleIdToken(googleIdToken);
            memberInfo.setGoogleRefreshToken(googleRefreshToken);
            memberInfo.setExpiresIn(expiresIn);
            socialLoginMapper.insertMember(memberInfo);
            return ResponseEntity.ok(JsonResponse.success("Registration success"));
        } else {
            socialLoginMapper.updateMemberTokens(
                    memberInfo.getEmail(),
                    googleAccessToken,
                    googleIdToken,
                    googleRefreshToken,
                    expiresIn
            );
            return ResponseEntity.ok(JsonResponse.success("Login success"));
        }
    }


    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code) {

        String googleAccessToken = getGoogleAccessToken(code);
        SocialLoginDTO googleSocialLoginInfo = getGoogleSocialLoginInfo(googleAccessToken);

        if (googleSocialLoginInfo == null) {
            throw new ApplicationException(ApplicationError.USER_INFO_REQUEST_FAILED);
        }

        socialLoginMapper.insertMember(googleSocialLoginInfo);

        String jwtToken = jwtProcessor.generateAccessToken(googleSocialLoginInfo.getEmail(), "USER_ROLE");

        googleSocialLoginInfo.setGoogleAccessToken(googleAccessToken);
        return ResponseEntity.ok(JsonResponse.success(googleSocialLoginInfo));
    }

    private SocialLoginDTO googleValidateToken(String googleIdToken) {

        try {
            Jwt jwt = jwtDecoder.decode(googleIdToken);
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String googleId = jwt.getClaimAsString("sub");

            return SocialLoginDTO.builder()
                    .email(email)
                    .memberName(name)
                    .googleId(googleId)
                    .socialType("GOOGLE")
                    .build();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }
    }

    private String getGoogleAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", googleClientId);
        requestParams.add("client_secret", googleClientSecret);
        requestParams.add("code", code);
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("redirect_uri", "http://localhost:8080/api/v1/oauth2/google/callback");

        try {
            ResponseEntity<Map<String, String>> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(requestParams, new HttpHeaders()),
                    new ParameterizedTypeReference<Map<String, String>>() {}
            );

            return response.getBody().get("access_token");
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.OAUTH2_AUTHORIZATION_FAILED);
        }
    }

    private SocialLoginDTO getGoogleSocialLoginInfo(String googleAccessToken) {
        String googleUserInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> userInfo = response.getBody();
            return SocialLoginDTO.builder()
                    .email((String) userInfo.get("email"))
                    .memberName((String) userInfo.get("name"))
                    .socialType("GOOGLE")
                    .googleId((String) userInfo.get("sub"))
                    .build();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.USER_INFO_REQUEST_FAILED);
        }
    }

    @Override
    public boolean googleMemberExists(String email) {
        return socialLoginMapper.checkMemberExists(email) > 0;
    }

    @Override
    public void googleMemberJoin(SocialLoginDTO socialLoginDTO) {
        socialLoginMapper.insertMember(socialLoginDTO);
    }
}
