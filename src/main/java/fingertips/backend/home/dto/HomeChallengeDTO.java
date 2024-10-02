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
    private int memberIdx;
    private String challengeName;
    private String challengeType;
    private Date challengeStartDate;
    private Date challengeEndDate;
}
