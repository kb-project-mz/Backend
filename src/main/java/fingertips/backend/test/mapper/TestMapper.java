package fingertips.backend.test.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestResultDTO;

@Mapper
public interface TestMapper {

    // 질문 리스트를 가져오는 메서드
    List<TestQuestionDTO> getAllQuestions();

    // 특정 질문에 대한 선택지 리스트를 가져오는 메서드
    List<TestOptionDTO> getOptionsByQuestionId(int questionIdx);

    // 테스트 결과를 저장하는 메서드
    void insertTestResult(TestResultDTO testResultDTO);

    void insertTestLinkClick(UserMetricsDTO userMetricsDTO);
}

