package fingertips.backend.transaction.controller;

import fingertips.backend.security.util.JwtProcessor;
import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.service.TransactionService;
import fingertips.backend.exception.dto.JsonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final JwtProcessor jwtProcessor;

    @PostMapping
    public ResponseEntity<JsonResponse<Integer>> saveTransaction(@RequestHeader("Authorization") String token) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        Integer response = transactionService.saveTransaction(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(response));
    }

    @GetMapping("/summary")
    public ResponseEntity<JsonResponse<MonthlySummaryDTO>> getMonthlySummary(@RequestHeader("Authorization") String token,
                                                                             @RequestParam String startDate, @RequestParam String endDate) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        MonthlySummaryDTO response = transactionService.getMonthlySummary(memberIdx, startDate, endDate);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/fixed")
    public ResponseEntity<JsonResponse<List<String>>> getFixedExpense(@RequestHeader("Authorization") String token) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<String> response = transactionService.getFixedExpense(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/top-usage")
    public ResponseEntity<JsonResponse<TopUsageDTO>> getTopUsageExpense(@RequestHeader("Authorization") String token,
                                                                   @RequestParam String startDate, @RequestParam String endDate) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        TopUsageDTO response = transactionService.getTopUsageExpense(memberIdx, startDate, endDate);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/recommendation")
    public ResponseEntity<JsonResponse<String>> getRecommendation(@RequestHeader("Authorization") String token) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        String response = transactionService.getRecommendation(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/category")
    public ResponseEntity<JsonResponse<List<CategoryTransactionCountDTO>>> getCategoryData(@RequestHeader("Authorization") String token,
                                                                                           @RequestParam String startDate, @RequestParam String endDate) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<CategoryTransactionCountDTO> transactionCounts = transactionService.getCategoryData(memberIdx, startDate, endDate);
        return ResponseEntity.ok().body(JsonResponse.success(transactionCounts));
    }

    @GetMapping("/daily")
    public ResponseEntity<JsonResponse<MonthlyDailyExpenseDTO>> get(@RequestHeader("Authorization") String token) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        MonthlyDailyExpenseDTO response = transactionService.getMonthlyDailyExpense(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/monthly")
    public ResponseEntity<JsonResponse<List<MonthlyExpenseDTO>>> getYearlyExpenseSummary(@RequestHeader("Authorization") String token) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);

        List<MonthlyExpenseDTO> yearlyExpenses = transactionService.getMonthlyExpenseSummary(memberIdx);

        return ResponseEntity.ok(JsonResponse.success(yearlyExpenses));
    }

    @GetMapping("/daily-transactions")
    public ResponseEntity<JsonResponse<Map<String, Object>>> getDailyTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam int page,
            @RequestParam int size
    ) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("Invalid page or size parameter.");
        }
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);

        // Service 호출
        List<DailyTransactionDTO> transactions = transactionService.getDailyTransactions(memberIdx, page, size);

        // 총 데이터 수 및 페이지 수 계산
        long totalElements = transactionService.getTotalTransactions(memberIdx); // 전체 데이터 개수 가져오기
        int totalPages = (int) Math.ceil((double) totalElements / size);


        Map<String, Object> response = new HashMap<>();
        response.put("data", transactions);
        response.put("totalElements", totalElements);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok(JsonResponse.success(response));
    }

    @GetMapping("/monthly/summary")
    public ResponseEntity<JsonResponse<List<DailyTransactionSummaryDTO>>> getMonthlyTransactionSummary(@RequestHeader("Authorization") String token,
                                                                                                   @RequestParam String startDate, @RequestParam String endDate) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<DailyTransactionSummaryDTO> response = transactionService.getMonthlyTransactionSummary(memberIdx, startDate, endDate);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
