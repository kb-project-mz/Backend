package fingertips.backend.member.sociallogin.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
    private String memberId;
    private String memberName;

    public TokenDTO(String accessToken, String refreshToken, String memberId, String memberName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
        this.memberName = memberName;
    }
}
