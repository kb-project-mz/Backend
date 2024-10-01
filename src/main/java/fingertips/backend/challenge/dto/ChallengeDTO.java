package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTO {

    private Integer challengeIdx;
    private Integer memberIdx;
    private String challengeName;
    private String challengeType;
    private Integer challengeLimit;
    private Integer categoryIdx;
    private String detailedCategory;
    private String challengeStatus;
    private String challengeStartDate;
    private String challengeEndDate;
    private String categoryName;
    private Integer isPublic;
}
