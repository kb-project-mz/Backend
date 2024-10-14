package fingertips.backend.member.mapper;

import fingertips.backend.member.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

@Mapper
public interface MemberMapper {

    MemberDTO getMemberByMemberId(String memberId);
    void insertMember(MemberDTO memberDTO);
    void updateLockStatus(MemberDTO memberDTO);
    void setRefreshToken(MemberDTO memberDTO);
    String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO);
    int existsMemberId(String memberId);
    ProfileDTO getProfile(String memberId);
    String getPassword(String memberId);
    void clearRefreshToken(String memberId);
    void withdrawMember(Integer memberIdx);
    void updatePasswordByEmail(PasswordFindDTO passwordFindDTO);
    int checkEmailDuplicate(String email);
    int existsMemberName(String memberName);
    void saveNewPassword(NewPasswordDTO newPassword);
    void saveNewImage(UploadFileDTO uploadFile);
    void saveNewEmail(NewEmailDTO newEmail);
    void insertAdmin(MemberDTO admin);
    String findInactiveMemberByNameAndEmail(MemberIdFindDTO memberIdFindDTO);
}
