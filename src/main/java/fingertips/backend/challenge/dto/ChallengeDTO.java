package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeDTO
{

    private Integer challengeIdx;
    private String categoryName; // private Integer challenge.category; => private String category.category_name
    private Integer memberIdx;
    private String challengeName;
    private String challengeType;
    private Integer challengeLimit;
    private String detailedCategory;
    private String challengeStartDate;
    private String challengeEndDate;
}
