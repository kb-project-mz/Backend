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

    @GetMapping("/memberInfo")
    public ResponseEntity<JsonResponse<MemberDTO>> getMember(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
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

    @GetMapping("/info")
    public ResponseEntity<JsonResponse<ProfileDTO>> getMemberInfo(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        ProfileDTO profile = memberService.getProfile(memberId);
        return ResponseEntity.ok(JsonResponse.success(profile));
    }

    @PostMapping("/verification/password")
    public ResponseEntity<JsonResponse<String>> verifyPassword(@RequestHeader("Authorization") String token,
                                                               @RequestBody VerifyPasswordDTO verifyPassword) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        memberService.verifyPassword(memberId, verifyPassword);
        return ResponseEntity.ok(JsonResponse.success("password verify success"));
    }

    @PostMapping("/verification/newPassword")
    public ResponseEntity<JsonResponse<String>> changePassword(@RequestHeader("Authorization") String token,
                                                               @RequestBody NewPasswordDTO newPassword) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        memberService.changePassword(memberId, newPassword);
        return ResponseEntity.ok(JsonResponse.success("password change success"));
    }

    @PostMapping("/image")
    public ResponseEntity<JsonResponse<UploadFileDTO>> uploadFile(
            @RequestHeader("Authorization") String token,
            @RequestPart MultipartFile file) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        UploadFile uploadFile = s3uploaderService.uploadFile(file);
        String imageUrl = uploadFile.getStoreFileName();
        UploadFileDTO uploadImage = memberService.uploadImage(memberId, imageUrl);
        return ResponseEntity.ok(JsonResponse.success(uploadImage));
    }

    @DeleteMapping("/image")
    public ResponseEntity<JsonResponse<UploadFileDTO>> deleteFile(
            @RequestHeader("Authorization") String token,
            @RequestParam("fileUrl") String fileUrl) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);

        s3uploaderService.deleteFile(fileUrl);
        String imageUrl = "basic-image/basic.jpg";
        UploadFileDTO uploadImage = memberService.uploadImage(memberId, imageUrl);
        return ResponseEntity.ok(JsonResponse.success(uploadImage));
    }

    @PostMapping("/email")
    public ResponseEntity<JsonResponse<String>> updateEmail(@RequestHeader("Authorization") String token,
                                                            @RequestBody NewEmailDTO newEmail) {
        String accessToken = jwtProcessor.extractToken(token);
        String memberId = jwtProcessor.getMemberId(accessToken);
        memberService.changeEmail(memberId, newEmail);
        return ResponseEntity.ok(JsonResponse.success("Email changed successfully"));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<JsonResponse<String>> withdrawMember(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        memberService.withdrawMember(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success("Withdraw successfully"));
    }
}
