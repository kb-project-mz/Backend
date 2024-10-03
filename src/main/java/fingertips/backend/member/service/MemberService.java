package fingertips.backend.member.service;

import fingertips.backend.member.dto.*;
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
    UploadFileDTO uploadImage(String memberId, String imageUrl);
    //void updateProfile(String memberId, UpdateProfileDTO updateProfile);
    void verifyPassword(String memberId, VerifyPasswordDTO verifyPassword);
    void changePassword(String memberId, NewPasswordDTO newPassword);

    void clearRefreshToken(String memberId);
    String findByNameAndEmail(String memberName, String email);
    void withdrawMember(String memberId);
    void updatePasswordByEmail(PasswordFindDTO passwordFindDTO);
    PasswordFindDTO processFindPassword(String memberName, String email);
    String processVerifyPassword(PasswordFindDTO passwordFindDTO);


}
