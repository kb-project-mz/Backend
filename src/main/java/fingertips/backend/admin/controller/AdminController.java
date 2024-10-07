package fingertips.backend.admin.controller;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.service.AdminService;
import fingertips.backend.exception.dto.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @PostMapping("/create")
    public String createAdmin() {
        adminService.createAdmin();
        return "Admin created successfully";
    }

    // 일별 전체 통계를 반환하는 API
    @GetMapping("/daily-metrics")
    public ResponseEntity<JsonResponse<List<UserMetricsAggregateDTO>>> getAllMetrics() throws IOException {
        logger.info("getAllMetrics API 호출됨");

        List<UserMetricsAggregateDTO> allMetrics = adminService.getDailyMetrics();
        logger.debug("디버깅 - 가져온 일별 메트릭 데이터: {}", allMetrics);
        return ResponseEntity.ok(JsonResponse.success(allMetrics));
    }

    // 누적 회원가입 수
    @GetMapping("/total/sign-up")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeSignUpCount() throws IOException {
        logger.info("updateCumulativeSignUpCount API 호출됨");

        int updatedSignUpCount = adminService.getCumulativeSignUpCount();
        logger.debug("디버깅 - 회원가입 수 누적 업데이트 완료: {}", updatedSignUpCount);
        return ResponseEntity.ok().body(JsonResponse.success(updatedSignUpCount));
    }

    // 누적 로그인 수
    @GetMapping("/total/login")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeLoginCount() throws IOException {
        logger.info("updateCumulativeLoginCount API 호출됨");

        int updatedLoginCount = adminService.getCumulativeLoginCount();
        logger.debug("디버깅 - 로그인 수 누적 업데이트 완료: {}", updatedLoginCount);
        return ResponseEntity.ok().body(JsonResponse.success(updatedLoginCount));
    }

    // 누적 방문자 수
    @GetMapping("/total/visit")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeVisitCount() throws IOException {
        logger.info("updateCumulativeVisitCount API 호출됨");

        int updatedVisitCount = adminService.getCumulativeVisitCount();
        logger.debug("디버깅 - 방문자 수 누적 업데이트 완료: {}", updatedVisitCount);
        return ResponseEntity.ok().body(JsonResponse.success(updatedVisitCount));
    }

    // 누적 탈퇴 수
    @GetMapping("/total/withdrawal")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeWithdrawalCount() throws IOException {
        logger.info("updateCumulativeWithdrawalCount API 호출됨");

        int updatedWithdrawalCount = adminService.getCumulativeWithdrawalCount();
        logger.debug("디버깅 - 탈퇴 수 누적 업데이트 완료: {}", updatedWithdrawalCount);
        return ResponseEntity.ok().body(JsonResponse.success(updatedWithdrawalCount));
    }

    // 증가율 데이터
    @GetMapping("/growth/all")
    public ResponseEntity<JsonResponse<Map<String, Float>>> getAllGrowthMetrics() throws IOException {
        logger.info("getAllGrowthMetrics API 호출됨");

        Map<String, Float> growthMetrics = adminService.getAllGrowthMetrics();
        logger.debug("디버깅 - 모든 증가율 계산 완료: {}", growthMetrics);
        return ResponseEntity.ok().body(JsonResponse.success(growthMetrics));
    }
}

