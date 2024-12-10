package fingertips.backend.data.controller;

import fingertips.backend.data.service.DataService;
import fingertips.backend.exception.dto.JsonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/data")
@RequiredArgsConstructor
@Slf4j
public class DataController {

    private final DataService dataService;

    @GetMapping("/generate")
    public ResponseEntity<JsonResponse<String>> generateData() {
        int count = 990;  // 10개의 데이터 생성
        dataService.generateAndSaveTransactions(count);
        return ResponseEntity.ok().body(JsonResponse.success("5개의 데이터가 추가되었습니다."));
    }
}
