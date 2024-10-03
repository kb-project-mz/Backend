package fingertips.backend.test.mapper;

import fingertips.backend.admin.dto.UserMetricsDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    void insertTestLinkClick(UserMetricsDTO userMetricsDTO);
}

