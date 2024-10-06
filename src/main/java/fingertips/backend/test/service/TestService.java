package fingertips.backend.test.service;

import java.util.List;

import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;

public interface TestService {
    List<TestQuestionDTO> getAllQuestions();
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);
    void saveTestResult(TestResultDTO testResultDTO);

}
