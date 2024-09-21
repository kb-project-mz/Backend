package fingertips.backend.security.handle;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.security.util.JsonResponse;
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

@Log4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;

    private AuthDTO makeAuth(MemberDTO user) {

        String memberId = user.getMemberId();
        String role = user.getRole();

        String accessToken = jwtProcessor.generateAccessToken(memberId, role);
        String refreshToken = jwtProcessor.generateRefreshToken(memberId);

        return AuthDTO.builder()
                .memberId(memberId)
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
        MemberDTO memberDTO = memberMapper.getMember(memberId);
        AuthDTO result = makeAuth(memberDTO);
        JsonResponse.send(response, result);
    }
}
