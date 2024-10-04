package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.*;

import java.util.List;
import java.util.Map;

public interface TransactionService {

    List<CardTransactionDTO> getCardTransactionList(Integer memberId);
    List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period);
    String getMostAndMaximumUsed(PeriodDTO period);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberId);
    String getAiRecommendation(PeriodDTO periodDTO);
    List<CategoryTransactionCountDTO> getCategoryTransactionCount(int memberIdx);
    List<MostSpentCategoryDTO> getMostSpentCategoryByAmount(int memberIdx);

}
