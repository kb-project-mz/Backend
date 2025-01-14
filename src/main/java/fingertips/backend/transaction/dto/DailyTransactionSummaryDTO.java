package fingertips.backend.transaction.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTransactionSummaryDTO {

    private String date;
    private Integer income;
    private Integer expense;
}
