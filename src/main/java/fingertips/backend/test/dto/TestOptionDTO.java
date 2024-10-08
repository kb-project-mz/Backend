package fingertips.backend.test.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOptionDTO {

    private Integer optionIdx;
    private Integer questionIdx;
    private String optionText;
    private Integer score;
    private Integer typeIdx;
}
