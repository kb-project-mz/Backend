package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;

public interface MemberService {

    String authenticate(String username, String password);
    void registerUser(MemberDTO memberDTO);
    MemberDTO getUserByUsername(String username);
    public boolean validateUser(String username, String password);
}
