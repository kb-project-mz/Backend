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

    private String memberId;
    private String accessToken;
    private String refreshToken;
    private String role;
}
