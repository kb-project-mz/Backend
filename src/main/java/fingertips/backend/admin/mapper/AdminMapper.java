package fingertips.backend.admin.mapper;

import fingertips.backend.admin.dto.UserMetricsAggregateDTO;
import fingertips.backend.admin.dto.UserMetricsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AdminMapper {
    int getTodaySignUpCount();
    int getTodayLoginCount();
    int getTodayVisitCount();
    int getTodayWithdrawalCount();
    void insertLoginLog(UserMetricsDTO userMetricsDTO);
    void updateCumulativeSignUpCount();
    void updateCumulativeLoginCount();
    void updateCumulativeVisitCount();
    void updateCumulativeWithdrawalCount();
    int getTodayTestLinkVisitCount();
    int getTodayTestResultClickCount();
    int getTodayTestSignUpCount();
    UserMetricsAggregateDTO selectTodayMetrics();

}
