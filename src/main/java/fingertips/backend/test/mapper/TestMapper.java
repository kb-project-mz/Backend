package fingertips.backend.test.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;

@Mapper
public interface TestMapper {

    List<TestQuestionDTO> getAllQuestions();
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);
    void insertTestResult(TestResultDTO testResultDTO);
}

