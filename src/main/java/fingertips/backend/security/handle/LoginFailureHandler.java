package fingertips.backend.security.handle;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fingertips.backend.security.util.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final MemberMapper mapper;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 3 * 60 * 1000;
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTimeCache = new ConcurrentHashMap<>();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String memberId = request.getParameter("memberId");
        long currentTime = System.currentTimeMillis();

        MemberDTO memberDTO = mapper.getMember(memberId);

        if (memberDTO != null && memberDTO.isLoginLocked()) {
            if (currentTime < memberDTO.getLoginLockTime() + LOCK_TIME_DURATION) {
                JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "로그인 시도가 초과되었습니다. 잠시 후 다시 시도하세요.");
                return;
            } else {
                memberDTO.setLoginLocked(false);
                memberDTO.setLoginLockTime(0);
                mapper.updateMember(memberDTO); // DB 업데이트
            }
        }

        int attempts = attemptsCache.getOrDefault(memberId, 0) + 1;
        attemptsCache.put(memberId, attempts);

        if (attempts > MAX_ATTEMPTS) {
            log.warn("로그인 시도 초과: 사용자명={}, 시도 횟수={}", memberId, attempts);
            if (memberDTO != null) {
                memberDTO.setLoginLocked(true);
                memberDTO.setLoginLockTime(currentTime);
                mapper.updateMember(memberDTO); // DB 업데이트
            }
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "로그인 시도가 초과되었습니다. 잠시 후 다시 시도하세요.");
        } else {
            if (memberDTO != null) {
                memberDTO.setLoginLocked(false); // 잠금 해제
                memberDTO.setLoginLockTime(0);
                mapper.updateMember(memberDTO); // DB 업데이트
            }
            JsonResponse.sendError(response, HttpStatus.UNAUTHORIZED, "사용자 ID 또는 비밀번호가 일치하지 않습니다.");
        }
        log.error("로그인 실패: 사용자명={}, 예외={}", memberId, exception.getMessage());
    }
}
