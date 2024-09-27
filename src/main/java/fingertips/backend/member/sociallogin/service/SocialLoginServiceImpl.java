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

import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    @Transactional
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
        if (googleIdToken == null || googleIdToken.isEmpty()) {
            logger.error("구글 ID 토큰이 유효하지 않음");
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        logger.info("구글 ID 토큰 검증 시작");
        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);

        logger.info("구글 ID 토큰 검증 후 memberInfo: {}", memberInfo);

        logger.info("setAdditionalTokenInfo 호출 준비");
        setAdditionalTokenInfo(memberInfo, request);

        return processGoogleLogin(memberInfo, memberInfo.getGoogleAccessToken());
    }

    @Override
    @Transactional
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleCallback(String code) {
        logger.info("구글 콜백 처리 시작, 코드: {}", code);

        Map<String, String> tokenInfo = getGoogleAccessToken(code);
        String accessToken = tokenInfo.get("access_token");
        String idToken = tokenInfo.get("id_token");
        String refreshToken = tokenInfo.get("refresh_token");
        Integer expiresIn = Integer.parseInt(tokenInfo.get("expires_in"));

        logger.info("구글 액세스 토큰 획득: {}", accessToken);
        logger.info("ID 토큰: {}", idToken);
        logger.info("리프레시 토큰: {}", refreshToken);
        logger.info("구글 콜백에서 expires_in 값: {}", expiresIn);

        SocialLoginDTO memberInfo = fetchUserInfoFromGoogle(accessToken, idToken, refreshToken, expiresIn);
        logger.info("구글 사용자 정보 획득: {}", memberInfo);

        Map<String, String> request = new HashMap<>();
        request.put("access_token", accessToken);
        request.put("id_token", idToken);
        request.put("refresh_token", refreshToken);
        request.put("expires_in", String.valueOf(expiresIn));

        setAdditionalTokenInfo(memberInfo, request);

        return processGoogleLogin(memberInfo, accessToken);
    }

    @Override
    public boolean googleMemberExists(String email) {
        logger.info("회원 존재 여부 확인 중: {}", email);
        return socialLoginMapper.checkMemberExists(email) > 0;
    }

    @Override
    @Transactional
    public void googleMemberJoin(SocialLoginDTO socialLoginDTO) {
        logger.info("회원 가입 처리 중: {}", socialLoginDTO.getEmail());
        try {
            socialLoginMapper.insertMember(socialLoginDTO);
            logger.info("회원 가입 성공: {}", socialLoginDTO.getEmail());
        } catch (Exception e) {
            logger.error("회원 가입 중 오류 발생: {}", e.getMessage(), e);
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<JsonResponse<SocialLoginDTO>> googleLoginWithTokens(SocialLoginDTO socialLoginDTO) {
        logger.info("구글 토큰 검증 시작: {}", socialLoginDTO.getGoogleIdToken());
        return processGoogleLogin(socialLoginDTO, null);
    }

    private void insertMember(SocialLoginDTO memberInfo) {
        logger.info("insertMember 호출: memberInfo={}", memberInfo);
        String uniqueMemberId = generateUniqueMemberId(memberInfo.getGoogleId());
        memberInfo.setMemberId(uniqueMemberId);
        logger.info("회원 ID 설정: {}", uniqueMemberId);
        try {
            socialLoginMapper.insertMember(memberInfo);
            logger.info("회원가입 완료: {}", memberInfo);
        } catch (Exception e) {
            logger.error("회원가입 중 오류 발생: {}", e.getMessage(), e);
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }

    private void updateMemberTokens(SocialLoginDTO memberInfo) {
        logger.info("updateMemberTokens 호출: memberInfo={}", memberInfo);
        try {
            socialLoginMapper.updateMemberTokens(memberInfo);
            logger.info("회원 토큰 업데이트 완료: {}", memberInfo);
        } catch (Exception e) {
            logger.error("회원 토큰 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }

    private ResponseEntity<JsonResponse<SocialLoginDTO>> processGoogleLogin(SocialLoginDTO memberInfo, String accessToken) {
        logger.info("processGoogleLogin 메서드 시작: memberInfo={}, accessToken={}", memberInfo, accessToken);

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

        String jwtToken = jwtProcessor.generateAccessToken(memberInfo.getEmail(), "ROLE_USER");
        logger.info("JWT 토큰 생성 완료: {}", jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);
        logger.info("Authorization 헤더 추가 완료: {}", headers);

        memberInfo.setGoogleAccessToken(accessToken);
        logger.info("processGoogleLogin accessToken 저장: {}", memberInfo.getGoogleAccessToken());

        logger.info("processGoogleLogin 완료 후 응답 준비");



        return new ResponseEntity<>(JsonResponse.success(memberInfo), headers, HttpStatus.OK);
    }

    private String generateUniqueMemberId(String googleId) {
        String uniqueId = googleId + "_" + UUID.randomUUID().toString().substring(0, 8);
        logger.info("고유 회원 ID 생성: {}", uniqueId);
        return uniqueId;
    }

    private Map<String, String> getGoogleAccessToken(String code) {
        logger.info("구글 Access Token 요청 시작, 코드: {}", code);
        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri",googleRedirectUri);
        System.out.println(googleRedirectUri);
        params.add("grant_type", "authorization_code");

        logger.info("구글 Access Token 요청 파라미터: {}", params);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);

            logger.info("구글 Access Token 요청 응답 상태: {}", response.getStatusCode());
            logger.info("구글 Access Token 요청 응답 바디: {}", response.getBody());

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                String accessToken = (String) responseBody.get("access_token");
                String idToken = (String) responseBody.get("id_token");
                String refreshToken = (String) responseBody.get("refresh_token");
                Integer expiresIn = (Integer) responseBody.get("expires_in");

                if (expiresIn != null) {
                    logger.info("expires_in 값 확인: {}", expiresIn);
                } else {
                    logger.warn("expires_in 값이 응답에 없음");
                }

                if (accessToken != null) {
                    logger.info("구글 Access Token 획득 성공");
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", accessToken);
                    tokens.put("id_token", idToken);
                    tokens.put("refresh_token", refreshToken);

                    if (expiresIn != null) {
                        tokens.put("expires_in", String.valueOf(expiresIn));  // String으로 변환하여 전달
                    }

                    return tokens;
                } else {
                    logger.error("구글 Access Token 응답에 Access Token이 없습니다: {}", responseBody);
                    throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
                }
            } else {
                logger.error("구글 Access Token 응답이 null입니다.");
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

    private SocialLoginDTO fetchUserInfoFromGoogle(String accessToken, String idToken, String refreshToken, Integer expiresIn) {
        logger.info("fetchUserInfoFromGoogle 호출: accessToken={}", accessToken);
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        logger.info("구글 사용자 정보 URL: {}", userInfoUrl);
        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoUrl, Map.class);
        Map<String, Object> userInfo = response.getBody();

        logger.info("구글 사용자 정보 응답: {}", userInfo);

        if (userInfo != null) {
            SocialLoginDTO memberInfo = SocialLoginDTO.builder()
                    .email((String) userInfo.get("email"))
                    .memberName((String) userInfo.get("name"))
                    .googleId((String) userInfo.get("id"))
                    .socialType("GOOGLE")
                    .googleAccessToken(accessToken)
                    .googleIdToken(idToken)
                    .googleRefreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .build();

            logger.info("구글 사용자 정보와 액세스 토큰 설정 완료: {}", memberInfo);

            return memberInfo;
        } else {
            logger.error("구글 사용자 정보가 null입니다.");
            throw new ApplicationException(ApplicationError.INVALID_USER_INFO);
        }
    }

    private SocialLoginDTO googleValidateToken(String googleIdToken) {
        logger.info("googleValidateToken 호출: googleIdToken={}", googleIdToken);
        try {
            Jwt jwt = jwtDecoder.decode(googleIdToken);
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String googleId = jwt.getClaimAsString("sub");

            logger.info("구글 토큰 유효성 검증 완료: 이메일={}, 이름={}, 구글ID={}", email, name, googleId);

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
        logger.info("setAdditionalTokenInfo 호출: memberInfo={}, request={}", memberInfo, request);

        String accessToken = request.get("access_token");
        String idToken = request.get("id_token");
        String refreshToken = request.get("refresh_token");
        String expiresIn = request.get("expires_in");

        logger.info("액세스 토큰: {}", accessToken);
        logger.info("ID 토큰: {}", idToken);
        logger.info("리프레시 토큰: {}", refreshToken);
        logger.info("expires_in: {}", expiresIn);

        memberInfo.setGoogleAccessToken(accessToken);
        memberInfo.setGoogleIdToken(idToken);
        memberInfo.setGoogleRefreshToken(refreshToken);

        if (expiresIn == null || expiresIn.isEmpty()) {
            logger.warn("expires_in 값이 없음, 기본값 0 설정");
            memberInfo.setExpiresIn(0);
        } else {
            try {
                memberInfo.setExpiresIn(Integer.parseInt(expiresIn));  // String 값을 int로 변환
                logger.info("expires_in 값 설정됨: {}", expiresIn);
            } catch (NumberFormatException e) {
                logger.error("expires_in 값을 정수로 변환하는 중 오류 발생: {}", e.getMessage());
                memberInfo.setExpiresIn(0); // 오류 발생 시 기본값으로 설정
            }
        }

        logger.info("추가 토큰 정보 설정 완료: {}", memberInfo);
    }
}
