package fingertips.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

    private String id;
    private String memberId;
    private String password;
    private String memberName;
    private String socialType;
    private String socialId;
    private String birthday;
    private String gender;
    private String email;
    private String imageUrl;
    private String joinDate;
    private String region;
    private String job;
    private String testStatus;
    private String activeStatus;
    private String refreshToken;
    private String withdrawDate;
    private String role;
    private int loginLocked;
    private long loginLockTime;
}
