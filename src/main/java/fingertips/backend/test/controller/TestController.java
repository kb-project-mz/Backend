package fingertips.backend.test.controller;


import fingertips.backend.exception.dto.JsonResponse;
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

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestController {

    @Autowired
    private TestService testService;

    // 1. 질문 리스트 불러오기
//    @GetMapping("/questions")
//    public List<TestQuestionDTO> getAllQuestions() {
//        return testService.getAllQuestions();
//    }
    @GetMapping("/questions")
    public ResponseEntity<JsonResponse<List<TestQuestionDTO>>> getAllQuestions() {
        List<TestQuestionDTO> result = testService.getAllQuestions();
        return ResponseEntity.ok().body(JsonResponse.success(result));
    }

    // 2. 특정 질문에 대한 선택지 리스트 불러오기
    @GetMapping("/questions/{questionIdx}/options")
    public ResponseEntity<JsonResponse<List<TestOptionDTO>>> getOptionsByQuestionId(@PathVariable int questionIdx) {
        List<TestOptionDTO> result = testService.getOptionsByQuestionId(questionIdx);
        return ResponseEntity.ok().body(JsonResponse.success(result));
    }


    // 3. 테스트 결과 저장하기
    @PostMapping("/results")
    public ResponseEntity<JsonResponse<String>> saveTestResult(@RequestBody TestResultDTO testResultDTO) {
        testService.saveTestResult(testResultDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Result"));
    }

}
