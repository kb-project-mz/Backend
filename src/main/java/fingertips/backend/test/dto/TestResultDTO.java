package fingertips.backend.test.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {

    private Integer memberIdx;
    private String type;
    private Integer totalScore;
}
