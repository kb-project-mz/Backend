package fingertips.backend.admin.mapper;

import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMetricsMapper {
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    List<UserMetricsDTO> getTestResultMetrics();
    List<UserMetricsAggregateDTO> getCumulativeMetrics();
    void insertLoginLog(UserMetricsDTO userMetricsDTO);
    void updateCumulativeSignUpCount(int todaySignUpCount);
    void updateCumulativeLoginCount(int todayLoginCount);
    void updateCumulativeVisitCount(int todayVisitCount);
    void updateCumulativeWithdrawalCount(int todayWithdrawalCount);
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();

}
