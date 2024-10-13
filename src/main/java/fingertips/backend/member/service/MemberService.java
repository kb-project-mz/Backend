package fingertips.backend.member.service;

import fingertips.backend.member.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {

    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void setRefreshToken(MemberDTO memberDTO);
    boolean existsMemberId(String memberId);
    boolean checkEmailDuplicate(String email);
    boolean existsMemberName(String memberName);

    ProfileDTO getProfile(String memberId);
    UploadFileDTO uploadImage(String memberId, String imageUrl);
    void verifyPassword(String memberId, VerifyPasswordDTO verifyPassword);
    void changePassword(String memberId, NewPasswordDTO newPassword);
    void changeEmail(String memberId, NewEmailDTO newEmail);

    void clearRefreshToken(String memberId);
    MemberIdFindDTO findByNameAndEmail(String memberName, String email);
    void withdrawMember(Integer memberIdx);

    void updatePasswordByEmail(PasswordFindDTO passwordFindDTO);
    PasswordFindDTO processFindPassword(String memberName, String email);
    String processVerifyPassword(PasswordFindDTO passwordFindDTO);
}
