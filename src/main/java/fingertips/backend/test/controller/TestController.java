package fingertips.backend.test.controller;

import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.test.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/test")
@RestController
@RequiredArgsConstructor
public class TestController {

    private TestService testService;

    @PostMapping("/test-link-click")
    public ResponseEntity<JsonResponse<String>> trackTestLinkClick(@RequestBody UserMetricsDTO userMetricsDTO) {
        TestService.saveTestLinkClick();
        return ResponseEntity.ok().body(JsonResponse.success("Click tracked successfully"));
    }
}
