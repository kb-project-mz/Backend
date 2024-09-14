package fingertips.backend.security.handle;

import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.security.account.dto.UserDTO;
import fingertips.backend.security.util.JsonResponse;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Log4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProcessor jwtProcessor;

    private AuthDTO makeAuth(UserDTO user) {
        String username = user.getUsername();
        String userID = user.getUserId();
        List<String> roles = user.getRoles();
        String accessToken = jwtProcessor.generateAccessToken(username, roles);
        String refreshToken = jwtProcessor.generateRefreshToken(username);
        return new AuthDTO(accessToken, userID, refreshToken);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UserDTO user = (UserDTO) authentication.getPrincipal();
        AuthDTO result = makeAuth(user);
        JsonResponse.send(response, result);
    }
}

