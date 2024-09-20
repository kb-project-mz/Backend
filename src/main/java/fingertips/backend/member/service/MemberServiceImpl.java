package fingertips.backend.member.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;
    private List<String> allowedDomains = Arrays.asList("gmail.com", "naver.com", "daum.net");

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

    public LoginDTO getMemberByUsername(String username) {
        MemberDTO memberDTO = mapper.getMember(username);
        if (memberDTO != null) {
            return LoginDTO.builder()
                    .memberId(memberDTO.getMemberId())
                    .password(memberDTO.getPassword())
                    .accessToken(jwtProcessor.generateAccessToken(memberDTO.getMemberId(), memberDTO.getRole()))
                    .build();
        }
        return null;
    }

    public void updateMember(MemberDTO memberDTO) {
        mapper.updateMember(memberDTO);
    }

    public void deleteMember(String username) {
        mapper.deleteMember(username);
    }

    public boolean validateMember(String username, String password) {
        LoginDTO loginDTO = getMemberByUsername(username);
        if (loginDTO != null) {
            return passwordEncoder.matches(password, loginDTO.getPassword());
        }
        return false;
    }

    public ResponseEntity<String> findMemberId(LoginDTO loginDTO) {
        try {
            if (loginDTO.getMemberId() == null || loginDTO.getEmail() == null ||
                    loginDTO.getMemberId().isEmpty() || loginDTO.getEmail().isEmpty()) {
                throw new IllegalArgumentException("아이디와 이메일은 필수입니다.");
            }

            validateEmail(loginDTO.getEmail());

            String domain = loginDTO.getEmail().substring(loginDTO.getEmail().indexOf("@") + 1);
            if (!allowedDomains.contains(domain) && !domain.equals("직접입력")) {
                log.warn("지원하지 않는 이메일 도메인: {}");
                throw new IllegalArgumentException("지원하지 않는 이메일 도메인입니다.");
            }

            MemberDTO memberDTO = mapper.findByIdAndEmail(loginDTO.getMemberId(), loginDTO.getEmail());
            if (memberDTO == null) {
                log.error("사용자 아이디를 찾을 수 없습니다. 입력값: {}");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 아이디를 찾을 수 없습니다.");
            }

            log.info("사용자 아이디 찾기 성공: {}");
            return ResponseEntity.ok(memberDTO.getMemberId());
        } catch (IllegalArgumentException e) {
            log.error("아이디 찾기 실패: {}");
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    private void validateEmail(String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }
}
