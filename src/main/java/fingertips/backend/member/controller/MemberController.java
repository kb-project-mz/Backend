package fingertips.backend.member.controller;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.security.account.dto.LoginDTO;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j
@RequestMapping("/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    @GetMapping("/all")
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
    public ResponseEntity<String> doMember(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userDetails.getUsername());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtProcessor.validateToken(refreshToken)) {
            String memberId = jwtProcessor.getMemberId(refreshToken);
            String role = jwtProcessor.getUserRole(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(memberId, role);
            String newRefreshToken = jwtProcessor.generateRefreshToken(memberId);

            MemberDTO memberDTO = MemberDTO.builder()
                    .id(memberId)
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
    public ResponseEntity<?> join(@RequestBody MemberDTO memberDTO) {
        try {
            String memberId = memberDTO.getMemberId();
            String password = memberDTO.getPassword();
            String email = memberDTO.getEmail();

            if (memberService.getMemberByMemberId(memberId) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID already in use.");
            }

            if (memberService.isEmailTaken(email)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use.");
            }

            memberService.joinMember(memberDTO);

            String accessToken = memberService.authenticate(memberId, password);
            String refreshToken = jwtProcessor.generateRefreshToken(memberId);

            memberDTO.setRefreshToken(refreshToken);
            memberService.setRefreshToken(memberDTO);

            return ResponseEntity.ok(
                    AuthDTO.builder()
                            .memberId(memberId)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/login")
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
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @GetMapping("/id/{memberName}/{email}")
    public ResponseEntity<String> findMemberId(@PathVariable String memberName, @PathVariable String email) {

        MemberIdFindDTO memberIdFindDTO = MemberIdFindDTO.builder()
                .memberName(memberName)
                .email(email)
                .build();

        String memberId = memberService.findByNameAndEmail(memberIdFindDTO);
        return ResponseEntity.ok(memberId);
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody MemberDTO memberDTO) {
        try {
            if (memberService.getMemberByUsername(memberDTO.getMemberId()) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 사용 중인 아이디입니다.");
            }

            memberService.joinMember(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = memberService.getMemberByUsername(username) != null;
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            if (memberService.validateMember(loginDTO.getMemberId(), loginDTO.getPassword())) {

                MemberDTO memberDTO = memberService.getMemberByUsername(loginDTO.getMemberId());
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
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            log.error("Login failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }
}