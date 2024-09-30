package fingertips.backend.test.dto;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestOptionDTO {

    private Integer optionIdx;
    private Integer questionIdx;
    private String optionText;
    private Integer score;
    private String type;
}
