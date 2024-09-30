package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTO {

    private Integer id;
    private Integer category;
    private Integer memberId;
    private String challengeName;
    private Integer challengeType;
    private Integer challengeLimit;
    private String detailedCategory;
    private String startDate;
    private String endDate;
}
