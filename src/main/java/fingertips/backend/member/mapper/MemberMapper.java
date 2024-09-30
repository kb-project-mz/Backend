package fingertips.backend.member.mapper;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    MemberDTO getMemberByMemberId(String memberId);
    void insertMember(MemberDTO memberDTO);
    void updateLockStatus(MemberDTO memberDTO);
    void deleteMember(String username);
    void setRefreshToken(MemberDTO memberDTO);
    String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO);
    int existsMemberId(String memberId);
    ProfileDTO getProfile(String memberId);
    void updateProfile(UpdateProfileDTO updateProfile);
    String getPassword(String memberId);
    void clearRefreshToken(String memberId);
    void withdrawMember(String memberId);
}
