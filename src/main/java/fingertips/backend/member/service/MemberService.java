package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;
import fingertips.backend.member.util.UploadFile;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    String authenticate(String username, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void setRefreshToken(MemberDTO memberDTO);
    boolean existsMemberId(String memberId);
    boolean checkEmailDuplicate(String email);
    boolean existsMemberName(String memberName);
    ProfileDTO getProfile(String memberId);
    void updateProfile(String memberId, UpdateProfileDTO updateProfile);
    void clearRefreshToken(String memberId);
    String findByNameAndEmail(String memberName, String email);
    void withdrawMember(String memberId);
    void updatePasswordByEmail(PasswordFindDTO passwordFindDTO);
    PasswordFindDTO processFindPassword(String memberName, String email);
    String processVerifyPassword(PasswordFindDTO passwordFindDTO);
}
