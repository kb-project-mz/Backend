package fingertips.backend.test.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;
import fingertips.backend.test.mapper.TestMapper;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<TestQuestionDTO> getAllQuestions() {
        return testMapper.getAllQuestions();
    }

    @Override
    public List<TestOptionDTO> getOptionsByQuestionId(int questionIdx) {
        return testMapper.getOptionsByQuestionId(questionIdx);
    }

    @Override
    public void saveTestResult(TestResultDTO testResultDTO) {
        testMapper.insertTestResult(testResultDTO);
    }


}
