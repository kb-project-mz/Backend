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
public class PeerChallengeDTO {
    private String challengeName;
    private String challengeType;
    private int challengeLimit;
    private String detailedCategory;
    private Date challengeStartDate;
    private Date challengeEndDate;
}
