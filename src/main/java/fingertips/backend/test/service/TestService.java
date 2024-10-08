package fingertips.backend.test.service;

import java.util.List;

import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;
import fingertips.backend.test.dto.TestTypeDTO;

public interface TestService {
    List<TestQuestionDTO> getAllQuestions();
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);
    List<TestTypeDTO> getResultsByMemberId();
    void saveTestResult(TestResultDTO testResultDTO);

}
