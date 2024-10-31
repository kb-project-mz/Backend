package fingertips.backend.carddata.controller;
import fingertips.backend.carddata.service.CardDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/carddata")
@RequiredArgsConstructor
public class CardDataController {

    private final CardDataService cardDataService;

    @GetMapping("/generate")
    public ResponseEntity<String> generateCardData() {
        int count = 5;  // 생성할 데이터 개수
        cardDataService.generateAndSaveCardData(count);
        return ResponseEntity.ok("5개의 카드 거래 데이터가 추가되었습니다.");
    }
}
