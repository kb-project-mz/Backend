package fingertips.backend.consumption.controller;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.service.ConsumptionService;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/consumption")
public class ConsumptionController {

    private final ConsumptionService consumptionService;
    private final OpenAiService openAiService;

    @GetMapping("/card/history/{memberId}")
    public ResponseEntity<List<CardConsumptionDTO>> getCardHistoryListPerMonth(@PathVariable int memberId) {

        List<CardConsumptionDTO> cardHistoryList = consumptionService.getCardHistoryList(memberId);
        return ResponseEntity.ok(cardHistoryList);
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askOpenAi(@RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");
        String response = openAiService.askOpenAi(prompt);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> mostAndMaximumUsed(@RequestBody PeriodDTO period) {

        Map<String, String> response = consumptionService.getMostAndMaximumUsed(period);
        return ResponseEntity.ok(response);
    }
}
