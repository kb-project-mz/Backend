package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.mapper.SocialLoginMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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

    private static final Logger logger = LoggerFactory.getLogger(SocialLoginServiceImpl.class);

    private final RestTemplate restTemplate;
    private final SocialLoginMapper socialLoginMapper;
    private final JwtProcessor jwtProcessor;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private final JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation("https://accounts.google.com");

    @Override
    public String getGoogleClientId() {
        logger.info("Google Client ID 요청됨");
        return googleClientId;
    }

    @Override
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
        if (googleIdToken == null || googleIdToken.isEmpty()) {
            logger.error("구글 ID 토큰이 유효하지 않음");
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        logger.info("구글 ID 토큰 검증 시작");
        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);
        setAdditionalTokenInfo(memberInfo, request);

        return processGoogleLogin(memberInfo, null);
    }

    @Override
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code) {
        logger.info("구글 콜백 처리 시작, 코드: {}", code);

        String accessToken = getGoogleAccessToken(code);
        logger.info("구글 액세스 토큰 획득: {}", accessToken);

        // 액세스 토큰으로 사용자 정보 요청
        SocialLoginDTO memberInfo = fetchUserInfoFromGoogle(accessToken);
        logger.info("구글 사용자 정보 획득: {}", memberInfo);

        return processGoogleLogin(memberInfo, accessToken);
    }

    @Override
    public boolean googleMemberExists(String email) {
        logger.info("회원 존재 여부 확인 중: {}", email);
        return socialLoginMapper.checkMemberExists(email) > 0;
    }

    @Override
    public void googleMemberJoin(SocialLoginDTO socialLoginDTO) {
        logger.info("회원 가입 처리 중: {}", socialLoginDTO.getEmail());
        socialLoginMapper.insertMember(socialLoginDTO);
    }

    @Override
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleLoginWithTokens(SocialLoginDTO socialLoginDTO) {
        logger.info("구글 토큰 검증 시작: {}", socialLoginDTO.getGoogleIdToken());
        return processGoogleLogin(socialLoginDTO, null);
    }

    private String getGoogleAccessToken(String code) {
        logger.info("구글 Access Token 요청 시작, 코드: {}", code);
        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        // 요청 파라미터 확인 로그
        logger.info("구글 Access Token 요청 파라미터: {}", params);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);

            logger.info("구글 Access Token 요청 응답 상태: {}", response.getStatusCode());
            logger.info("구글 Access Token 요청 응답 바디: {}", response.getBody());

            Map<String, String> responseBody = response.getBody();
            if (responseBody != null && responseBody.get("access_token") != null) {
                logger.info("구글 Access Token 획득 성공");
                return responseBody.get("access_token");
            } else {
                logger.error("구글 Access Token 응답에 Access Token이 없습니다: {}", responseBody);
                throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP 오류 발생: 상태 코드 = {}, 메시지 = {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            logger.error("구글 Access Token 요청 중 예외 발생", e);
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        }

    }

    private SocialLoginDTO fetchUserInfoFromGoogle(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoUrl, Map.class);
        Map<String, Object> userInfo = response.getBody();

        if (userInfo != null) {
            return SocialLoginDTO.builder()
                    .email((String) userInfo.get("email"))
                    .memberName((String) userInfo.get("name"))
                    .googleId((String) userInfo.get("id"))
                    .socialType("GOOGLE")
                    .build();
        } else {
            throw new ApplicationException(ApplicationError.INVALID_USER_INFO);
        }
    }

    private SocialLoginDTO googleValidateToken(String googleIdToken) {
        try {
            Jwt jwt = jwtDecoder.decode(googleIdToken);
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String googleId = jwt.getClaimAsString("sub");

            logger.info("구글 토큰 유효성 검증 완료: 이메일={}, 이름={}", email, name);

            return SocialLoginDTO.builder()
                    .email(email)
                    .memberName(name)
                    .googleId(googleId)
                    .socialType("GOOGLE")
                    .build();
        } catch (Exception e) {
            logger.error("구글 ID 토큰 검증 실패", e);
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }
    }

    private void setAdditionalTokenInfo(SocialLoginDTO memberInfo, Map<String, String> request) {
        memberInfo.setGoogleAccessToken(request.get("access_token"));
        memberInfo.setGoogleIdToken(request.get("id_token"));
        memberInfo.setGoogleRefreshToken(request.get("refresh_token"));
        memberInfo.setExpiresIn(Integer.parseInt(request.get("expires_in")));
    }

    private ResponseEntity<JsonResponse<SocialLoginDTO>> processGoogleLogin(SocialLoginDTO memberInfo, String accessToken) {
        logger.info("processGoogleLogin 메서드 시작");

        // 이메일로 회원 존재 여부 확인
        logger.info("확인할 이메일: {}", memberInfo.getEmail());
        boolean memberExists = socialLoginMapper.checkMemberExists(memberInfo.getEmail()) > 0;
        logger.info("회원 존재 여부: {}", memberExists);

        if (!memberExists) {
            logger.info("회원 존재하지 않음. 회원가입 처리 시작.");
            insertMember(memberInfo);
        } else {
            logger.info("회원 존재. 토큰 업데이트 시작.");
            updateMemberTokens(memberInfo);
        }

        // JWT 토큰 생성
        String jwtToken = jwtProcessor.generateAccessToken(memberInfo.getEmail(), "ROLE_USER");
        logger.info("JWT 토큰 생성 완료: {}", jwtToken);

        // HTTP 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        logger.info("processGoogleLogin 완료 후 응답 준비");
        return new ResponseEntity<>(JsonResponse.success(memberInfo), headers, HttpStatus.OK);
    }


    private void insertMember(SocialLoginDTO memberInfo) {

        memberInfo.setMemberId(memberInfo.getGoogleId());
        socialLoginMapper.insertMember(memberInfo);
    }

    private void updateMemberTokens(SocialLoginDTO memberInfo) {
        socialLoginMapper.updateMemberTokens(memberInfo);
    }
}

