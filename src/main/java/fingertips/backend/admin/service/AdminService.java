package fingertips.backend.admin.service;


import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface AdminService {
    int getCumulativeSignUpCount();
    int getCumulativeLoginCount();
    int getCumulativeVisitCount();
    int getCumulativeWithdrawalCount();
    void updateCumulativeSignUpCount();
    void updateCumulativeLoginCount();
    void updateCumulativeVisitCount();
    void updateCumulativeWithdrawalCount();
    List<UserMetricsAggregateDTO> getDailyMetrics();
    Map<String, Float> getAllGrowthMetrics();
}