package fingertips.backend.consumption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDTO {

    private Integer memberIdx;
    private Integer startYear;
    private Integer startMonth;
    private Integer startDay;
    private Integer endYear;
    private Integer endMonth;
    private Integer endDay;
}
