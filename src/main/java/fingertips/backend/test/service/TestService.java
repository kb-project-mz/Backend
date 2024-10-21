package fingertips.backend.test.service;

import java.util.List;

import fingertips.backend.test.dto.*;

public interface TestService {

    List<TestQuestionDTO> getAllQuestions();
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);
    List<TestTypeDTO> getTypeResults();
    void saveTestResult(TestResultDTO testResultDTO);
    ForSurveyDTO getSurveyInfo(String memberId);
    ForAdditionalSurveyDTO getAdditionalSurveyInfo(int memberIdx);
}
