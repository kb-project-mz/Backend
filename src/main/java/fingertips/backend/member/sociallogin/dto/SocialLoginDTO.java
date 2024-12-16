package fingertips.backend.member.sociallogin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLoginDTO {
    private Integer memberIdx;
    private String memberId;
    private String password;
    private String email;
    private String memberName;
    private String birthday;
    private String gender;
    private String googleId;
    private String googleAccessToken;
    private String googleIdToken;
    private String googleRefreshToken;
    private int expiresIn;
    private String socialType;
    private Integer isActive;
}
