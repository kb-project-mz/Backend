package fingertips.backend.admin.mapper;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    void insertLoginLog(UserMetricsDTO userMetricsDTO);
    void updateCumulativeSignUpCount();
    void updateCumulativeLoginCount();
    void updateCumulativeVisitCount();
    void updateCumulativeWithdrawalCount();
    int getCumulativeSignUpCount();
    int getCumulativeLoginCount();
    int getCumulativeVisitCount();
    int getCumulativeWithdrawalCount();
    List<UserMetricsDTO> selectDailyMetrics();
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();
    void insertDailyMetrics(UserMetricsDTO metrics);
    void insertUserMetricsAggregate();
    Float getSignUpGrowthPercentage();
    Float getLoginGrowthPercentage();
    Float getVisitGrowthPercentage();
    Float getWithdrawalGrowthPercentage();
    void updateGrowthRates(Map<String, Object> params);
}
