package fingertips.backend.member.sociallogin.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private String memberId;
    private String memberIdx;
    private String memberName;
}
