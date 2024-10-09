package fingertips.backend.admin.service;


import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;

import java.util.List;
import java.util.Map;

public interface AdminService {

    void createAdmin();

    int getCumulativeSignUpCount();
    int getCumulativeLoginCount();
    int getCumulativeVisitCount();
    int getCumulativeWithdrawalCount();
    void updateCumulativeSignUpCount();
    void updateCumulativeLoginCount();
    void updateCumulativeVisitCount();
    void updateCumulativeWithdrawalCount();
    List<UserMetricsDTO> getDailyMetrics();
    Map<String, Float> getAllGrowthMetrics();
    void insertDailyMetrics(UserMetricsDTO metrics);
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();
    void insertUserMetricsAggregate(UserMetricsAggregateDTO aggregateMetrics);
}