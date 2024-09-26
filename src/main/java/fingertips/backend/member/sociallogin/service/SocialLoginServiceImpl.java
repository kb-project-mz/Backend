package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.mapper.SocialLoginMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
        if (googleIdToken == null || googleIdToken.isEmpty()) {
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        // ID Token으로 사용자 정보 추출
        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);

        // 사용자 토큰 정보를 DTO에 추가
        memberInfo.setGoogleAccessToken(request.get("access_token"));
        memberInfo.setGoogleIdToken(request.get("id_token"));
        memberInfo.setGoogleRefreshToken(request.get("refresh_token"));
        memberInfo.setExpiresIn(Integer.parseInt(request.get("expires_in")));

        // 회원 로그인/회원가입 처리
        return processGoogleLogin(memberInfo, null);
    }

    @Override
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code) {
        // 구글에서 Access Token, Id Token, Refresh Token 가져오기
        Map<String, String> googleTokenInfo = getGoogleToken(code);

        // 구글에서 사용자 정보를 가져옴
        SocialLoginDTO googleSocialLoginInfo = getGoogleSocialLoginInfo(googleTokenInfo.get("access_token"));

        if (googleSocialLoginInfo == null) {
            throw new ApplicationException(ApplicationError.USER_INFO_REQUEST_FAILED);
        }

        // 사용자 토큰 정보를 DTO에 추가
        googleSocialLoginInfo.setGoogleAccessToken(googleTokenInfo.get("access_token"));
        googleSocialLoginInfo.setGoogleIdToken(googleTokenInfo.get("id_token"));
        googleSocialLoginInfo.setGoogleRefreshToken(googleTokenInfo.get("refresh_token"));
        googleSocialLoginInfo.setExpiresIn(3600);

        // 회원 로그인/회원가입 처리
        return processGoogleLogin(googleSocialLoginInfo, null);
    }

    private ResponseEntity<JsonResponse<SocialLoginDTO>> processGoogleLogin(SocialLoginDTO memberInfo, HttpServletRequest request) {
        // 회원이 존재하는지 확인
        boolean isExistingMember = socialLoginMapper.checkMemberExists(memberInfo.getEmail()) > 0;

        // 회원이 존재하지 않으면 회원가입 처리
        if (!isExistingMember) {
            insertMember(memberInfo);
        } else {
            // 로그인 처리 (토큰 업데이트)
            updateMemberTokens(memberInfo);
        }

        // 세션에 사용자 정보 저장
        if (request != null) {
            saveSession(request, memberInfo);
        }

        // JWT 생성 (사용자 이메일과 역할 정보를 포함하여 AccessToken 생성)
        String jwtToken = jwtProcessor.generateAccessToken(memberInfo.getEmail(), "ROLE_USER");  // 기본적으로 ROLE_USER로 설정

        // JWT 토큰을 응답으로 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        return new ResponseEntity<>(JsonResponse.success(memberInfo), headers, HttpStatus.OK);
    }


    // 회원가입 로직 함수로 분리
    private void insertMember(SocialLoginDTO memberInfo) {
        memberInfo.setSocialType("GOOGLE");  // 구글 소셜 로그인으로 가입된 사용자임을 표시
        socialLoginMapper.insertMember(memberInfo);
    }

    // 토큰 업데이트 로직 함수로 분리
    private void updateMemberTokens(SocialLoginDTO memberInfo) {
        socialLoginMapper.updateMemberTokens(
                memberInfo.getEmail(),
                memberInfo.getGoogleAccessToken(),
                memberInfo.getGoogleIdToken(),
                memberInfo.getGoogleRefreshToken(),
                String.valueOf(memberInfo.getExpiresIn())
        );
    }

    // 세션 저장 로직 함수로 분리
    private void saveSession(HttpServletRequest request, SocialLoginDTO memberInfo) {
        HttpSession session = request.getSession();
        session.setAttribute("loggedInUser", memberInfo);
    }

    // 구글 ID 토큰 검증 및 사용자 정보 추출
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

    // 구글 액세스 토큰 가져오기
    private Map<String, String> getGoogleToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("code", code);
        requestParams.add("client_id", googleClientId);
        requestParams.add("client_secret", googleClientSecret);
        requestParams.add("grant_type", "authorization_code");
        requestParams.add("redirect_uri", "http://localhost:8080/api/v1/member/login/google/callback");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, requestParams, Map.class);
            return response.getBody();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.OAUTH2_AUTHORIZATION_FAILED);
        }
    }

    // 구글 소셜 로그인 사용자 정보 가져오기
    private SocialLoginDTO getGoogleSocialLoginInfo(String googleAccessToken) {
        String googleUserInfoUrl = "https://www.googleapis.com/userinfo/v2/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, request, Map.class);
            Map<String, Object> userInfo = response.getBody();
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
