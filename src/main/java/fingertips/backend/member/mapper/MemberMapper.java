package fingertips.backend.member.mapper;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    MemberDTO getMemberByMemberId(String memberId);
    void insertMember(MemberDTO memberDTO);
    void updateLockStatus(MemberDTO memberDTO);
    void deleteMember(String username);
    void setRefreshToken(MemberDTO memberDTO);
    int isEmailTaken(String email);
    String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO);
    int existsMemberId(String memberId);
}
