package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;

public interface MemberService {

    String authenticate(String username, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void deleteMember(String username);
    void setRefreshToken(MemberDTO memberDTO);
    boolean existsMemberId(String memberId);

    ProfileDTO getProfile(String memberId);
    void updateProfile(String memberId, UpdateProfileDTO updateProfile);
    void clearRefreshToken(String memberId);
    String findByNameAndEmail(String memberName, String email);
}
