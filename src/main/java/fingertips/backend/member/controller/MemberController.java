package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.ErrorResponse;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    public ResponseEntity<JsonResponse<String>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String memberId = jwtProcessor.getMemberId(token);
            memberService.clearRefreshToken(memberId);
        }
        return ResponseEntity.ok(JsonResponse.success("Logout successful"));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<JsonResponse<MemberDTO>> getMember(@PathVariable String memberId) {

        MemberDTO member = memberService.getMemberByMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(member));
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
