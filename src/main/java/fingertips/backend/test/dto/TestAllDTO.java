package fingertips.backend.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAllDTO {

    private Integer questionIdx;
    private String questionText;
    private Integer optionIdx;
    private String optionText;
    private Integer score;
    private String type;

}
