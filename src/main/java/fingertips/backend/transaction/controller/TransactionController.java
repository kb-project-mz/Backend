package fingertips.backend.transaction.controller;

import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.service.TransactionService;
import fingertips.backend.exception.dto.JsonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @GetMapping("/card/{memberIdx}")
    public ResponseEntity<JsonResponse<List<CardTransactionDTO>>> getCardTransactionListPerMonth(@PathVariable int memberIdx) {

        List<CardTransactionDTO> cardTransactionList = transactionService.getCardTransactionList(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(cardTransactionList));
    }

    @GetMapping("/account/{memberIdx}")
    public ResponseEntity<JsonResponse<List<AccountTransactionDTO>>> getAccountTransactionListPerMonth(@PathVariable int memberIdx) {

        List<AccountTransactionDTO> accountTransactionList = transactionService.getAccountTransactionList(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(accountTransactionList));
    }
  
    @GetMapping("/top-usage")
    public ResponseEntity<JsonResponse<String>> mostAndMaximumUsed(@RequestParam Map<String, String> params) {

        PeriodDTO period = makePeriodDTO(params);

        String response = transactionService.getMostAndMaximumUsed(period);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/recommendation")
    public ResponseEntity<JsonResponse<String>> getRecommendation(@RequestParam Map<String, String> params) {

        PeriodDTO period = makePeriodDTO(params);

        String response = transactionService.getAiRecommendation(period);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    // 설정한 기간 내에서 카테고리별 사용 금액 내림차순으로 반환
    @GetMapping("/category")
    public ResponseEntity<JsonResponse<List<CategoryTransactionCountDTO>>> getCategoryData(@RequestParam Map<String, String> params) {

        PeriodDTO period = makePeriodDTO(params);
        List<CategoryTransactionCountDTO> transactionCounts = transactionService.getCategoryData(period);
        return ResponseEntity.ok().body(JsonResponse.success(transactionCounts));
    }

    @GetMapping("/fixed/{memberIdx}")
    public ResponseEntity<JsonResponse<List<String>>> getFixedExpense(@PathVariable Integer memberIdx) {

        List<String> response = transactionService.getFixedExpense(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    private PeriodDTO makePeriodDTO(@RequestParam Map<String, String> params) {

        Integer memberIdx = Integer.parseInt(params.get("memberIdx"));

        Integer startYear = Integer.parseInt(params.get("startYear"));
        Integer startMonth = Integer.parseInt(params.get("startMonth"));
        Integer startDay = Integer.parseInt(params.get("startDay"));

        Integer endYear = Integer.parseInt(params.get("endYear"));
        Integer endMonth = Integer.parseInt(params.get("endMonth"));
        Integer endDay = Integer.parseInt(params.get("endDay"));

        PeriodDTO periodDTO = PeriodDTO.builder()
                .memberIdx(memberIdx)
                .startYear(startYear)
                .startMonth(startMonth)
                .startDay(startDay)
                .endYear(endYear)
                .endMonth(endMonth)
                .endDay(endDay)
                .build();

        return periodDTO;
    }
}
