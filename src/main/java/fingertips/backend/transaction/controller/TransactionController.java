package fingertips.backend.transaction.controller;

import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.service.TransactionService;
import fingertips.backend.exception.dto.JsonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    // 새로운 카테고리별 거래 건수를 반환하는 API
    @GetMapping("/category-count/{memberIdx}")
    public ResponseEntity<JsonResponse<List<CategoryTransactionCountDTO>>> getCategoryTransactionCount(@PathVariable int memberIdx) {
        List<CategoryTransactionCountDTO> transactionCounts = transactionService.getCategoryTransactionCount(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(transactionCounts));
    }



    // 금액 기준으로 가장 많이 지출한 카테고리를 반환하는 엔드포인트
    @GetMapping("/most-spent-category/{memberIdx}")
    public ResponseEntity<JsonResponse<List<MostSpentCategoryDTO>>> getMostSpentCategoryByAmount(@PathVariable int memberIdx) {
        List<MostSpentCategoryDTO> mostSpentCategories = transactionService.getMostSpentCategoryByAmount(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(mostSpentCategories));
    }

    private PeriodDTO makePeriodDTO(@RequestParam Map<String, String> params) {

        return PeriodDTO.builder()
                .memberIdx(Integer.parseInt(params.get("memberIdx")))
                .startYear(Integer.parseInt(params.get("startYear")))
                .startMonth(Integer.parseInt(params.get("startMonth")))
                .startDay(Integer.parseInt(params.get("startDay")))
                .endYear(Integer.parseInt(params.get("endYear")))
                .endMonth(Integer.parseInt(params.get("endMonth")))
                .endDay(Integer.parseInt(params.get("endDay")))
                .build();
    }
}
