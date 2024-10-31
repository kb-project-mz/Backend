package fingertips.backend.carddata.controller;

import fingertips.backend.carddata.service.CardDataService;
import fingertips.backend.exception.dto.JsonResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carddata")
@RequiredArgsConstructor
@Slf4j
public class CardDataController {

    private final CardDataService cardDataService;

    @GetMapping("/generate")
    public ResponseEntity<JsonResponse<String>> generateCardData() {
        int count = 1000;
        cardDataService.generateAndSaveCardData(count);
        return ResponseEntity.ok(JsonResponse.success("1000개의 데이터가 추가되었습니다."));
    }
}

