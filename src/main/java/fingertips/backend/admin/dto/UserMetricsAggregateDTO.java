package fingertips.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricsAggregateDTO {
    private int aggregateIdx;
    private Date metricDate;
    private Integer totalSignUpCount;
    private Integer totalWithdrawalCount;
    private Integer totalVisitCount;
    private Integer totalLoginCount;
    private Integer totalTestLinkVisitCount;
    private Integer totalTestResultClickCount;
    private Integer totalTestSignUpCount;
}