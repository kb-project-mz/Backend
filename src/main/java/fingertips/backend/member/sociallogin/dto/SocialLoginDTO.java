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
    private String memberId;
    private String memberName;
    private String password;
    private String email;
    private String birthday;
    private String gender;
    private String socialType;
    private String googleId;
    private String googleAccessToken;
    private String googleRefreshToken;
}
