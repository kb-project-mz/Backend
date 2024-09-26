package fingertips.backend.challenge.dto;

import lombok.Data;

@Data
public class ProgressDTO {
    private Integer id;
    private Integer challengeLimit;
    private Integer cardHistoryCount;
}
