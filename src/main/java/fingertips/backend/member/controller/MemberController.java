package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;
import fingertips.backend.member.service.MemberService;
import fingertips.backend.member.service.UploadFileService;
import fingertips.backend.member.util.UploadFile;
import fingertips.backend.security.account.dto.AuthDTO;
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
import java.util.Map;
import java.util.logging.Logger;

@Log4j
@RequestMapping("/api/v1/member")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JwtProcessor jwtProcessor;
    private final UploadFileService uploadFileService;


    @PostMapping("/join")
    public ResponseEntity<JsonResponse<String>> join(@RequestBody MemberDTO memberDTO) {

        memberService.joinMember(memberDTO);
        return ResponseEntity.ok().body(JsonResponse.success("Join Success"));
    }

    @GetMapping("/memberIdx/{memberName}/{email}")
    public ResponseEntity<JsonResponse<MemberIdFindDTO>> findMemberId(@PathVariable String memberName, @PathVariable String email) {

        String foundMemberId = memberService.findByNameAndEmail(memberName, email);

        MemberIdFindDTO memberIdFindDTO = new MemberIdFindDTO(foundMemberId, memberName, email);

        return ResponseEntity.ok(JsonResponse.success(memberIdFindDTO));
    }

    @GetMapping("/check-memberId/{memberId}")
    public ResponseEntity<JsonResponse<Boolean>> checkMemberId(@PathVariable String memberId) {

        boolean exists = memberService.existsMemberId(memberId);
        return ResponseEntity.ok(JsonResponse.success(exists));
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

    @GetMapping("/memberInfo/{memberId}")
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
    @GetMapping("/info")
    public ResponseEntity<JsonResponse<ProfileDTO>> getMemberInfo() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();
        ProfileDTO profile = memberService.getProfile(memberId);
        return ResponseEntity.ok(JsonResponse.success(profile));
    }

//    @PostMapping("/info")
//    public ResponseEntity<JsonResponse<ProfileDTO>> updateMemberInfo(@RequestBody UpdateProfileDTO updateProfile) {
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String memberId = authentication.getName();
//        memberService.updateProfile(memberId, updateProfile);
//        ProfileDTO updatedProfile = memberService.getProfile(memberId);
//        return ResponseEntity.ok(JsonResponse.success(updatedProfile));
//    }

    @PostMapping("/info")
    public ResponseEntity<JsonResponse<ProfileDTO>> updateMemberInfo(
            @RequestParam("profileImage") MultipartFile profileImage,
            @RequestParam String password,
            @RequestParam String newPassword,
            @RequestParam String email) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        String imageUrl = null;
        if(profileImage != null && !profileImage.isEmpty()) {
            UploadFile uploadFile = uploadFileService.storeFile(profileImage);
            imageUrl = uploadFile.getStoreFileName();
        }

        UpdateProfileDTO updateProfile = UpdateProfileDTO.builder()
                .memberId(memberId)
                .password(password)
                .newPassword(newPassword)
                .imageUrl(imageUrl)
                .email(email)
                .build();

        memberService.updateProfile(memberId, updateProfile);

        log.info("111111111================================");
        ProfileDTO updatedProfile = memberService.getProfile(memberId);
        return ResponseEntity.ok(JsonResponse.success(updatedProfile));
    }

}
