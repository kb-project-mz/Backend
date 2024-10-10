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
public class UserMetricsDTO {
    private int metricIdx;
    private int memberIdx;
    private Date metricDate;
    private Integer todaySignUpCount;
    private Integer todayWithdrawalCount;
    private Integer todayVisitCount;
    private Integer todayLoginCount;
    private Integer  todayTestLinkVisitCount;
    private Integer  todayTestResultClickCount;
    private Integer  todayTestSignUpCount;
    private String userAgent;
    private String ipAddress;
}
