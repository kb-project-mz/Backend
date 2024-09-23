package fingertips.backend.challenge.dto;

import lombok.Data;

@Data
public class ChallengeDto
{
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
