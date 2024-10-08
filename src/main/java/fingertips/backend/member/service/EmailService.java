package fingertips.backend.member.service;

import fingertips.backend.member.dto.PasswordFindDTO;

public interface EmailService {

    void sendVerificationEmail(String email, String verificationCode);
    boolean verifyCode(String email, String inputCode);
    boolean verifyEmail(String email, String inputCode);
    String generateVerificationCode();
    boolean isEmailTaken(String email);
    String generateRandomPassword();
    void sendNewPasswordEmail(String email, String newPassword);
    PasswordFindDTO processFindPasswordAndUpdate(PasswordFindDTO passwordFindDTO);
}


