package fingertips.backend.consumption.controller;

import fingertips.backend.consumption.dto.AccountConsumptionDTO;
import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.service.ConsumptionService;
import fingertips.backend.exception.dto.JsonResponse;
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
@RequestMapping("/api/v1/history")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    @GetMapping("/card/{memberId}")
    public ResponseEntity<JsonResponse<List<CardConsumptionDTO>>> getCardHistoryListPerMonth(@PathVariable int memberId) {

        List<CardConsumptionDTO> cardHistoryList = consumptionService.getCardHistoryList(memberId);
        return ResponseEntity.ok().body(JsonResponse.success(cardHistoryList));
    }

    @GetMapping("/account/{memberId}")
    public ResponseEntity<JsonResponse<List<AccountConsumptionDTO>>> getAccountHistoryListPerMonth(@PathVariable int memberId) {

        List<AccountConsumptionDTO> accountHistoryList = consumptionService.getAccountHistoryList(memberId);
        return ResponseEntity.ok().body(JsonResponse.success(accountHistoryList));
    }
  
    @GetMapping("/top-usage")
    public ResponseEntity<JsonResponse<String>> mostAndMaximumUsed(@RequestParam Map<String, String> params) {

        PeriodDTO period = PeriodDTO.builder()
                .memberId(Integer.parseInt(params.get("memberId")))
                .startYear(Integer.parseInt(params.get("startYear")))
                .startMonth(Integer.parseInt(params.get("startMonth")))
                .startDay(Integer.parseInt(params.get("startDay")))
                .endYear(Integer.parseInt(params.get("endYear")))
                .endMonth(Integer.parseInt(params.get("endMonth")))
                .endDay(Integer.parseInt(params.get("endDay")))
                .build();

        String response = consumptionService.getMostAndMaximumUsed(period);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
