package fingertips.backend.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDTO {
    private String memberId;
    private String password;
    private String memberName;
    private LocalDate birthday;
    private String gender;
    private String email;
    private Integer isActive;
    private LocalDate joinDate;
    private String role;
    private Integer isLoginLocked;
    private Integer loginLockTime;
}
