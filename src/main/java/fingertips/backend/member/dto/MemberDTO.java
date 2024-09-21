package fingertips.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
    private Integer id;
    private String memberId;
    private String password;
    private String memberName;
    private String socialType;
    private String socialId;
    private LocalDateTime birthday;
    private String gender;
    private String email;
    private String imageUrl;
    private LocalDateTime joinDate;
    private String region;
    private String job;
    private Integer testStatus;
    private Integer activeStatus;
    private String refreshToken;
    private LocalDateTime withdrawDate;
    private String role;
    private boolean loginLocked;
    private long loginLockTime;
    private boolean terms;
}
