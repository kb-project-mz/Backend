package fingertips.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyExpenseDTO {
    private Integer month; // 월 (1~12)
    private Long totalExpense; // 해당 월의 총 지출액
}
