package fingertips.backend.member.sociallogin.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class TokenDto {
    public TokenDto(String accessToken, String refreshToken, String memberId, String memberName) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
        this.memberName = memberName;
    }
    private String accessToken;
    private String refreshToken;
    private String memberId;
    private String memberName;
}
