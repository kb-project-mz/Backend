package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.*;
import fingertips.backend.member.service.S3uploaderService;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.member.service.MemberService;

import fingertips.backend.member.util.UploadFile;

import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final S3uploaderService s3uploaderService;

    @PostMapping("/join")
    public ResponseEntity<JsonResponse<String>> join(@RequestBody MemberDTO memberDTO) {

        memberService.joinMember(memberDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Join Success"));
    }

    @GetMapping("/memberId/{memberName}/{email}")
    public ResponseEntity<JsonResponse<MemberIdFindDTO>> findMemberId(@PathVariable String memberName, @PathVariable String email) {

            MemberIdFindDTO foundMemberId = memberService.findByNameAndEmail(memberName, email);

            MemberIdFindDTO memberIdFindDTO = MemberIdFindDTO.builder()
                    .memberId(foundMemberId.getMemberId())
                    .memberName(memberName)
                    .email(email)
                    .isActive(foundMemberId.getIsActive())
                    .build();

            return ResponseEntity.ok(JsonResponse.success(memberIdFindDTO));
    }

    @GetMapping("/password/{memberName}/{email}")
    public ResponseEntity<JsonResponse<PasswordFindDTO>> findPassword(
            @PathVariable String memberName,
            @PathVariable String email) {

        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);

        PasswordFindDTO passwordFindDTO = memberService.processFindPassword(memberName, decodedEmail);

        return ResponseEntity.ok(JsonResponse.success(passwordFindDTO));
    }

    @PostMapping("/password/verify-password")
    public ResponseEntity<JsonResponse<String>> verifyPassword(@RequestBody PasswordFindDTO passwordFindDTO) {

        String memberId = memberService.processVerifyPassword(passwordFindDTO);

        return ResponseEntity.ok(JsonResponse.success(memberId));
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
    @GetMapping("/info")
    public ResponseEntity<JsonResponse<ProfileDTO>> getMemberInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        String memberId = authentication.getName();
        ProfileDTO profile = memberService.getProfile(memberId);
        return ResponseEntity.ok(JsonResponse.success(profile));
    }

    @PostMapping("/verification/password")
    public ResponseEntity<JsonResponse<String>> verifyPassword(@RequestBody VerifyPasswordDTO verifyPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        memberService.verifyPassword(memberId, verifyPassword);
        return ResponseEntity.ok(JsonResponse.success("password verify success"));
    }

    @PostMapping("/verification/newPassword")
    public ResponseEntity<JsonResponse<String>> changePassword(@RequestBody NewPasswordDTO newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        memberService.changePassword(memberId, newPassword);
        return ResponseEntity.ok(JsonResponse.success("password change success"));
    }

    @PostMapping("/image")
    public ResponseEntity<JsonResponse<UploadFileDTO>> uploadFile(
            @RequestPart MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        UploadFile uploadFile = s3uploaderService.uploadFile(file);
        String imageUrl = uploadFile.getStoreFileName();
        UploadFileDTO uploadImage = memberService.uploadImage(memberId, imageUrl);
        return ResponseEntity.ok(JsonResponse.success(uploadImage));
    }

    @DeleteMapping("/image")
    public ResponseEntity<JsonResponse<UploadFileDTO>> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        s3uploaderService.deleteFile(fileUrl);
        String imageUrl = "basic.jpg";
        UploadFileDTO uploadImage = memberService.uploadImage(memberId, imageUrl);
        return ResponseEntity.ok(JsonResponse.success(uploadImage));
    }

    @PostMapping("/email")
    public ResponseEntity<JsonResponse<String>> updateEmail(@RequestBody NewEmailDTO newEmail) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        memberService.changeEmail(memberId, newEmail);
        return ResponseEntity.ok(JsonResponse.success("Email changed successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<JsonResponse<String>> withdrawMember(@RequestBody Integer memberIdx) {

        log.info("withdraw memberIdx:" + memberIdx.toString());
        memberService.withdrawMember(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success("Withdraw successfully"));
    }
}
