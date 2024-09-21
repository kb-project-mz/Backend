package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    String authenticate(String memberId, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByMemberId(String memberId);
    void deleteMember(String memberId);
    boolean validateMember(String memberId, String password);
    void setRefreshToken(MemberDTO memberDTO);
    ResponseEntity<String> findMemberId(LoginDTO loginDTO);
    boolean isEmailTaken(String email);


}
