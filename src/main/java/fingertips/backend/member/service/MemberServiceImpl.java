package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.util.JwtProcessor;
import lombok.extern.log4j.Log4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    public String authenticate(String username, String password) {
        MemberDTO memberDTO = mapper.getMember(username);
        if (memberDTO != null && passwordEncoder.matches(password, memberDTO.getPassword())) {
            return jwtProcessor.generateAccessToken(username, memberDTO.getRole());
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void joinMember(MemberDTO memberDTO) {
        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);
        mapper.insertMember(memberDTO);
    }

    public MemberDTO getMemberByMemberId(String memberId) {

        return mapper.getMember(memberId);
    }

    public void deleteMember(String username) {
        mapper.deleteMember(username);
    }

    public boolean validateMember(String memberId, String password) {
        MemberDTO memberDTO = getMemberByMemberId(memberId);

        if (memberDTO != null) {
            return passwordEncoder.matches(password, memberDTO.getPassword());
        }

        return false;
    }

    public String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO) {

        String memberId = mapper.findByNameAndEmail(memberIdFindDTO);

        if (memberId == null) {
            return "사용자 아이디를 찾을 수 없습니다.";
        }

        return memberId;
    }

    @Override
    public void setRefreshToken(MemberDTO memberDTO) {

        mapper.setRefreshToken(memberDTO);
    }

    @Override
    public boolean isEmailTaken(String email) {

        int isTaken = mapper.isEmailTaken(email);
        return isTaken != 0;
    }
}
