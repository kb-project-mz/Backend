package fingertips.backend.test.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {

    private Integer memberIdx;
    private Integer typeIdx;
    private String typeImage;
    private Integer birthYear;
    private String gender;
    private String region;
    private String occupation;
}
