package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.security.account.dto.LoginDTO;

public interface MemberService {

    String authenticate(String username, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void setRefreshToken(MemberDTO memberDTO);
    boolean existsMemberId(String memberId);
    void clearRefreshToken(String memberId);
    String findByNameAndEmail(String memberName, String email);
    void withdrawMember(String memberId);
    void updatePasswordByEmail(PasswordFindDTO passwordFindDTO);
}
