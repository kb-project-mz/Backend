package fingertips.backend.test.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {

    private int memberIdx;
    private int typeIdx;
    private int totalScore;
}
