package fingertips.backend.transaction.controller;

import fingertips.backend.transaction.dto.AccountTransactionDTO;
import fingertips.backend.transaction.dto.CardTransactionDTO;
import fingertips.backend.transaction.dto.PeriodDTO;
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

        PeriodDTO period = PeriodDTO.builder()
                .memberIdx(Integer.parseInt(params.get("memberIdx")))
                .startYear(Integer.parseInt(params.get("startYear")))
                .startMonth(Integer.parseInt(params.get("startMonth")))
                .startDay(Integer.parseInt(params.get("startDay")))
                .endYear(Integer.parseInt(params.get("endYear")))
                .endMonth(Integer.parseInt(params.get("endMonth")))
                .endDay(Integer.parseInt(params.get("endDay")))
                .build();

        String response = transactionService.getMostAndMaximumUsed(period);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
