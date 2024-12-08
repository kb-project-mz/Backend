package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.*;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    void saveTransaction(Integer memberIdx);
    MonthlySummaryDTO getMonthlySummary(Integer memberIdx, String startDate, String endDate);
    List<CardTransactionDTO> getCardTransactionList(Integer memberId);
    List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period);
    String getMostAndMaximumUsed(PeriodDTO period);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberId);
    String getAiRecommendation(PeriodDTO periodDTO);
    List<CategoryTransactionCountDTO> getCategoryData(PeriodDTO periodDTO);
    List<String> getFixedExpense(Integer memberIdx);
    List<CardTransactionDTO> getCardTransactionLastFourMonths(Integer memberIdx);
}
