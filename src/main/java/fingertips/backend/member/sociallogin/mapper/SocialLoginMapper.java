package fingertips.backend.member.sociallogin.mapper;

import fingertips.backend.member.sociallogin.dto.SocialLoginDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialLoginMapper {

    int checkMemberExists(String email);
    void insertMember(SocialLoginDTO socialLoginDTO);
    void updateMemberTokens(SocialLoginDTO socialLoginDTO);
    SocialLoginDTO getMemberByGoogleId(String googleId);
    void activateMemberByEmail(String email);
}
