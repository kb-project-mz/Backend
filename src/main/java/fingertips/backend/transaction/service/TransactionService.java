package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.*;

import java.util.List;

public interface TransactionService {

    void saveTransaction(Integer memberIdx);
    MonthlySummaryDTO getMonthlySummary(Integer memberIdx, String startDate, String endDate);
    List<CardTransactionDTO> getCardTransactionList(Integer memberId);
    String getMostAndMaximumUsed(Integer memberIdx, String startDate, String endDate);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberId);
    String getRecommendation(Integer memberIdx);
    List<CategoryTransactionCountDTO> getCategoryData(PeriodDTO periodDTO);
    List<String> getFixedExpense(Integer memberIdx);
}
