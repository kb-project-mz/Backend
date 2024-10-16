package fingertips.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeChallengeDTO {

    private Integer challengeIdx;
    private Integer challengeLimit;
    private String challengeName;
    private Integer cardHistoryCount;
}
