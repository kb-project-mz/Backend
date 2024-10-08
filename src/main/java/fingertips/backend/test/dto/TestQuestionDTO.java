package fingertips.backend.test.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionDTO {

    private Integer questionIdx;
    private String questionText;
}
