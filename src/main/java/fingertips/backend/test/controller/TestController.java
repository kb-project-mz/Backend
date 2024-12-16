package fingertips.backend.test.controller;


import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.security.util.JwtProcessor;
import fingertips.backend.test.dto.*;
import fingertips.backend.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;


import java.util.*;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final TestService testService;
    private final JwtProcessor jwtProcessor;

    @GetMapping("/questions")
    public ResponseEntity<JsonResponse<List<TestQuestionDTO>>> getAllQuestions() {
        List<TestQuestionDTO> questions = testService.getAllQuestions();
        return ResponseEntity.ok(JsonResponse.success(questions));
    }

    @GetMapping("/options/{questionIdx}")
    public ResponseEntity<JsonResponse<List<TestOptionDTO>>> getOptionsByQuestionId(@PathVariable int questionIdx) {
        List<TestOptionDTO> options = testService.getOptionsByQuestionId(questionIdx);
        return ResponseEntity.ok(JsonResponse.success(options));
    }

    @GetMapping("/types")
    public ResponseEntity<JsonResponse<List<TestTypeDTO>>> getTypeResults() {
        List<TestTypeDTO> results = testService.getTypeResults();
        return ResponseEntity.ok(JsonResponse.success(results));
    }

    @PostMapping("/result")
    public ResponseEntity<JsonResponse<String>> saveTestResult(@RequestBody TestResultDTO testResultDTO) {
        testService.saveTestResult(testResultDTO);
        return ResponseEntity.ok(JsonResponse.success("Result"));
    }

    @GetMapping("/survey")
    public ResponseEntity<JsonResponse<ForSurveyDTO>> getSurveyInfo(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        ForSurveyDTO results = testService.getSurveyInfo(memberId);
        return ResponseEntity.ok(JsonResponse.success(results));
    }

    @GetMapping("/survey/additional-info")
    public ResponseEntity<JsonResponse<ForAdditionalSurveyDTO>> getAdditionalSurveyInfo(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        ForAdditionalSurveyDTO results = testService.getAdditionalSurveyInfo(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(results));
    }
}
