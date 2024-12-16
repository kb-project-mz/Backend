package fingertips.backend.transaction.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlySummaryDTO {

    private Long expense;
    private Long income;
    private Long average;
}
