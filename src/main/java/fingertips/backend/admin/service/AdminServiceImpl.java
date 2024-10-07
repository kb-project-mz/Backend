package fingertips.backend.admin.service;


import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.mapper.AdminMapper;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberMapper memberMapper;

    public void createAdmin() {
        MemberDTO admin = new MemberDTO();
        admin.setMemberId("admin");
        admin.setPassword(passwordEncoder.encode("1234"));
        admin.setRole("ROLE_ADMIN");
        admin.setMemberName("admin");
        memberMapper.insertAdmin(admin);
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
    public List<UserMetricsAggregateDTO> getDailyMetrics() {
        try {
            return adminMapper.selectDailyMetrics();
        } catch (Exception e) {
            logger.error("일별 메트릭 데이터를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("일별 메트릭 데이터를 가져오는 중 오류 발생", e);
        }
    }

    @Override
    public void updateCumulativeSignUpCount() {
        try {
            adminMapper.updateCumulativeSignUpCount();
        } catch (Exception e) {
            logger.error("회원가입 수 누적 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("회원가입 수 누적 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public int getCumulativeSignUpCount() {
        try {
            return adminMapper.getCumulativeSignUpCount();
        } catch (Exception e) {
            logger.error("회원가입 수 누적 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("회원가입 수 누적 조회 중 오류 발생", e);
        }
    }

    @Override
    public void updateCumulativeLoginCount() {
        try {
            adminMapper.updateCumulativeLoginCount();
        } catch (Exception e) {
            logger.error("로그인 수 누적 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("로그인 수 누적 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public int getCumulativeLoginCount() {
        try {
            return adminMapper.getCumulativeLoginCount();
        } catch (Exception e) {
            logger.error("로그인 수 누적 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("로그인 수 누적 조회 중 오류 발생", e);
        }
    }

    @Override
    public void updateCumulativeVisitCount() {
        try {
            adminMapper.updateCumulativeVisitCount();
        } catch (Exception e) {
            logger.error("방문자 수 누적 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("방문자 수 누적 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public int getCumulativeVisitCount() {
        try {
            return adminMapper.getCumulativeVisitCount();
        } catch (Exception e) {
            logger.error("방문자 수 누적 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("방문자 수 누적 조회 중 오류 발생", e);
        }
    }

    @Override
    public void updateCumulativeWithdrawalCount() {
        try {
            adminMapper.updateCumulativeWithdrawalCount();
        } catch (Exception e) {
            logger.error("탈퇴 수 누적 업데이트 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("탈퇴 수 누적 업데이트 중 오류 발생", e);
        }
    }

    @Override
    public int getCumulativeWithdrawalCount() {
        try {
            return adminMapper.getCumulativeWithdrawalCount();
        } catch (Exception e) {
            logger.error("탈퇴 수 누적 조회 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("탈퇴 수 누적 조회 중 오류 발생", e);
        }
    }

    @Override
    public Map<String, Float> getAllGrowthMetrics() {
        Map<String, Float> growthMetrics = new HashMap<>();
        try {
            growthMetrics.put("signUpGrowth", adminMapper.getSignUpGrowthPercentage());
            growthMetrics.put("loginGrowth", adminMapper.getLoginGrowthPercentage());
            growthMetrics.put("visitGrowth", adminMapper.getVisitGrowthPercentage());
            growthMetrics.put("withdrawalGrowth", adminMapper.getWithdrawalGrowthPercentage());
        } catch (Exception e) {
            logger.error("증가율 데이터를 가져오는 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("증가율 데이터를 가져오는 중 오류 발생", e);
        }
        return growthMetrics;
    }

    @Override
    public void insertDailyMetrics(UserMetricsDTO metrics) {
        adminMapper.insertDailyMetrics(metrics);
    }

    @Override
    public int getTodaySignUpCount() {
        return adminMapper.getTodaySignUpCount();
    }

    @Override
    public int getTodayLoginCount() {
        return adminMapper.getTodayLoginCount();
    }

    @Override
    public int getTodayVisitCount() {
        return adminMapper.getTodayVisitCount();
    }

    @Override
    public int getTodayWithdrawalCount() {
        return adminMapper.getTodayWithdrawalCount();
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
}
