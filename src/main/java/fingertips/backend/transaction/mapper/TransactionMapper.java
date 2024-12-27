package fingertips.backend.transaction.mapper;

import fingertips.backend.transaction.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TransactionMapper {

    List<TransactionDTO> getTransaction(Integer memberIdx);
    MonthlySummaryDTO getMonthlySummary(Map<String, Object> params);
    List<MonthlyExpenseDTO> getMonthlyExpenseSummary(Integer memberIdx);
    List<TransactionDTO> getThreeMonthsExpense(Integer memberIdx);
    List<TransactionDTO> getThisMonthExpense(Integer memberIdx);
    List<TransactionDTO> getSelectedTransaction(Map<String, Object> params);
}
