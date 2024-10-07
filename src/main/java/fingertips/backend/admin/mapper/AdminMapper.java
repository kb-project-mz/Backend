package fingertips.backend.admin.mapper;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
    List<UserMetricsAggregateDTO> selectDailyMetrics();
    int getYesterdayCumulativeSignUpCount();
    float getSignUpGrowthPercentage();
    int getYesterdayCumulativeLoginCount();
    float getLoginGrowthPercentage();
    int getYesterdayCumulativeVisitCount();
    float getVisitGrowthPercentage();
    int getYesterdayCumulativeWithdrawalCount();
    float getWithdrawalGrowthPercentage();
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();
    void insertDailyMetrics(UserMetricsDTO metrics);
}
