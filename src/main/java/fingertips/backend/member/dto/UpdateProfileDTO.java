package fingertips.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileDTO {
    private String memberId;
    private String password;
    private String newPassword;
    private String email;
    private String imageUrl;
}
