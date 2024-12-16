package fingertips.backend.transaction.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.transaction.dto.*;
import fingertips.backend.transaction.mapper.TransactionMapper;
import fingertips.backend.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final OpenAiService openAiService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private List<TransactionDTO> fetchTransactionFromDB(Integer memberIdx) {
        return transactionMapper.getTransaction(memberIdx);
    }

    @Override
    public Integer saveTransaction(Integer memberIdx) {

        try {
            List<TransactionDTO> transactions = fetchTransactionFromDB(memberIdx);
            String json = objectMapper.writeValueAsString(transactions);
            redisTemplate.opsForValue().set(String.valueOf(memberIdx), json);
            return transactions.size();
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.REDIS_ERROR);
        }
    }

    private List<TransactionDTO> getTransaction(Integer memberIdx, String startDateString, String endDateString) {

        Object data = redisTemplate.opsForValue().get(String.valueOf(memberIdx));

        try {
            List<TransactionDTO> transactions = objectMapper.readValue(
                    (String) data, new TypeReference<List<TransactionDTO>>() {});
            System.out.println("Transactions size: " + transactions.size());
            LocalDate startDate = LocalDate.parse(startDateString, formatter);
            LocalDate endDate = LocalDate.parse(endDateString, formatter);

            return transactions.stream()
                    .filter(transaction -> {
                        LocalDate transactionDate = LocalDate.parse(transaction.getTransactionDate(), formatter);
                        return (transactionDate.isEqual(startDate) || transactionDate.isAfter(startDate)) &&
                                (transactionDate.isEqual(endDate) || transactionDate.isBefore(endDate));
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.REDIS_ERROR);
        }
    }

    @Override
    public MonthlySummaryDTO getMonthlySummary(Integer memberIdx, String startDateString, String endDateString) {

        Long expense = 0L;
        Long income = 0L;

        LocalDate startDate = LocalDate.parse(startDateString, formatter);
        LocalDate endDate = LocalDate.parse(endDateString, formatter);
        Long daysDifference = ChronoUnit.DAYS.between(startDate, endDate);

        List<TransactionDTO> transactions = getTransaction(memberIdx, startDateString, endDateString);
        for (TransactionDTO transactionDTO : transactions) {
            if (transactionDTO.getTransactionType().equals("출금")) {
                expense += transactionDTO.getAmount();
            } else {
                income += transactionDTO.getAmount();
            }
        }

        return MonthlySummaryDTO.builder()
                .expense(expense)
                .income(income)
                .average(expense / daysDifference)
                .build();
    }

    @Override
    public List<MonthlyExpenseDTO> getMonthlyExpenseSummary(Integer memberIdx) {

        LocalDate today = LocalDate.now();
        LocalDate elevenMonthsAgo = today.minusMonths(11);

        String startDate = String.valueOf(LocalDate.of(elevenMonthsAgo.getYear(), elevenMonthsAgo.getMonth(), 1));
        String endDate = String.valueOf(today);

        List<TransactionDTO> transactions = getTransaction(memberIdx, startDate, endDate).stream()
                .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                .toList();

        Map<YearMonth, Integer> monthlyExpense = new HashMap<>();

        for (TransactionDTO transaction : transactions) {
            LocalDate transactionDate = LocalDate.parse(transaction.getTransactionDate());
            YearMonth yearMonth = YearMonth.from(transactionDate);
            monthlyExpense.merge(yearMonth, transaction.getAmount(), Integer::sum);
        }

        return monthlyExpense.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> MonthlyExpenseDTO.builder()
                        .year(entry.getKey().getYear())
                        .month(entry.getKey().getMonthValue())
                        .totalExpense(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyTransactionDTO> getDailyTransactions(Integer memberIdx, int page, int size) {
        String redisKey = String.valueOf(memberIdx);
        Object data = redisTemplate.opsForValue().get(redisKey);

        if (data == null) {
            log.warn("Redis에 데이터가 없습니다. 사용자 ID: {}", memberIdx);
            return Collections.emptyList(); // Redis에 데이터가 없으면 빈 리스트 반환
        }

        try {
            // Redis에서 데이터를 읽어옴
            List<TransactionDTO> transactions = objectMapper.readValue(
                    (String) data, new TypeReference<List<TransactionDTO>>() {}
            );

            // DailyTransactionDTO로 변환
            List<DailyTransactionDTO> dailyTransactions = transactions.stream()
                    .map(transaction -> {
                        DailyTransactionDTO dto = new DailyTransactionDTO();
                        String transactionDateTime = LocalDate.parse(transaction.getTransactionDate(), formatter)
                                .atTime(LocalTime.parse(transaction.getTransactionTime()))
                                .format(DATETIME_FORMATTER);

                        dto.setTransactionDateTime(transactionDateTime);
                        dto.setTransactionType(transaction.getTransactionType());
                        dto.setTransactionDescription(transaction.getTransactionDescription());
                        dto.setAssetName(transaction.getAssetName());
                        dto.setFormattedAmount(String.format("%,d원", transaction.getAmount()));
                        return dto;
                    })
                    .collect(Collectors.toList());

            // 페이지네이션 처리
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, dailyTransactions.size());

            if (fromIndex > dailyTransactions.size()) {
                return Collections.emptyList(); // 요청된 페이지가 데이터 범위를 벗어난 경우 빈 리스트 반환
            }

            return dailyTransactions.subList(fromIndex, toIndex);

        } catch (Exception e) {
            log.error("getDailyTransactions 처리 중 오류 발생: 사용자 ID: {}", memberIdx, e);
            throw new ApplicationException(ApplicationError.REDIS_ERROR);
        }
    }

    @Override
    public long getTotalTransactions(Integer memberIdx) {
        String redisKey = String.valueOf(memberIdx);
        Object data = redisTemplate.opsForValue().get(redisKey);

        if (data == null) {
            log.warn("Redis에 데이터가 없습니다. 사용자 ID: {}", memberIdx);
            return 0L; // 데이터가 없으면 0 반환
        }

        try {
            // Redis에서 전체 데이터 크기를 계산
            List<TransactionDTO> transactions = objectMapper.readValue(
                    (String) data, new TypeReference<List<TransactionDTO>>() {}
            );
            return transactions.size();

        } catch (Exception e) {
            log.error("getTotalTransactions 처리 중 오류 발생: 사용자 ID: {}", memberIdx, e);
            throw new ApplicationException(ApplicationError.REDIS_ERROR);
        }
    }

    @Override
    public List<CategoryTransactionCountDTO> getCategoryData(Integer memberIdx, String startDate, String endDate) {

        List<TransactionDTO> allTransactions = getTransaction(memberIdx, startDate, endDate);

        List<TransactionDTO> transactions = allTransactions.stream()
                .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                .toList();

        int totalSpentAmount = transactions.stream()
                .mapToInt(TransactionDTO::getAmount)
                .sum();

        Map<String, Integer> categoryTotalSpent = transactions.stream()
                .collect(Collectors.groupingBy(TransactionDTO::getCategoryName,
                        Collectors.summingInt(TransactionDTO::getAmount)));

        return categoryTotalSpent.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(entry -> {
                    String categoryName = entry.getKey();
                    Integer totalSpent = entry.getValue();
                    Double percentage = (double) totalSpent / totalSpentAmount * 100;

                    return new CategoryTransactionCountDTO(categoryName, totalSpent, percentage);
                })
                .collect(Collectors.toList());
    }

    @Override
    public TopUsageDTO getTopUsageExpense(Integer memberIdx, String startDateString, String endDateString) {

        List<TransactionDTO> transactions = getTransaction(memberIdx, startDateString, endDateString).stream()
                .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                .toList();
        String data = formatTransactionAsTable(transactions);

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

        String answer = openAiService.askOpenAi(prompt);
        return formatTopUsageResult(answer);
    }

    private TopUsageDTO formatTopUsageResult(String usageData) {

        String[] parts = usageData.split("], \\[");
        String mostUsageData = parts[0].replace("[", "").replace("]", "");
        String maximumUsageData = parts[1].replace("[", "").replace("]", "");

        Map<String, Integer> mostUsageMap = parseToMap(mostUsageData);
        Map<String, Integer> maximumUsageMap = parseToMap(maximumUsageData);

        return TopUsageDTO.builder()
                .mostUsage(mostUsageMap)
                .maximumUsage(maximumUsageMap)
                .build();
    }

    private Map<String, Integer> parseToMap(String data) {

        Map<String, Integer> resultMap = new HashMap<>();

        if (data.trim().isEmpty()) {
            return resultMap;
        }

        String[] entries = data.split(", ");
        for (String entry : entries) {
            String[] parts = entry.split(":");
            String key = parts[0];
            int value = Integer.parseInt(parts[1]);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    @Override
    public String getRecommendation(Integer memberIdx) {

        String startDate = String.valueOf(LocalDate.now().withDayOfMonth(1));
        String endDate = String.valueOf(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));

        List<TransactionDTO> transactions = getTransaction(memberIdx, startDate, endDate).stream()
                .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                .toList();
        String data = formatTransactionAsTable(transactions);

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

        String startDate = String.valueOf(LocalDate.now().minusMonths(3));
        String endDate = String.valueOf(LocalDate.now());

        List<TransactionDTO> transactionLastThreeMonths = getTransaction(memberIdx, startDate, endDate).stream()
                .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                .toList();

        Map<String, Set<String>> transactionGroupedByDescription = transactionLastThreeMonths.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getTransactionDescription().toUpperCase(),
                        Collectors.mapping(TransactionDTO::getTransactionDate, Collectors.toSet())
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


    public MonthlyDailyExpenseDTO getMonthlyDailyExpense(Integer memberIdx) {

        String lastMonthStartDate = String.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(1));
        String lastMonthEndDate = String.valueOf(LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth()));
        List<TransactionDTO> transactionLastMonth = getTransaction(memberIdx, lastMonthStartDate, lastMonthEndDate);

        String thisMonthStartDate = String.valueOf(LocalDate.now().withDayOfMonth(1));
        String thisMonthEndDate = String.valueOf(LocalDate.now());
        List<TransactionDTO> transactionThisMonth = getTransaction(memberIdx, thisMonthStartDate, thisMonthEndDate);

        Map<String, Integer> lastMonthExpenses = calculateCumulativeDailyExpense(
                transactionLastMonth.stream()
                        .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                        .collect(Collectors.toList()),
                lastMonthStartDate,
                lastMonthEndDate
        );

        Map<String, Integer> thisMonthExpenses = calculateCumulativeDailyExpense(
                transactionThisMonth.stream()
                        .filter(transaction -> "출금".equals(transaction.getTransactionType()))
                        .collect(Collectors.toList()),
                thisMonthStartDate,
                thisMonthEndDate
        );

        return MonthlyDailyExpenseDTO.builder()
                .lastMonth(lastMonthExpenses)
                .thisMonth(thisMonthExpenses)
                .build();
    }

    private Map<String, Integer> calculateCumulativeDailyExpense(List<TransactionDTO> transactions, String startDate, String endDate) {

        Map<String, Integer> dailyExpenses = transactions.stream()
                .collect(Collectors.groupingBy(
                        TransactionDTO::getTransactionDate,
                        Collectors.summingInt(TransactionDTO::getAmount)
                ));

        Map<String, Integer> filledExpenses = fillMissingDates(dailyExpenses, startDate, endDate);

        Map<String, Integer> cumulativeExpenses = new LinkedHashMap<>();
        int cumulativeSum = 0;

        for (String date : filledExpenses.keySet()) {
            cumulativeSum += filledExpenses.get(date);
            cumulativeExpenses.put(date, cumulativeSum);
        }

        return cumulativeExpenses;
    }

    private Map<String, Integer> fillMissingDates(Map<String, Integer> dailyExpenses, String startDate, String endDate) {

        Map<String, Integer> filledExpenses = new LinkedHashMap<>();
        LocalDate currentDate = LocalDate.parse(startDate);
        LocalDate lastDate = LocalDate.parse(endDate);

        int lastAmount = 0;

        while (!currentDate.isAfter(lastDate)) {
            String currentDateStr = currentDate.toString();

            if (dailyExpenses.containsKey(currentDateStr)) {
                lastAmount = dailyExpenses.get(currentDateStr);
            }

            filledExpenses.put(currentDateStr, lastAmount);
            currentDate = currentDate.plusDays(1);
        }

        return filledExpenses;
    }

    private String formatTransactionAsTable(List<TransactionDTO> transactions) {

        StringBuilder table = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        table.append("|----------|-------------------------------------|").append(lineSeparator);
        table.append("|  amount  |             description             |").append(lineSeparator);
        table.append("|----------|-------------------------------------|").append(lineSeparator);

        for (TransactionDTO transaction : transactions) {
            table.append(String.format("| %-8d | %-35s |",
                    transaction.getAmount(), transaction.getTransactionDescription())).append(lineSeparator);
        }

        table.append("|----------|-------------------------------------|").append(lineSeparator);

        return table.toString();
    }

    @Override
    public List<DailyTransactionSummaryDTO> getMonthlyTransactionSummary(Integer memberIdx, String startDate, String endDate) {

        List<TransactionDTO> transactions = getTransaction(memberIdx, startDate, endDate);

        Map<String, Map<String, Integer>> dailyDataMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        TransactionDTO::getTransactionDate,
                        Collectors.groupingBy(
                                TransactionDTO::getTransactionType,
                                Collectors.summingInt(TransactionDTO::getAmount)
                        )
                ));

        List<DailyTransactionSummaryDTO> dailyData = new ArrayList<>();
        for (LocalDate date = LocalDate.parse(startDate);
             !date.isAfter(LocalDate.parse(endDate));
             date = date.plusDays(1)) {

            String dateKey = date.toString();

            Map<String, Integer> transactionTypeMap = dailyDataMap.getOrDefault(dateKey, new HashMap<>());
            int spent = transactionTypeMap.getOrDefault("출금", 0);
            int earned = transactionTypeMap.getOrDefault("입금", 0);

            dailyData.add(DailyTransactionSummaryDTO.builder()
                    .date(dateKey)
                    .expense(spent)
                    .income(earned)
                    .build());
        }

        LocalDate start = LocalDate.parse(startDate);
        return dailyData;
    }
}
