package fingertips.backend.test.mapper;


import fingertips.backend.test.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {

    List<TestQuestionDTO> getAllQuestions();
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);
    List<TestTypeDTO> getTypeResults();
    void saveTestResult(TestResultDTO testResultDTO);
    void incrementParticipants(int typeIdx);
    ForSurveyDTO getSurveyInfo(String memberId);
    ForAdditionalSurveyDTO getAdditionalSurveyInfo(int memberIdx);
}

