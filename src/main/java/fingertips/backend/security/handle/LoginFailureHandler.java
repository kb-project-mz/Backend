package fingertips.backend.security.handle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fingertips.backend.exception.dto.ErrorResponse;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.AuthDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final MemberMapper memberMapper;

    @Getter
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 3 * 60 * 1000; // 3분

    @Getter
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String memberId = (String) request.getAttribute("memberId");
        MemberDTO memberDTO = memberMapper.getMemberByMemberId(memberId);

        if (exception instanceof UsernameNotFoundException || memberDTO == null) {
            JsonResponse.sendError(response, ApplicationError.MEMBER_ID_NOT_FOUND);
            return;
        }

        long currentTime = System.currentTimeMillis();

        if (memberDTO.getIsLoginLocked() == 1) {
            if (currentTime < memberDTO.getLoginLockTime() + LOCK_TIME_DURATION) {
                log.info("currentTime: " + currentTime);
                log.info("memberDTO.getLoginLockTime() + LOCK_TIME_DURATION: " + (memberDTO.getLoginLockTime() + LOCK_TIME_DURATION));
                JsonResponse.sendError(response, ApplicationError.LOGIN_ATTEMPTS);
                return;
            } else {
                memberDTO.setIsLoginLocked(0);
                memberDTO.setLoginLockTime(0L);
                memberMapper.updateLockStatus(memberDTO);
                attemptsCache.put(memberId, 0);
            }
        }

        int attempts = attemptsCache.getOrDefault(memberId, 0) + 1;
        attemptsCache.put(memberId, attempts);
        log.info("attempts: " + attempts);

        if (attempts >= MAX_ATTEMPTS) {
            memberDTO.setIsLoginLocked(1);
            memberDTO.setLoginLockTime(currentTime);
            log.info("currentTime: " + currentTime);
            memberMapper.updateLockStatus(memberDTO);
            JsonResponse.sendError(response, ApplicationError.LOGIN_ATTEMPTS);
        } else {
            JsonResponse.sendError(response, ApplicationError.PASSWORD_INVALID);
        }
    }
}
