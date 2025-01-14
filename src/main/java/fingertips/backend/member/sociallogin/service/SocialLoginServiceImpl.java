package fingertips.backend.member.sociallogin.service;


import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import fingertips.backend.member.sociallogin.dto.TokenDTO;
import fingertips.backend.member.sociallogin.mapper.SocialLoginMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    private static final Logger logger = LoggerFactory.getLogger(SocialLoginServiceImpl.class);

    private final RestTemplate restTemplate;
    private final SocialLoginMapper socialLoginMapper;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    private final JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation("https://accounts.google.com");

    @Override
    public String getGoogleClientId() {
        return googleClientId;
    }

    @Override
    @Transactional
    public TokenDTO googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
        logger.info("Google ID 토큰: {}", googleIdToken);

        if (googleIdToken == null || googleIdToken.isEmpty()) {
            logger.error("구글 ID 토큰이 유효하지 않음");
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }

        SocialLoginDTO memberInfo = googleValidateToken(googleIdToken);
        setAdditionalTokenInfo(memberInfo, request);

        return processGoogleLogin(memberInfo, memberInfo.getGoogleAccessToken());
    }

    @Override
    @Transactional
    public TokenDTO googleCallback(String code) {
        logger.info("Google Callback 호출, 코드: {}", code);

        Map<String, String> tokenInfo = getGoogleAccessToken(code);
        logger.info("Google 액세스 토큰 정보: {}", tokenInfo);

        String accessToken = tokenInfo.get("access_token");
        String idToken = tokenInfo.get("id_token");
        String refreshToken = tokenInfo.get("refresh_token");
        Integer expiresIn = Integer.parseInt(tokenInfo.get("expires_in"));

        SocialLoginDTO memberInfo = fetchUserInfoFromGoogle(accessToken, idToken, refreshToken, expiresIn);
        logger.info("Google 사용자 정보: {}", memberInfo);

        Map<String, String> request = new HashMap<>();
        request.put("access_token", accessToken);
        request.put("id_token", idToken);
        request.put("refresh_token", refreshToken);
        request.put("expires_in", String.valueOf(expiresIn));

        setAdditionalTokenInfo(memberInfo, request);

        return processGoogleLogin(memberInfo, accessToken);
    }

    @Transactional
    public SocialLoginDTO fetchUserInfoFromGoogle(String accessToken, String idToken, String refreshToken, Integer expiresIn) {
        // 기존 사용자 정보 요청 URL
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
        logger.info("Google 사용자 정보 요청 URL: {}", userInfoUrl);

        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoUrl, Map.class);
        Map<String, Object> userInfo = response.getBody();
        logger.info("Google 사용자 정보 응답: {}", userInfo);

        if (userInfo != null) {
            String googleId = (String) userInfo.get("id");
            String email = (String) userInfo.get("email");
            String name = (String) userInfo.get("name");

            String peopleApiUrl = "https://people.googleapis.com/v1/people/me?personFields=birthdays,genders";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> peopleResponse = restTemplate.exchange(peopleApiUrl, HttpMethod.GET, entity, Map.class);
            Map<String, Object> personData = peopleResponse.getBody();
            logger.info("Google People API 응답: {}", personData);

            List<Map<String, Object>> birthdays = (List<Map<String, Object>>) personData.get("birthdays");
            String birthDate = null;
            if (birthdays != null && !birthdays.isEmpty()) {
                Map<String, Object> birthday = birthdays.get(0);
                Map<String, Object> date = (Map<String, Object>) birthday.get("date");
                if (date != null) {
                    birthDate = date.get("year") + "-" + date.get("month") + "-" + date.get("day");
                    logger.info("생년월일: {}", birthDate);
                }
            }

            List<Map<String, Object>> genders = (List<Map<String, Object>>) personData.get("genders");
            String gender = null;
            if (genders != null && !genders.isEmpty()) {
                Map<String, Object> genderInfo = genders.get(0);
                gender = (String) genderInfo.get("value");
                logger.info("성별: {}", gender);
            }

            SocialLoginDTO memberInfo = socialLoginMapper.getMemberByGoogleId(googleId);
            if (memberInfo == null) {
                logger.info("DB에 존재하지 않는 사용자. 새로운 사용자 생성 시작.");
                memberInfo = SocialLoginDTO.builder()
                        .googleId(googleId)
                        .email(email)
                        .memberName(name)
                        .googleAccessToken(accessToken)
                        .googleIdToken(idToken)
                        .googleRefreshToken(refreshToken)
                        .expiresIn(expiresIn)
                        .socialType("GOOGLE")
                        .birthday(birthDate)
                        .gender(gender)
                        .build();

                logger.info("새로운 사용자 정보: {}", memberInfo);

                memberInfo.setMemberId(email);

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String password = generateUniqueMemberId(googleId);
                String encodedPassword = passwordEncoder.encode(password);
                memberInfo.setPassword(encodedPassword);

                try {
                    socialLoginMapper.insertMember(memberInfo);
                    logger.info("새로운 사용자 DB 저장 완료");
                } catch (Exception e) {
                    logger.error("새로운 사용자 저장 중 오류 발생: ", e);
                    throw new ApplicationException(ApplicationError.DATABASE_ERROR);
                }

                SocialLoginDTO memberByGoogleId = socialLoginMapper.getMemberByGoogleId(googleId);
                memberInfo.setMemberIdx(memberByGoogleId.getMemberIdx());
                logger.info("새로운 회원 등록 완료: {}", memberInfo);

            } else {
                logger.info("기존 회원 정보: {}", memberInfo);
                if (memberInfo.getIsActive() == 0) {
                    logger.info("탈퇴한 회원, 다시 활성화 처리");
                    try {
                        socialLoginMapper.activateMemberByEmail(email);
                        logger.info("사용자 계정 활성화 완료");
                    } catch (Exception e) {
                        logger.error("계정 활성화 중 오류 발생: ", e);
                        throw new ApplicationException(ApplicationError.DATABASE_ERROR);
                    }
                }
            }

            return memberInfo;
        } else {
            throw new ApplicationException(ApplicationError.INVALID_USER_INFO);
        }
    }

    private TokenDTO processGoogleLogin(SocialLoginDTO memberInfo, String accessToken) {
        boolean memberExists = socialLoginMapper.checkMemberExists(memberInfo.getEmail()) > 0;
        logger.info("회원 존재 여부: {}", memberExists);

        if (!memberExists) {
            logger.info("새 회원 등록: {}", memberInfo.getEmail());
            insertMember(memberInfo);
        } else {
            logger.info("기존 회원 업데이트: {}", memberInfo.getEmail());
            updateMemberTokens(memberInfo);
        }

        String jwtToken = jwtProcessor.generateAccessToken(memberInfo.getMemberId(), "ROLE_USER", memberInfo.getMemberIdx(), memberInfo.getMemberName());
        String jwtRefreshToken = jwtProcessor.generateRefreshToken(memberInfo.getMemberId());

        logger.info("JWT 액세스 토큰: {}", jwtToken);
        logger.info("JWT 리프레시 토큰: {}", jwtRefreshToken);

        memberInfo.setGoogleAccessToken(accessToken);

        logger.info("processGoogleLogin 완료 후 응답 준비: {}", memberInfo);

        return new TokenDTO(jwtToken, jwtRefreshToken, memberInfo.getMemberId(), memberInfo.getMemberIdx(), memberInfo.getMemberName());
    }

    private Map<String, String> getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        try {
            logger.info("Google 토큰 요청 파라미터: {}", params);
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);

            Map<String, Object> responseBody = response.getBody();
            logger.info("Google 토큰 응답: {}", responseBody);

            if (responseBody != null) {
                String accessToken = (String) responseBody.get("access_token");
                String idToken = (String) responseBody.get("id_token");
                String refreshToken = (String) responseBody.get("refresh_token");
                Integer expiresIn = (Integer) responseBody.get("expires_in");

                if (accessToken != null) {
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("access_token", accessToken);
                    tokens.put("id_token", idToken);
                    tokens.put("refresh_token", refreshToken);
                    tokens.put("expires_in", String.valueOf(expiresIn));

                    return tokens;
                } else {
                    throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
                }
            } else {
                throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
            }
        } catch (HttpClientErrorException e) {
            logger.error("HttpClientErrorException 발생: ", e);
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            logger.error("액세스 토큰 요청 중 예외 발생: ", e);
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        }
    }

    private String generateUniqueMemberId(String googleId) {
        String uniqueId = googleId + "_" + UUID.randomUUID().toString().substring(0, 8);
        return uniqueId;
    }

    private SocialLoginDTO googleValidateToken(String googleIdToken) {
        try {
            logger.info("Google ID 토큰 검증 중: {}", googleIdToken);
            Jwt jwt = jwtDecoder.decode(googleIdToken);
            String email = jwt.getClaimAsString("email");
            String name = jwt.getClaimAsString("name");
            String googleId = jwt.getClaimAsString("sub");

            logger.info("Google ID 토큰 검증 완료, 이메일: {}, 이름: {}, Google ID: {}", email, name, googleId);

            return SocialLoginDTO.builder()
                    .email(email)
                    .memberName(name)
                    .googleId(googleId)
                    .socialType("GOOGLE")
                    .build();
        } catch (Exception e) {
            logger.error("Google ID 토큰 검증 실패: ", e);
            throw new ApplicationException(ApplicationError.INVALID_ID_TOKEN);
        }
    }

    private void setAdditionalTokenInfo(SocialLoginDTO memberInfo, Map<String, String> request) {
        logger.info("추가 토큰 정보 설정: {}", request);

        String accessToken = request.get("access_token");
        String idToken = request.get("id_token");
        String refreshToken = request.get("refresh_token");
        String expiresIn = request.get("expires_in");

        memberInfo.setGoogleAccessToken(accessToken);
        memberInfo.setGoogleIdToken(idToken);
        memberInfo.setGoogleRefreshToken(refreshToken);

        if (expiresIn == null || expiresIn.isEmpty()) {
            memberInfo.setExpiresIn(0);
        } else {
            try {
                memberInfo.setExpiresIn(Integer.parseInt(expiresIn));
            } catch (NumberFormatException e) {
                memberInfo.setExpiresIn(0);
            }
        }
    }

    @Override
    public boolean googleMemberExists(String email) {
        return socialLoginMapper.checkMemberExists(email) > 0;
    }

    @Override
    @Transactional
    public void googleMemberJoin(SocialLoginDTO socialLoginDTO) {
        try {
            socialLoginMapper.insertMember(socialLoginDTO);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }

    @Override
    @Transactional
    public TokenDTO googleLoginWithTokens(SocialLoginDTO socialLoginDTO) {
        return processGoogleLogin(socialLoginDTO, null);
    }

    private void insertMember(SocialLoginDTO memberInfo) {
        try {
            socialLoginMapper.insertMember(memberInfo);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }

    private void updateMemberTokens(SocialLoginDTO memberInfo) {
        try {
            socialLoginMapper.updateMemberTokens(memberInfo);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.DATABASE_ERROR);
        }
    }
}
