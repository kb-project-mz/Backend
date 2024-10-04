package fingertips.backend.admin.service;


import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();
    void updateCumulativeSignUpCount();
    void updateCumulativeLoginCount();
    void updateCumulativeVisitCount();
    void updateCumulativeWithdrawalCount();
    UserMetricsAggregateDTO getTodayMetrics();
    Map<String, Integer> getTodayTestMetrics();
;
}