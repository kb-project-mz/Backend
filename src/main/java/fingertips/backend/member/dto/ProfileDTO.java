package fingertips.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDTO {

    private String memberId;
    private String memberName;
    private String socialType;
    private String birthday;
    private String gender;
    private String email;
    private String imageUrl;
}
