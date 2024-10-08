package fingertips.backend.test.controller;


import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.test.dto.TestResultDTO;
import fingertips.backend.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestTypeDTO;


import java.util.*;

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

    // 2. 특정 질문에 대한 선택지 리스트 불러@GetMapping("/options/{questionIdx}")오기
    @GetMapping("/options/{questionIdx}")
    public ResponseEntity<JsonResponse<List<TestOptionDTO>>> getOptionsByQuestionId(@PathVariable int questionIdx) {
        List<TestOptionDTO> options = testService.getOptionsByQuestionId(questionIdx);
        return ResponseEntity.ok(JsonResponse.success(options));
    }

    // 3. 테스트 유형 불러오기
    @GetMapping("/result")
    public ResponseEntity<JsonResponse<List<TestTypeDTO>>> getResultsByMemberId() {
        List<TestTypeDTO> results = testService.getResultsByMemberId();
        return ResponseEntity.ok(JsonResponse.success(results));
    }


    // 3. 테스트 결과 저장하기
    @PostMapping("/saveResult")
    public ResponseEntity<JsonResponse<String>> saveTestResult(@RequestBody TestResultDTO testResultDTO) {
        System.out.println("Received data: " + testResultDTO);
        testService.saveTestResult(testResultDTO);
        return ResponseEntity.ok(JsonResponse.success("Result"));
    }

    // !!!!!!!!!!!!!!!!!!지우 보시오 !!!!!!!!!!
//    @GetMapping("/result}")
//    public ResponseEntity<JsonResponse<List<TestResultDTO>>> getResultsByMemberId(int memberIdx) {
//        List<TestResultDTO> results = testService.getResultsByMemberId(memberIdx);
//        return ResponseEntity.ok(JsonResponse.success(results));
//    }


}
