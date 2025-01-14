package fingertips.backend.security.handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

@Log4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;
    private final LoginFailureHandler loginFailureHandler;

    private AuthDTO makeAuth(MemberDTO user) {

        Integer memberIdx = user.getMemberIdx();
        String memberId = user.getMemberId();
        String memberName = user.getMemberName();
        String imageUrl = user.getImageUrl();
        String role = user.getRole();

        String accessToken = jwtProcessor.generateAccessToken(memberId, role, memberIdx, memberName);
        String refreshToken = jwtProcessor.generateRefreshToken(memberId);

        return AuthDTO.builder()
                .memberIdx(memberIdx)
                .memberId(memberId)
                .memberName(memberName)
                .imageUrl(imageUrl)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(role)
                .build();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String memberId = userDetails.getUsername();
        MemberDTO memberDTO = memberMapper.getMemberByMemberId(memberId);

        AuthDTO result = makeAuth(memberDTO);
        JsonResponse.sendToken(response, result);
        loginFailureHandler.getAttemptsCache().put(memberId, 0);

        memberDTO.setIsLoginLocked(0);
        memberDTO.setLoginLockTime(0L);
        memberMapper.updateLockStatus(memberDTO);
    }
}
