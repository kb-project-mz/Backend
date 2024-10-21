package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {

    private Integer challengeIdx;
    private Integer challengeLimit;
    private Integer cardHistoryCount;
}
