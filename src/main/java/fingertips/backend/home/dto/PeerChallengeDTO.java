package fingertips.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeerChallengeDTO {

    private String challengeName;
    private String challengeType;
    private int challengeLimit;
    private String detailedCategory;
    private String challengeStartDate;
    private String challengeEndDate;
}
