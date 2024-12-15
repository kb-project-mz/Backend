package fingertips.backend.transaction.controller;

import fingertips.backend.security.util.JwtProcessor;
import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.service.TransactionService;
import fingertips.backend.exception.dto.JsonResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


    @GetMapping("/yearly-expenses")
    public ResponseEntity<JsonResponse<List<MonthlyExpenseDTO>>> getYearlyExpenseSummary(
            @RequestHeader("Authorization") String token,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {

        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);

        List<MonthlyExpenseDTO> yearlyExpenses = transactionService.getYearlyExpenseSummary(memberIdx, startDate, endDate);

        return ResponseEntity.ok(JsonResponse.success(yearlyExpenses));
    }

    @GetMapping("/daily-transactions")
    public ResponseEntity<JsonResponse<List<DailyTransactionDTO>>> getDailyTransactions(
            @RequestHeader("Authorization") String token,
            @RequestParam int page,
            @RequestParam int size
    ) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);

        List<DailyTransactionDTO> dailyTransactions = transactionService.getDailyTransactions(memberIdx, page, size);
        return ResponseEntity.ok(JsonResponse.success(dailyTransactions));
    }



}
