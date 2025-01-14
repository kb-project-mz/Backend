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

    private Integer year;
    private Integer month;
    private Integer totalExpense;
}
