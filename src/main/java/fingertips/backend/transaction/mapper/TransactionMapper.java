package fingertips.backend.transaction.mapper;

import fingertips.backend.transaction.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransactionMapper {

    List<CardTransactionDTO> getCardTransactionList(Integer memberIdx);
    List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberIdx);
    List<CategoryTransactionCountDTO> getCategoryTransactionCount(Integer memberIdx);
    List<MostSpentCategoryDTO> getMostSpentCategoryByAmount(int memberIdx);
    List<CardTransactionDTO> getCardTransactionLastFourMonths(Integer memberIdx);
}
