package fingertips.backend.test.service;


import fingertips.backend.test.dto.TestResultDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fingertips.backend.test.dto.TestQuestionDTO;
import fingertips.backend.test.dto.TestOptionDTO;
import fingertips.backend.test.dto.TestTypeDTO;
import fingertips.backend.test.mapper.TestMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j
@RequiredArgsConstructor
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
    public List<TestTypeDTO> getResultsByMemberId(){
        return testMapper.getResultsByMemberId();
    };

    @Transactional
    @Override
    public void saveTestResult(TestResultDTO testResultDTO) {
        testMapper.saveTestResult(testResultDTO);
        testMapper.incrementParticipants(testResultDTO.getTypeIdx());
    }


}
