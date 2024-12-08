package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.*;

import java.util.List;

public interface TransactionService {

    void saveTransaction(Integer memberIdx);
    MonthlySummaryDTO getMonthlySummary(Integer memberIdx, String startDate, String endDate);
    String getMostAndMaximumUsed(Integer memberIdx, String startDate, String endDate);
    String getRecommendation(Integer memberIdx);
    List<CategoryTransactionCountDTO> getCategoryData(Integer memberIdx, String startDate, String endDate);
    List<String> getFixedExpense(Integer memberIdx);
}
