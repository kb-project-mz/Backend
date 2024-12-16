package fingertips.backend.member.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.member.dto.EmailDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.member.service.EmailService;
import fingertips.backend.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/email")
public class EmailController {

    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @PostMapping("/verification")
    public ResponseEntity<JsonResponse<Boolean>> verifyEmail(@RequestBody EmailDTO emailDTO) {
        boolean isVerified = emailService.verifyEmail(emailDTO.getEmail(), emailDTO.getInputCode());
        return ResponseEntity.ok(JsonResponse.success(isVerified));
    }

    @PostMapping("/")
    public ResponseEntity<JsonResponse<String>> requestEmailChange(@RequestBody EmailDTO emailDTO) {
        String verificationCode = emailService.generateVerificationCode();
        emailService.sendVerificationEmail(emailDTO.getNewEmail(), verificationCode);
        return ResponseEntity.ok(JsonResponse.success("변경 요청이 성공적으로 전송되었습니다."));
    }

    @PostMapping("/code")
    public ResponseEntity<JsonResponse<String>> sendVerificationCode(@RequestBody EmailDTO emailDTO) {
        String newVerificationCode = emailService.generateVerificationCode();
        emailService.sendVerificationEmail(emailDTO.getEmail(), newVerificationCode);
        return ResponseEntity.ok(JsonResponse.success("인증 코드가 전송되었습니다."));
    }

    @PostMapping("/newpassword")
    public ResponseEntity<JsonResponse<PasswordFindDTO>> findPassword(@RequestBody PasswordFindDTO passwordFindDTO) {
        PasswordFindDTO responseDTO = emailService.processFindPasswordAndUpdate(passwordFindDTO);
        return ResponseEntity.ok(JsonResponse.success(responseDTO));
    }
}


