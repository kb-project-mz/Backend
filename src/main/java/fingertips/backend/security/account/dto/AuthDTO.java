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
    private String accessToken; // 액세스 토큰
    private String userId;
    private String refreshToken; // 리프레시 토큰 추가
}
