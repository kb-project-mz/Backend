package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.mapper.SocialLoginMapper;
import fingertips.backend.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.util.MultiValueMap;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;

import java.net.URI;
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
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
        String googleAccessToken = request.get("access_token");
        String googleRefreshToken = request.get("refresh_token");
        int expiresIn = Integer.parseInt(request.get("expires_in"));

        if (googleIdToken == null || googleIdToken.isEmpty()) {
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        // ID Token으로 사용자 정보 추출
        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);

        // 회원 로그인/회원가입 처리
        return processGoogleLogin(memberInfo, googleAccessToken, googleIdToken, googleRefreshToken, expiresIn);
    }

    @Override
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code) {
        // 구글에서 Access Token, Id Token, Refresh Token 가져오기
        Map<String, String> googleTokenInfo = getGoogleToken(code);
        System.out.println("#### token Info ####" );
        System.out.println(googleTokenInfo.get("access_token"));
        System.out.println(googleTokenInfo.get("refresh_token"));
        System.out.println(googleTokenInfo.get("id_token"));
        // 구글에서 사용자 정보를 가져옴
        SocialLoginDTO googleSocialLoginInfo = getGoogleSocialLoginInfo(googleTokenInfo.get("access_token"));

        if (googleSocialLoginInfo == null) {
            throw new ApplicationException(ApplicationError.USER_INFO_REQUEST_FAILED);
        }

        // 회원 로그인/회원가입 처리
//        processGoogleLogin(googleSocialLoginInfo, googleTokenInfo.get("access_token"), googleTokenInfo.get("id_token"), googleTokenInfo.get("refresh_token"), 3600);

        // 처리 끝나고 프론트로 리다이렉트
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:5173/home"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    private ResponseEntity<JsonResponse<SocialLoginDTO>> processGoogleLogin(SocialLoginDTO memberInfo, String googleAccessToken, String googleIdToken, String googleRefreshToken, int expiresIn) {
        int memberCount = socialLoginMapper.checkMemberExists(memberInfo.getEmail());

        if (memberCount == 0) {
            // 회원가입
            memberInfo.setGoogleIdToken(googleIdToken);
            memberInfo.setGoogleAccessToken(googleAccessToken);
            memberInfo.setGoogleRefreshToken(googleRefreshToken);
            memberInfo.setExpiresIn(expiresIn);
            socialLoginMapper.insertMember(memberInfo);
            return ResponseEntity.ok(JsonResponse.success(memberInfo));
        } else {
            // 로그인
            socialLoginMapper.updateMemberTokens(
                    memberInfo.getEmail(),
                    googleAccessToken,
                    googleIdToken,
                    googleRefreshToken,
                    String.valueOf(expiresIn)
            );
            return ResponseEntity.ok(JsonResponse.success(memberInfo));
        }
    }


    private SocialLoginDTO googleValidateToken(String googleIdToken) {
        try {
            // Google ID Token을 디코드하여 사용자 정보 추출
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

    private Map getGoogleToken(String code) {

        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", code);
        requestParams.add("client_id", googleClientId);
        requestParams.add("client_secret", googleClientSecret);
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("redirect_uri", "http://localhost:8080/api/v1/member/login/google/callback");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response;

        try {
            response = restTemplate.postForEntity(tokenUrl, requestParams, Map.class);
            System.out.println("tokenInfo");
            System.out.println(response.getBody());
            // 액세스 토큰 추출
            return response.getBody();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.OAUTH2_AUTHORIZATION_FAILED);
        }
    }

    private SocialLoginDTO getGoogleSocialLoginInfo(String googleAccessToken) {

        String googleUserInfoUrl = "https://www.googleapis.com/userinfo/v2/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, request, Map.class);

            Map<String, Object> userInfo = response.getBody();
            // 사용자 정보 추출
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");
            String socialId = (String) userInfo.get("id");
            System.out.println("email: " + email
            + " name: " + name
            + " socialId: " + socialId
            );
            return SocialLoginDTO.builder()
                    .email((String) userInfo.get("email"))
                    .memberName((String) userInfo.get("name"))
                    .socialType("GOOGLE")
                    .googleId((String) userInfo.get("id"))
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
