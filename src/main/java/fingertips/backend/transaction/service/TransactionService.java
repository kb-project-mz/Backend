package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.*;

import java.util.List;

public interface TransactionService {

    Integer saveTransaction(Integer memberIdx);
    MonthlySummaryDTO getMonthlySummary(Integer memberIdx, String startDate, String endDate);
    TopUsageDTO getTopUsageExpense(Integer memberIdx, String startDate, String endDate);
    String getRecommendation(Integer memberIdx);
    List<CategoryTransactionCountDTO> getCategoryData(Integer memberIdx, String startDate, String endDate);
    List<String> getFixedExpense(Integer memberIdx);
    MonthlyDailyExpenseDTO getMonthlyDailyExpense(Integer memberIdx);
    List<MonthlyExpenseDTO> getMonthlyExpenseSummary(Integer memberIdx);
    List<DailyTransactionDTO> getDailyTransactions(Integer memberIdx, int page, int size);
    long getTotalTransactions(Integer memberIdx);
    List<DailyTransactionSummaryDTO> getMonthlyTransactionSummary(Integer memberIdx, String startDate, String endDate);
}
