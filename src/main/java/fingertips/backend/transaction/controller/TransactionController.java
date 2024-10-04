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

    // 새로운 카테고리별 거래 건수를 반환하는 API
    @GetMapping("/category-count/{memberIdx}")
    public ResponseEntity<JsonResponse<List<CategoryTransactionCountDTO>>> getCategoryTransactionCount(@PathVariable int memberIdx) {
        List<CategoryTransactionCountDTO> transactionCounts = transactionService.getCategoryTransactionCount(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(transactionCounts));
    }



    // 금액 기준으로 가장 많이 지출한 카테고리를 반환하는 엔드포인트
    @GetMapping("/most-spent-category/{memberIdx}")
    public ResponseEntity<JsonResponse<List<MostSpentCategoryDTO>>> getMostSpentCategoryByAmount(@PathVariable int memberIdx) {
        logger.info("getMostSpentCategoryByAmount 메서드가 호출되었습니다. 입력된 memberIdx: {}", memberIdx);

        // TransactionService에서 가장 많이 지출한 카테고리 정보를 가져오기 전에 로그를 찍습니다.
        logger.info("가장 많이 지출한 카테고리 정보를 가져옵니다.");
        List<MostSpentCategoryDTO> mostSpentCategories = transactionService.getMostSpentCategoryByAmount(memberIdx);

        // 데이터를 성공적으로 가져온 후 해당 데이터를 로그로 출력합니다.
        logger.info("가져온 mostSpentCategories 데이터: {}", mostSpentCategories);

        // 정상적으로 데이터를 반환하는지 확인하는 로그입니다.
        logger.info("가장 많이 지출한 카테고리 정보를 성공적으로 반환합니다.");
        return ResponseEntity.ok(JsonResponse.success(mostSpentCategories));
    }

    private PeriodDTO makePeriodDTO(@RequestParam Map<String, String> params) {
        logger.info("makePeriodDTO 메서드가 호출되었습니다. 입력된 파라미터: {}", params);

        // 파라미터에서 각 값을 추출할 때마다 로그를 찍어 오류 발생 시 어느 부분에서 문제가 생겼는지 확인할 수 있습니다.
        int memberIdx = Integer.parseInt(params.get("memberIdx"));
        logger.info("memberIdx 값: {}", memberIdx);

        int startYear = Integer.parseInt(params.get("startYear"));
        int startMonth = Integer.parseInt(params.get("startMonth"));
        int startDay = Integer.parseInt(params.get("startDay"));
        logger.info("시작 날짜 정보 - 연도: {}, 월: {}, 일: {}", startYear, startMonth, startDay);

        int endYear = Integer.parseInt(params.get("endYear"));
        int endMonth = Integer.parseInt(params.get("endMonth"));
        int endDay = Integer.parseInt(params.get("endDay"));
        logger.info("종료 날짜 정보 - 연도: {}, 월: {}, 일: {}", endYear, endMonth, endDay);

        // PeriodDTO를 만들고 나서 해당 DTO 객체를 로그로 확인합니다.
        PeriodDTO periodDTO = PeriodDTO.builder()
                .memberIdx(memberIdx)
                .startYear(startYear)
                .startMonth(startMonth)
                .startDay(startDay)
                .endYear(endYear)
                .endMonth(endMonth)
                .endDay(endDay)
                .build();

        logger.info("생성된 PeriodDTO 객체: {}", periodDTO);

        // PeriodDTO가 정상적으로 반환되었는지 로그로 확인합니다.
        logger.info("PeriodDTO 객체를 성공적으로 반환합니다.");
        return periodDTO;
    }
}
