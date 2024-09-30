package fingertips.backend.member.service;

public interface EmailService {

    void sendVerificationEmail(String email, String verificationCode);
    boolean verifyCode(String email, String inputCode);
    boolean verifyEmail(String email, String inputCode);
    String generateVerificationCode();
    boolean isEmailTaken(String email);
}


