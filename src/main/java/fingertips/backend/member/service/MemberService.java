package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

public interface MemberService {

    String authenticate(String username, String password);
    void joinMember(MemberDTO memberDTO);
    MemberDTO getMemberByUsername(String username);
    void deleteMember(String username);
    boolean validateMember(String username, String password);
    void setRefreshToken(MemberDTO memberDTO);
    ResponseEntity<String> findMemberId(LoginDTO loginDTO);
}
