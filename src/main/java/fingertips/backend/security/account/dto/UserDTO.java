package fingertips.backend.security.account.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String username;
    private String userId;
    private String password;
    private String email;
    private List<String> roles; // 권한 목록
    private List<AuthDTO> authList; // 권한 목록
}