package fingertips.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetricsDTO {
    private int metricIdx;
    private int guestIdx;
    private int memberIdx;
    private Integer todaySignUpCount;
    private Integer todayWithdrawalCount;
    private Integer todayVisitCount;
    private Integer todayLoginCount;
    private Integer todayTestLinkVisitCount;
    private Integer todayTestResultClickCount;
    private Integer todayTestSignUpCount;
    private String metricDate;
    private String createDate;
    private String userAgent;
    private String ipAddress;
}
