package fingertips.backend.member.controller;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    @GetMapping("/all")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<String> doAll() {
        return ResponseEntity.ok("All can access everybody");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> doAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return ResponseEntity.ok("Admin resource accessed");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
    }

    @GetMapping("/member")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<String> doMember(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails.getUsername());
    }

    @PostMapping("/refresh")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtProcessor.validateToken(refreshToken)) {
            String memberId = jwtProcessor.getMemberId(refreshToken);
            String role = jwtProcessor.getMemberRole(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(memberId, role);
            String newRefreshToken = jwtProcessor.generateRefreshToken(memberId);

            MemberDTO memberDTO = MemberDTO.builder()
                    .id(Integer.parseInt(memberId))
                    .refreshToken(newRefreshToken)
                    .build();

            memberService.setRefreshToken(memberDTO);

            return ResponseEntity.ok(
                    AuthDTO.builder()
                            .memberId(memberId)
                            .accessToken(newAccessToken)
                            .refreshToken(newRefreshToken)
                            .build()
            );
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/join")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> join(@RequestBody MemberDTO memberDTO) {
        try {
            if (memberDTO.getMemberId() == null || memberDTO.getMemberId().isEmpty()) {
                return ResponseEntity.badRequest().body("회원 ID는 필수입니다.");
            }
            if (memberDTO.getPassword() == null || memberDTO.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("비밀번호는 필수입니다.");
            }
            if (memberDTO.getEmail() == null || memberDTO.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("이메일은 필수입니다.");
            }
            if (memberDTO.getMemberName() == null || memberDTO.getMemberName().isEmpty()) {
                return ResponseEntity.badRequest().body("이름은 필수입니다.");
            }

            if (!isValidEmail(memberDTO.getEmail())) {
                return ResponseEntity.badRequest().body("유효한 이메일 형식이 아닙니다.");
            }

            if (memberService.getMemberByMemberId(memberDTO.getMemberId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 아이디입니다.");
            }
            if (memberService.isEmailTaken(memberDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 이메일입니다.");
            }

            memberService.joinMember(memberDTO);

            String accessToken = memberService.authenticate(memberDTO.getMemberId(), memberDTO.getPassword());
            String refreshToken = jwtProcessor.generateRefreshToken(memberDTO.getMemberId());

            memberDTO.setRefreshToken(refreshToken);
            memberService.setRefreshToken(memberDTO);

            return ResponseEntity.ok(AuthDTO.builder()
                    .memberId(memberDTO.getMemberId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    @GetMapping("/check-email")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = memberService.isEmailTaken(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-memberId/{memberId}")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Boolean> checkMemberId(@PathVariable String memberId) {
        boolean exists = memberService.getMemberByMemberId(memberId) != null;
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            if (memberService.validateMember(loginDTO.getMemberId(), loginDTO.getPassword())) {

                MemberDTO memberDTO = memberService.getMemberByMemberId(loginDTO.getMemberId());
                String token = memberService.authenticate(loginDTO.getMemberId(), loginDTO.getPassword());
                String refreshToken = jwtProcessor.generateRefreshToken(loginDTO.getMemberId());

                memberDTO.setRefreshToken(refreshToken);
                memberService.setRefreshToken(memberDTO);

                if (loginDTO.getMemberId().startsWith("admin")) {
                    return ResponseEntity.ok(
                            AuthDTO.builder()
                                    .memberId(loginDTO.getMemberId())
                                    .accessToken(token)
                                    .refreshToken(refreshToken)
                                    .role("ROLE_ADMIN")
                                    .build()
                    );
                } else {
                    return ResponseEntity.ok(
                            AuthDTO.builder()
                                    .memberId(loginDTO.getMemberId())
                                    .accessToken(token)
                                    .refreshToken(refreshToken)
                                    .role("ROLE_USER")
                                    .build()
                    );
                }

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid memberId or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/findMemberId")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<String> findMemberId(@RequestBody LoginDTO loginDTO) {
        try {
            return memberService.findMemberId(loginDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자 아이디를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/logout")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}
