package fingertips.backend.member.controller;


import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.member.service.EmailService;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Log4j
@RequestMapping("/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);


    @PostMapping("/join")
    public ResponseEntity<JsonResponse<String>> join(@RequestBody MemberDTO memberDTO) {

        memberService.joinMember(memberDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Join Success"));
    }

    @GetMapping("/memberId/{memberName}/{email}")
    public ResponseEntity<JsonResponse<MemberIdFindDTO>> findMemberId(@PathVariable String memberName, @PathVariable String email) {

        String foundMemberId = memberService.findByNameAndEmail(memberName, email);

        MemberIdFindDTO memberIdFindDTO = MemberIdFindDTO.builder()
                .memberId(foundMemberId)
                .memberName(memberName)
                .email(email)
                .build();

        return ResponseEntity.ok(JsonResponse.success(memberIdFindDTO));
    }

    // 비밀번호 찾기
    @GetMapping("/password/{memberName}/{email}")
    public ResponseEntity<JsonResponse<PasswordFindDTO>> findPassword(@PathVariable String memberName, @PathVariable String email) {
        logger.info("비밀번호 찾기 요청이 접수되었습니다. 회원명: {}, 이메일: {}", memberName, email);

        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);

        try {
            memberService.findByNameAndEmail(memberName, email);
            logger.info("회원명과 이메일을 사용하여 회원 정보를 찾았습니다. 회원명: {}, 이메일: {}", memberName, email);
        } catch (Exception e) {
            logger.warn("회원명 또는 이메일로 회원 정보를 찾는 중 오류 발생. 회원명: {}, 이메일: {}", memberName, email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 실패 시 null 응답
        }

        PasswordFindDTO passwordFindDTO = PasswordFindDTO.builder()
                .memberName(memberName)
                .email(decodedEmail)
                .newPassword(null)
                .build();

        logger.info("비밀번호 찾기 프로세스가 완료되었습니다. 회원명: {}, 이메일: {}", memberName, email);
        return ResponseEntity.ok(JsonResponse.success(passwordFindDTO));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<JsonResponse<Boolean>> verifyPassword(@RequestBody PasswordFindDTO passwordFindDTO) {
        try {
            // 1. 이름과 이메일을 이용해 사용자 ID를 조회
            logger.info("사용자 ID 조회 요청: 이름={}, 이메일={}", passwordFindDTO.getMemberName(), passwordFindDTO.getEmail());
            String memberId = memberService.findByNameAndEmail(passwordFindDTO.getMemberName(), passwordFindDTO.getEmail());

            if (memberId == null) {
                logger.warn("사용자를 찾을 수 없습니다: 이름={}, 이메일={}", passwordFindDTO.getMemberName(), passwordFindDTO.getEmail());
                return ResponseEntity.ok(JsonResponse.success(null));  // 사용자를 찾을 수 없을 때 null 반환
            }

            // 2. DB에서 사용자 ID를 통해 사용자 정보를 조회
            logger.info("사용자 정보 조회: memberId={}", memberId);
            MemberDTO member = memberService.getMemberByMemberId(memberId);

            if (member == null) {
                logger.warn("사용자 정보를 찾을 수 없습니다: memberId={}", memberId);
                return ResponseEntity.ok(JsonResponse.success(null));  // 사용자를 찾을 수 없을 때 null 반환
            }

            // 3. 입력된 비밀번호가 DB에 저장된 비밀번호와 일치하는지 확인
            boolean isPasswordMatching = passwordEncoder.matches(passwordFindDTO.getNewPassword(), member.getPassword());

            if (isPasswordMatching) {
                logger.info("비밀번호 일치: memberId={}", memberId);
                return ResponseEntity.ok(JsonResponse.success(true));  // 비밀번호 일치
            } else {
                logger.warn("비밀번호 불일치: memberId={}", memberId);
                return ResponseEntity.ok(JsonResponse.success(null));  // 비밀번호 불일치 시 null 반환
            }
        } catch (Exception e) {
            logger.error("비밀번호 검증 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(JsonResponse.success(null));  // 서버 오류 발생 시 null 반환
        }
    }

    @GetMapping("/memberInfo/{memberId}")
    public ResponseEntity<JsonResponse<MemberDTO>> getMember(@PathVariable String memberId) {

        MemberDTO member = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(member));
    }

    @GetMapping("/check-memberId/{memberId}")
    public ResponseEntity<JsonResponse<Boolean>> checkMemberId(@PathVariable String memberId) {

        boolean exists = memberService.existsMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(exists));
    }

    @GetMapping("/email/duplicate")
    public ResponseEntity<Map<String, Boolean>> checkEmailDuplicate(@RequestParam String email) {
        boolean exists = memberService.checkEmailDuplicate(email);
        System.out.println("이메일 중복 확인 결과 (exists): " + exists);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/memberName/{memberName}")
    public ResponseEntity<JsonResponse<Boolean>> checkMemberName(@PathVariable String memberName) {
        boolean exists = memberService.existsMemberName(memberName);
        return ResponseEntity.ok(JsonResponse.success(exists));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (jwtProcessor.validateToken(refreshToken)) {
            String memberId = jwtProcessor.getMemberId(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(memberId, "ROLE_USER");
            String newRefreshToken = jwtProcessor.generateRefreshToken(memberId);
            memberService.setRefreshToken(MemberDTO.builder().memberId(memberId).refreshToken(newRefreshToken).build());

            return ResponseEntity.ok(AuthDTO.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired. Please login again.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<JsonResponse<String>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String memberId = jwtProcessor.getMemberId(token);
            memberService.clearRefreshToken(memberId);
        }
        return ResponseEntity.ok(JsonResponse.success("Logout successful"));
    }

    /*
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
    */
}
