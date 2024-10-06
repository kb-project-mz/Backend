package fingertips.backend.test.controller;


import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.test.dto.TestAllDTO;
import fingertips.backend.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/questions")
    public ResponseEntity<JsonResponse<List<TestQuestionDTO>>> getAllQuestions() {
        List<TestQuestionDTO> questions = testService.getAllQuestions();
        return ResponseEntity.ok(JsonResponse.success(questions));
    }

    // 2. 특정 질문에 대한 선택지 리스트 불러오기
    @GetMapping("/options/{questionIdx}")
    public ResponseEntity<JsonResponse<List<TestOptionDTO>>> getOptionsByQuestionId(@PathVariable int questionIdx) {
        List<TestOptionDTO> options = testService.getOptionsByQuestionId(questionIdx);
        return ResponseEntity.ok(JsonResponse.success(options));
    }

    @GetMapping("/questions/options")
    public ResponseEntity<JsonResponse<List<TestAllDTO>>> getOptionsByQuestionId() {
        log.info("000000000000000000000000");
        List<TestAllDTO> questionOptions = testService.getAllQustionOptions();
        log.info("1111111111111111111111111");
        return ResponseEntity.ok(JsonResponse.success(questionOptions));
    }

    // 3. 테스트 결과 저장하기
    @PostMapping("/results")
    public ResponseEntity<JsonResponse<String>> saveTestResult(@RequestBody TestResultDTO testResultDTO) {
        testService.saveTestResult(testResultDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Result"));
    }

}
