package fingertips.backend.transaction.service;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.mapper.TransactionMapper;
import fingertips.backend.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final OpenAiService openAiService;

    @Override
    public List<CardTransactionDTO> getCardTransactionList(Integer memberId) {
        return transactionMapper.getCardTransactionList(memberId);
    }

    @Override
    public List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period) {
        return transactionMapper.getCardTransactionListByPeriod(period);
    }

    @Override
    public List<CategoryTransactionCountDTO> getCategoryData(PeriodDTO periodDTO) {
        List<CategoryTransactionCountDTO> categoryTransactionCounts = transactionMapper.getCategoryData(periodDTO);
        int totalTransactions = categoryTransactionCounts.stream()
                .mapToInt(CategoryTransactionCountDTO::getTotalSpent)
                .sum();

        categoryTransactionCounts.forEach(transaction -> {
            double percentage = (double) transaction.getTotalSpent()/ totalTransactions * 100;
            transaction.setPercentage(percentage);
        });

        return categoryTransactionCounts;
    }

    @Override
    public List<AccountTransactionDTO> getAccountTransactionList(Integer memberId) {
        return transactionMapper.getAccountTransactionList(memberId);
    }

    @Override
    public String getMostAndMaximumUsed(PeriodDTO period) {

        List<CardTransactionDTO> cardTransactionListByPeriod = getCardTransactionListByPeriod(period);
        String data = formatConsumptionListAsTable(cardTransactionListByPeriod);

        String prompt = data.concat("이 테이블의 cardTransactionDescription 컬럼은 돈을 쓴 사용처야. " +
                "동일한 브랜드에 속하는 상호명을 인식하여 하나의 브랜드로 통일해줘. " +
                "예를 들어, '스타벅스 대화역점'과 '스타벅스 어린이대공원'은 '스타벅스'로 인식하고 통일해. " +
                "공백, 특수문자, 지점명(예: 'OO점', '역', '어린이대공원', '_34') 같은 불필요한 부분은 제거하고, 핵심 브랜드명만 추출해줘. " +
                "예를 들어, '충칭 마라탕'과 '충칭마라탕'은 동일한 브랜드로 인식해. " +
                "단, '마라탕', '치킨', '커피'와 같은 공통 단어를 기준으로 통합하지 말고, 브랜드명에서 일관된 부분만 통일해줘." +
                "두 가지 질문에 대해 정확하게 대답해. " +
                "1. 동일한 브랜드로 방문한 사용처를 테이블에서 찾아 정확히 카운트해서 방문 횟수 기준으로 내림차순으로 3개를 추출해주고," +
                "결과를 확실하게 검토한 후 각 브랜드의 방문 횟수를 적어줘. " +
                "2. 브랜드가 동일한 상호명을 그룹화하고, 각 브랜드별로 총 금액을 테이블에서 찾아 정확히 계산해서 총 금액 내림차순으로 3개를 추출해주고, " +
                "결과를 확실하게 검토한 후 각 브랜드에서 사용한 총액을 적어줘. " +
                "대답할 때 다른 말 붙이지 말고 '[지점명:방문 횟수, 지점명:방문 횟수, 지점명:방문 횟수], [지점명:총 금액, 지점명:총 금액, 지점명:총 금액]'" +
                "이렇게 리스트로만 대답하고 방문 횟수 내림차순, 총 금액 내림차순으로 정렬해서 대답해. 무조건 위 형식대로 대답해. 방문 횟수 총 금액 이런거 텍스트 넣지 말고" +
                "지점명과 방문 횟수를 딱 숫자만 넣어. " +
                "1. [지점명 ~] 한 줄띄고 2. [지점명~] 이딴 식으로 대답하지 말고 한 줄로 처리해");

        return openAiService.askOpenAi(prompt);
    }

    @Override
    public String getAiRecommendation(PeriodDTO period) {

        List<CardTransactionDTO> cardTransactionListByPeriod = getCardTransactionListByPeriod(period);
        String data = formatConsumptionListAsTable(cardTransactionListByPeriod);

        String prompt = data.concat("이 테이블의 cardTransactionDescription 컬럼은 한 달 동안 돈을 쓴 사용처야. " +
                "이 소비 내역을 보고 이 사람이 다음 달에 어떤 식으로 소비를 하면 얼마나 소비를 줄일 수 있을 지 간단하게 " +
                "이번 달에 어떤 곳에 소비를 많이 했네요. 다음 달에 이런 식으로, 여기에서 소비를 줄이면 얼마를 절약할 수 있을 것 같아요. " +
                "다음 달에는 이렇게 하면 어떨까요? 이런 식으로 세 줄로 간단하게 조언 한 개만 해줘. 꼭 Description 뿐만 아니라 " +
                "특정 카테고리에 너무 많이 사용하고 있거나 하는 등의 소비 패턴을 분석해서 조언해줘.");

        return openAiService.askOpenAi(prompt);
    }

    @Override
    public List<String> getFixedExpense(Integer memberIdx) {

        List<String> recurringExpense = new ArrayList<>();

        List<CardTransactionDTO> transactionLastThreeMonths = getCardTransactionLastFourMonths(memberIdx);
        Map<String, Set<String>> transactionGroupedByDescription = transactionLastThreeMonths.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getCardTransactionDescription().toUpperCase(),
                        Collectors.mapping(CardTransactionDTO::getCardTransactionDate, Collectors.toSet())
                ));


        for (Map.Entry<String, Set<String>> transaction : transactionGroupedByDescription.entrySet()) {
            if (transaction.getValue().size() >= 3) {
                List<LocalDate> sortedDates = transaction.getValue().stream()
                        .map(LocalDate::parse).sorted().toList();

                boolean allWithinRange = true;

                for (int i = 1; i < sortedDates.size(); i++) {
                    long daysBetween = ChronoUnit.DAYS.between(sortedDates.get(i - 1), sortedDates.get(i));
                    if (daysBetween < 27 || daysBetween > 33) {
                        allWithinRange = false;
                        break;
                    }
                }

                if (allWithinRange) {
                    recurringExpense.add(transaction.getKey());
                }
            }
        }

        return recurringExpense;
    }

    @Override
    public List<CardTransactionDTO> getCardTransactionLastFourMonths(Integer memberIdx) {
        return transactionMapper.getCardTransactionLastFourMonths(memberIdx);
    }

    public String formatConsumptionListAsTable(List<CardTransactionDTO> cardConsumption) {

        StringBuilder table = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        table.append("|----------|-------------------------------------|").append(lineSeparator);
        table.append("|  amount  |               content               |").append(lineSeparator);
        table.append("|----------|-------------------------------------|").append(lineSeparator);

        for (CardTransactionDTO transaction : cardConsumption) {
            table.append(String.format("| %-8d | %-35s |",
                    transaction.getAmount(), transaction.getCardTransactionDescription())).append(lineSeparator);
        }

        table.append("|----------|-------------------------------------|").append(lineSeparator);

        return table.toString();
    }
}
