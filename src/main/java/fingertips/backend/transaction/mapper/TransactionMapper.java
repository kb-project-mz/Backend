package fingertips.backend.transaction.mapper;

import fingertips.backend.transaction.dto.AccountTransactionDTO;
import fingertips.backend.transaction.dto.CardTransactionDTO;
import fingertips.backend.transaction.dto.CategoryTransactionCountDTO;
import fingertips.backend.transaction.dto.PeriodDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {

    List<CardTransactionDTO> getCardTransactionList(Integer memberIdx);
    List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period);
    List<AccountTransactionDTO> getAccountTransactionList(Integer memberIdx);
    List<CategoryTransactionCountDTO> getCategoryTransactionCount(Integer memberIdx);
}
