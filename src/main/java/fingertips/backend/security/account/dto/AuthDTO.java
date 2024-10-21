package fingertips.backend.security.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthDTO {

    private Integer memberIdx;
    private String memberId;
    private String memberName;
    private String imageUrl;
    private String accessToken;
    private String refreshToken;
    private String role;
}
