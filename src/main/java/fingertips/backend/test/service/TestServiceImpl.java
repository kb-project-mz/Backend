package fingertips.backend.test.service;

import fingertips.backend.admin.dto.UserMetricsDTO;
import fingertips.backend.test.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    public void saveTestLinkClick(UserMetricsDTO userMetricsDTO) {
        testMapper.insertTestLinkClick(userMetricsDTO);
    }
}
