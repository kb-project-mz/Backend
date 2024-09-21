package fingertips.backend.member.mapper;

import fingertips.backend.member.dto.MemberDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

    MemberDTO getMember(String username);
    void insertMember(MemberDTO memberDTO);
    void deleteMember(String username);
    void setRefreshToken(MemberDTO memberDTO);
}