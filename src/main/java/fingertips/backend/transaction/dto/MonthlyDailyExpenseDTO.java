package fingertips.backend.transaction.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyDailyExpenseDTO {

    private Map<String, Integer> lastMonth;
    private Map<String, Integer> thisMonth;
}
