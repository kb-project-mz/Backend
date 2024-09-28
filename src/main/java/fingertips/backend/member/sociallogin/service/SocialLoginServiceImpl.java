package fingertips.backend.member.sociallogin.service;

import fingertips.backend.exception.dto.JsonResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;

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
        return googleClientId;
    }

    @Override
    @Transactional
    public TokenDTO googleLogin(Map<String, String> request) {
        String googleIdToken = request.get("id_token");
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

        Map<String, String> tokenInfo = getGoogleAccessToken(code);
        String accessToken = tokenInfo.get("access_token");
        String idToken = tokenInfo.get("id_token");
        String refreshToken = tokenInfo.get("refresh_token");
        Integer expiresIn = Integer.parseInt(tokenInfo.get("expires_in"));


        SocialLoginDTO memberInfo = fetchUserInfoFromGoogle(accessToken, idToken, refreshToken, expiresIn);

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
        String uniqueMemberId = generateUniqueMemberId(memberInfo.getGoogleId());
        memberInfo.setMemberId(uniqueMemberId);
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

    private TokenDTO processGoogleLogin(SocialLoginDTO memberInfo, String accessToken) {
        boolean memberExists = socialLoginMapper.checkMemberExists(memberInfo.getEmail()) > 0;

        if (!memberExists) {
            insertMember(memberInfo);
        } else {
            updateMemberTokens(memberInfo);
        }

        String jwtToken = jwtProcessor.generateAccessToken(memberInfo.getEmail(), "ROLE_USER");
        String jwtRefreshToken = jwtProcessor.generateRefreshToken(memberInfo.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwtToken);

        memberInfo.setGoogleAccessToken(accessToken);

        if (memberInfo.getMemberId() == null) {
        }

        logger.info("processGoogleLogin 완료 후 응답 준비");

        JsonResponse<SocialLoginDTO> response = JsonResponse.success(memberInfo);
        System.out.println(response);
        return new TokenDTO(jwtToken, jwtRefreshToken, memberInfo.getMemberId(), memberInfo.getMemberName());

    }

    private String generateUniqueMemberId(String googleId) {
        String uniqueId = googleId + "_" + UUID.randomUUID().toString().substring(0, 8);
        return uniqueId;
    }

    private Map<String, String> getGoogleAccessToken(String code) {

        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri",googleRedirectUri);
        params.add("grant_type", "authorization_code");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, params, Map.class);

            Map<String, Object> responseBody = response.getBody();
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

                    if (expiresIn != null) {
                        tokens.put("expires_in", String.valueOf(expiresIn));
                    }

                    return tokens;
                } else {
                    throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
                }
            } else {
                throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
            }
        } catch (HttpClientErrorException e) {
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.INVALID_ACCESS_TOKEN);
        }
    }

    private SocialLoginDTO fetchUserInfoFromGoogle(String accessToken, String idToken, String refreshToken, Integer expiresIn) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        ResponseEntity<Map> response = restTemplate.getForEntity(userInfoUrl, Map.class);
        Map<String, Object> userInfo = response.getBody();

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

            return memberInfo;
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

    private void setAdditionalTokenInfo(SocialLoginDTO memberInfo, Map<String, String> request) {

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
}
