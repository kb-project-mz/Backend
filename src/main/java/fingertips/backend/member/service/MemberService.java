package fingertips.backend.member.service;

import fingertips.backend.member.dto.*;

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
    void verifyPassword(String memberId, VerifyPasswordDTO verifyPassword);
    void changePassword(String memberId, NewPasswordDTO newPassword);
}
