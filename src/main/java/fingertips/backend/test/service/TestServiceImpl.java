package fingertips.backend.test.service;


import fingertips.backend.test.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public List<TestTypeDTO> getTypeResults(){
        return testMapper.getTypeResults();
    };

    @Transactional
    @Override
    public void saveTestResult(TestResultDTO testResultDTO) {
        testMapper.saveTestResult(testResultDTO);
        testMapper.incrementParticipants(testResultDTO.getTypeIdx());
    }

    @Override
    public ForSurveyDTO getSurveyInfo(String memberId) {
        return testMapper.getSurveyInfo(memberId);
    }

    @Override
    public ForAdditionalSurveyDTO getAdditionalSurveyInfo(int memberIdx) {
        return testMapper.getAdditionalSurveyInfo(memberIdx);
    }


}
