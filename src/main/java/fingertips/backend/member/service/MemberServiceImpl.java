package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.util.JwtProcessor;
import lombok.extern.log4j.Log4j;
import lombok.RequiredArgsConstructor;
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

    public MemberDTO getMemberByUsername(String username) {

        return mapper.getMember(username);
    }

    public void deleteMember(String username) {
        mapper.deleteMember(username);
    }

    public boolean validateMember(String username, String password) {
        MemberDTO memberDTO = getMemberByUsername(username);

        if (memberDTO != null) {
            return passwordEncoder.matches(password, memberDTO.getPassword());
        }

        return false;
    }

    @Override
    public void setRefreshToken(MemberDTO memberDTO) {

        mapper.setRefreshToken(memberDTO);
    }
}
