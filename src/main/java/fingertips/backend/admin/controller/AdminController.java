package fingertips.backend.admin.controller;

import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.service.AdminService;
import fingertips.backend.exception.dto.JsonResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/")
    public ResponseEntity<?> getAdminDashboard(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return new ResponseEntity<>("403 Forbidden - 관리자 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Admin Dashboard", HttpStatus.OK);
    }

    @GetMapping("/daily-metrics")
    public ResponseEntity<JsonResponse<List<UserMetricsDTO>>> getAllMetrics() throws IOException {
        List<UserMetricsDTO> allMetrics = adminService.getDailyMetrics();
        return ResponseEntity.ok(JsonResponse.success(allMetrics));
    }

    @GetMapping("/total/sign-up")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeSignUpCount() throws IOException {
        int updatedSignUpCount = adminService.getCumulativeSignUpCount();
        return ResponseEntity.ok().body(JsonResponse.success(updatedSignUpCount));
    }

    @GetMapping("/total/login")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeLoginCount() throws IOException {
        int updatedLoginCount = adminService.getCumulativeLoginCount();
        return ResponseEntity.ok().body(JsonResponse.success(updatedLoginCount));
    }

    @GetMapping("/total/visit")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeVisitCount() throws IOException {
        int updatedVisitCount = adminService.getCumulativeVisitCount();
        return ResponseEntity.ok().body(JsonResponse.success(updatedVisitCount));
    }

    @GetMapping("/total/withdrawal")
    public ResponseEntity<JsonResponse<Integer>> updateCumulativeWithdrawalCount() throws IOException {
        int updatedWithdrawalCount = adminService.getCumulativeWithdrawalCount();
        return ResponseEntity.ok().body(JsonResponse.success(updatedWithdrawalCount));
    }

    @GetMapping("/growth/all")
    public ResponseEntity<JsonResponse<Map<String, Float>>> getAllGrowthMetrics() throws IOException {
        Map<String, Float> growthMetrics = adminService.getAllGrowthMetrics();
        return ResponseEntity.ok().body(JsonResponse.success(growthMetrics));
    }
}

