package fingertips.backend.admin.controller;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.service.AdminService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
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

    // 오늘의 회원가입 수를 가져오는 API
    @GetMapping("/today/sign-up")
    public ResponseEntity<JsonResponse<Integer>> getTodaySignUpCount(HttpServletResponse response) throws IOException {
        logger.info("getTodaySignUpCount API 호출됨");
        try {
            int signUpCount = adminService.getTodaySignUpCount();
            logger.info("오늘의 회원가입 수 성공적으로 조회됨: {}", signUpCount);
            return ResponseEntity.ok().body(JsonResponse.success(signUpCount));
        } catch (Exception e) {
            logger.error("오늘의 회원가입 수 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // 오늘의 로그인 수를 가져오는 API
    @GetMapping("/today/login")
    public ResponseEntity<JsonResponse<Integer>> getTodayLoginCount(HttpServletResponse response) throws IOException {
        logger.info("getTodayLoginCount API 호출됨");
        try {
            int loginCount = adminService.getTodayLoginCount();
            logger.info("오늘의 로그인 수 성공적으로 조회됨: {}", loginCount);
            return ResponseEntity.ok().body(JsonResponse.success(loginCount));
        } catch (Exception e) {
            logger.error("오늘의 로그인 수 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // 오늘의 방문자 수를 가져오는 API
    @GetMapping("/today/visit")
    public ResponseEntity<JsonResponse<Integer>> getTodayVisitCount(HttpServletResponse response) throws IOException {
        logger.info("getTodayVisitCount API 호출됨");
        try {
            int visitCount = adminService.getTodayVisitCount();
            logger.info("오늘의 방문자 수 성공적으로 조회됨: {}", visitCount);
            return ResponseEntity.ok().body(JsonResponse.success(visitCount));
        } catch (Exception e) {
            logger.error("오늘의 방문자 수 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // 오늘의 탈퇴 수를 가져오는 API
    @GetMapping("/today/withdrawal")
    public ResponseEntity<JsonResponse<Integer>> getTodayWithdrawalCount(HttpServletResponse response) throws IOException {
        logger.info("getTodayWithdrawalCount API 호출됨");
        try {
            int withdrawalCount = adminService.getTodayWithdrawalCount();
            logger.info("오늘의 탈퇴 수 성공적으로 조회됨: {}", withdrawalCount);
            return ResponseEntity.ok().body(JsonResponse.success(withdrawalCount));
        } catch (Exception e) {
            logger.error("오늘의 탈퇴 수 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    // 막대 그래프 데이터를 가져오는 API
    @GetMapping("/test-results")
    public ResponseEntity<JsonResponse<Map<String, Integer>>> getTestResultMetrics(HttpServletResponse response) throws IOException {
        logger.info("getTestResultMetrics API 호출됨");
        try {
            // 세 가지 테스트 메트릭 데이터를 조회
            Map<String, Integer> testResultMetrics = adminService.getTodayTestMetrics();
            logger.info("테스트 결과 지표 데이터 성공적으로 조회됨: {}", testResultMetrics);

            // 데이터를 JsonResponse로 성공적으로 반환
            return ResponseEntity.ok().body(JsonResponse.success(testResultMetrics));
        } catch (Exception e) {
            logger.error("테스트 결과 지표 데이터 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null; // 이미 응답이 전송되었으므로 null 반환
        }
    }

    // 누적 데이터를 가져오는 API
    @GetMapping("/total")
    public ResponseEntity<JsonResponse<List<UserMetricsAggregateDTO>>> getCumulativeMetrics(HttpServletResponse response) throws IOException {
        logger.info("getCumulativeMetrics API 호출됨");
        try {
            List<UserMetricsAggregateDTO> cumulativeMetrics = adminService.getCumulativeMetrics();

            logger.info("누적 지표 데이터 성공적으로 조회됨: {}", cumulativeMetrics);
            return ResponseEntity.ok().body(JsonResponse.success(cumulativeMetrics));
        } catch (RuntimeException e) {
            logger.error("누적 지표 데이터 조회 중 오류 발생: {}", e.getMessage(), e);
            JsonResponse.sendError(response, ApplicationError.INTERNAL_SERVER_ERROR);
            return null;
        }
    }
}

