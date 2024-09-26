package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Log4j
@RequestMapping("/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;

    @PostMapping("/join")
    public ResponseEntity<JsonResponse<String>> join(@RequestBody MemberDTO memberDTO) {

        memberService.joinMember(memberDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Join Success"));
    }

    @GetMapping("/id/{memberName}/{email}")
    public ResponseEntity<JsonResponse<String>> findMemberId(@PathVariable String memberName, @PathVariable String email) {

        MemberIdFindDTO memberIdFindDTO = MemberIdFindDTO.builder()
                .memberName(memberName)
                .email(email)
                .build();

        String memberId = memberService.findByNameAndEmail(memberIdFindDTO);
        return ResponseEntity.ok(JsonResponse.success(memberId));
    }

    @GetMapping("/check-memberId/{memberId}")
    public ResponseEntity<JsonResponse<Boolean>> checkMemberId(@PathVariable String memberId) {

        boolean exists = memberService.existsMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(exists));
    }

    @GetMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<JsonResponse<MemberDTO>> getMember(@PathVariable String memberId) {

        MemberDTO member = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(member));
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody String refreshToken) {
        if (jwtProcessor.validateToken(refreshToken)) {
            String memberId = jwtProcessor.getMemberId(refreshToken);
            String role = jwtProcessor.getUserRole(refreshToken);
            String newAccessToken = jwtProcessor.generateAccessToken(memberId, role);
            String newRefreshToken = jwtProcessor.generateRefreshToken(memberId);

            MemberDTO memberDTO = MemberDTO.builder()
                    .memberId(memberId)
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
    */
    // 멤버 정보 가져오기
    @GetMapping("/info")
    public ResponseEntity<JsonResponse<MemberDTO>> getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // JWT에서 추출된 memberId (또는 username)
        log.info("memberId: " + memberId);
        MemberDTO info = memberService.getMemberInfo(memberId);
        return ResponseEntity.ok(JsonResponse.success(info));
    }

    @PostMapping("/update/info")
    public ResponseEntity<JsonResponse<MemberDTO>> updateMemberInfo(@RequestBody MemberDTO mypageInfo) {
        // 로그인된 사용자 정보를 SecurityContext에서 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName(); // 일반적으로 사용자의 username 또는 memberId가 저장됨
        memberService.updateMemberInfo(memberId ,mypageInfo);
        MemberDTO updatedInfo = memberService.getMemberInfo(memberId);
        log.info(updatedInfo.getEmail());
        return ResponseEntity.ok(JsonResponse.success(updatedInfo));
    }

}
