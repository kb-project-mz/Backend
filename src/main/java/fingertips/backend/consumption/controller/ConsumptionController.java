package fingertips.backend.consumption.controller;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.service.ConsumptionService;
import fingertips.backend.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/most")
    public ResponseEntity<Map<String, String>> mostAndMaximumUsed(@RequestParam Map<String, String> params) {

        PeriodDTO period = PeriodDTO.builder()
                .memberId(Integer.parseInt(params.get("memberId")))
                .startYear(Integer.parseInt(params.get("startYear")))
                .startMonth(Integer.parseInt(params.get("startMonth")))
                .startDay(Integer.parseInt(params.get("startDay")))
                .endYear(Integer.parseInt(params.get("endYear")))
                .endMonth(Integer.parseInt(params.get("endMonth")))
                .endDay(Integer.parseInt(params.get("endDay")))
                .build();

        Map<String, String> response = consumptionService.getMostAndMaximumUsed(period);
        return ResponseEntity.ok(response);
    }
}
