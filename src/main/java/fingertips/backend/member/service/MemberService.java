package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;

public interface MemberService {

    String authenticate(String username, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void deleteMember(String username);
    void setRefreshToken(MemberDTO memberDTO);
    String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO);
    boolean isEmailTaken(String email);
    boolean existsMemberId(String memberId);

    MemberDTO getMemberInfo(String memberId);
    void updateMemberInfo(String memberId, MemberDTO memberDTO);
}
