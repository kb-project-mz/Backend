package fingertips.backend.member.mapper;

import fingertips.backend.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    MemberDTO getMember(String memberId);
    void insertMember(MemberDTO memberDTO);
    void updateMember(MemberDTO memberDTO);
    void deleteMember(String memberId);
    void setRefreshToken(MemberDTO memberDTO);
    MemberDTO findByIdAndEmail(String memberId, String email);
    MemberDTO findByEmail(String email);
    int isEmailTaken(String email);


}
