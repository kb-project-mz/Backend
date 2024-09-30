package fingertips.backend.test.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.*;
import fingertips.backend.test.dto.TestQuestionDTO;


import java.util.List;

@Mapper
public class TestMapper {
    List<TestQuestionDTO> getAllQuestions();

}
