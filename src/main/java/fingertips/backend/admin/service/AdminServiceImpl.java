package fingertips.backend.admin.service;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.mapper.AdminMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    public int getTodaySignUpCount() {
        return adminMapper.getTodaySignUpCount();
    }

    public int getTodayLoginCount() {
        return adminMapper.getTodayLoginCount();
    }

    public int getTodayVisitCount() {
        return adminMapper.getTodayVisitCount();
    }

    public int getTodayWithdrawalCount() {
        return adminMapper.getTodayWithdrawalCount();
    }

    public void logLogin(int memberIdx, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();

        UserMetricsDTO loginLogDTO = new UserMetricsDTO();
        loginLogDTO.setMemberIdx(memberIdx);
        loginLogDTO.setUserAgent(userAgent);
        loginLogDTO.setIpAddress(ipAddress);

        adminMapper.insertLoginLog(loginLogDTO);
    }

    @Override
    public int getTodayTestLinkVisitCount() {
        return adminMapper.getTodayTestLinkVisitCount();
    }

    @Override
    public int getTodayTestResultClickCount() {
        return adminMapper.getTodayTestResultClickCount();
    }

    @Override
    public int getTodayTestSignUpCount() {
        return adminMapper.getTodayTestSignUpCount();
    }

    @Override
    public Map<String, Integer> getTodayTestMetrics() {
        Map<String, Integer> testMetrics = new HashMap<>();

        int testLinkVisitCount = getTodayTestLinkVisitCount();
        int testResultClickCount = getTodayTestResultClickCount();
        int testSignUpCount = getTodayTestSignUpCount();

        testMetrics.put("testLinkVisitCount", testLinkVisitCount);
        testMetrics.put("testResultClickCount", testResultClickCount);
        testMetrics.put("testSignUpCount", testSignUpCount);

        return testMetrics;
    }

    public Map<String, Integer> getTodayCumulativeMetrics() {
        Map<String, Integer> cumulativeMetrics = new HashMap<>();

        try {
            int todaySignUpCount = getTodaySignUpCount(); // 오늘의 회원가입 수
            int todayWithdrawalCount = getTodayWithdrawalCount(); // 오늘의 탈퇴 수
            int todayVisitCount = getTodayVisitCount(); // 오늘의 방문자 수
            int todayLoginCount = getTodayLoginCount(); // 오늘의 로그인 수

            // Map에 메트릭 추가
            cumulativeMetrics.put("todaySignUpCount", todaySignUpCount);
            cumulativeMetrics.put("todayWithdrawalCount", todayWithdrawalCount);
            cumulativeMetrics.put("todayVisitCount", todayVisitCount);
            cumulativeMetrics.put("todayLoginCount", todayLoginCount);
        } catch (Exception e) {
            logger.error("누적 메트릭 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("누적 메트릭 조회 중 오류 발생", e); // 예외를 던져서 호출 쪽에서 처리하게 함
        }

        return cumulativeMetrics;
    }

    @Override
    public void updateCumulativeSignUpCount() {
        adminMapper.updateCumulativeSignUpCount();
    }

    @Override
    public void updateCumulativeLoginCount() {
        adminMapper.updateCumulativeLoginCount();
    }

    @Override
    public void updateCumulativeVisitCount() {
        adminMapper.updateCumulativeVisitCount();
    }

    @Override
    public void updateCumulativeWithdrawalCount() {
        adminMapper.updateCumulativeWithdrawalCount();
    }

    @Override
    public UserMetricsAggregateDTO getTodayMetrics() {
        return adminMapper.selectTodayMetrics();
    }
}
