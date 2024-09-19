package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.util.JwtProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    public String authenticate(String username, String password) {

        MemberDTO memberDTO = mapper.getMember(username);
        if (memberDTO != null && passwordEncoder.matches(password, memberDTO.getPassword())) {
            String role = memberDTO.getRole(); // Assume roles are stored in memberDTO
            return jwtProcessor.generateAccessToken(username, role);
        }

        throw new RuntimeException("Invalid username or password");
    }

    public void registerUser(MemberDTO memberDTO) {

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);

        mapper.insertMember(memberDTO);
    }

    public MemberDTO getUserByUsername(String username) {

        return mapper.getMember(username);
    }

    public boolean validateUser(String username, String password) {
        MemberDTO memberDTO = getUserByUsername(username);

        if (memberDTO != null) {
            return passwordEncoder.matches(password, memberDTO.getPassword());
        }

        return false;
    }
}
