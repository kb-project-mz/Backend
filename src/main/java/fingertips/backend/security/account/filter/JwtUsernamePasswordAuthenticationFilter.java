package fingertips.backend.security.account.filter;

import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.handle.LoginFailureHandler;
import fingertips.backend.security.handle.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    public JwtUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            LoginSuccessHandler loginSuccessHandler,
            LoginFailureHandler loginFailureHandler) {
        super(authenticationManager);
        setFilterProcessesUrl("/api/v1/member/login");
        setAuthenticationSuccessHandler(loginSuccessHandler);
        setAuthenticationFailureHandler(loginFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        LoginDTO login = LoginDTO.of(request);
        if (memberMapper.getMemberByMemberId(login.getMemberId()) == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }
        request.setAttribute("memberId", login.getMemberId());

        int attempts = loginFailureHandler.getAttemptsCache().getOrDefault(login.getMemberId(), 0);
        if (attempts >= LoginFailureHandler.getMAX_ATTEMPTS()) {
            throw new BadCredentialsException("로그인 시도가 초과되었습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(login.getMemberId(), login.getPassword());

        return getAuthenticationManager().authenticate(authenticationToken);
    }
}