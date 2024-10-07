package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.member.service.EmailService;
import fingertips.backend.security.account.dto.AuthDTO;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;
import fingertips.backend.member.service.MemberService;

import fingertips.backend.member.service.UploadFileService;
import fingertips.backend.member.util.UploadFile;
import fingertips.backend.security.account.dto.AuthDTO;


import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private final UploadFileService uploadFileService;
    private final JwtProcessor jwtProcessor;


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
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam String password,
            @RequestParam String newPassword,
            @RequestParam String email) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String memberId = authentication.getName();

        String imageUrl = null;
        UploadFile uploadFile = uploadFileService.storeFile(profileImage);
        imageUrl = uploadFile.getStoreFileName();

        log.info("0000000000000000000" + imageUrl);
        UpdateProfileDTO updateProfile = UpdateProfileDTO.builder()
                .memberId(memberId)
                .password(password)
                .newPassword(newPassword)
                .imageUrl(imageUrl)
                .email(email)
                .build();

        memberService.updateProfile(memberId, updateProfile);

        ProfileDTO updatedProfile = memberService.getProfile(memberId);
        return ResponseEntity.ok(JsonResponse.success(updatedProfile));
    }

}
