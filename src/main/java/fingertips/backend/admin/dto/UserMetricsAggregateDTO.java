package fingertips.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricsAggregateDTO {
    private int aggregateIdx;
    private Date metricDate;
    private int totalSignUpCount;
    private int totalWithdrawalCount;
    private int totalVisitCount;
    private int totalLoginCount;
    private int totalTestLinkVisitCount;
    private int totalTestResultClickCount;
    private int totalTestSignUpCount;
}