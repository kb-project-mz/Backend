package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {

    private Integer id;
    private Integer challengeLimit;
    private Integer cardHistoryCount;
}
