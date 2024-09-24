package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class EmailController {

    private final EmailService emailService;

    // 이메일 인증 코드 확인
    @PostMapping("/verify-email")
    public ResponseEntity<JsonResponse<Boolean>> verifyEmail(@RequestBody MemberDTO memberDTO) {
        boolean isVerified = emailService.verifyEmail(memberDTO.getEmail(), memberDTO.getInputCode());
        return ResponseEntity.ok(JsonResponse.success(isVerified));
    }

    // 이메일 변경
    @PostMapping("/request-email-change")
    public ResponseEntity<JsonResponse<String>> requestEmailChange(@RequestBody MemberDTO memberDTO) {
        emailService.sendVerificationEmail(memberDTO.getNewEmail(), emailService.generateVerificationCode());
        return ResponseEntity.ok(JsonResponse.success("변경 요청이 성공적으로 전송되었습니다."));
    }

    // 인증 코드 재전송
    @PostMapping("/resend-verification-code")
    public ResponseEntity<JsonResponse<String>> resendVerificationCode(@RequestBody MemberDTO memberDTO) {
        String newVerificationCode = emailService.generateVerificationCode();
        emailService.sendVerificationEmail(memberDTO.getEmail(), newVerificationCode);
        return ResponseEntity.ok(JsonResponse.success("인증 코드가 재전송되었습니다."));
    }
}
