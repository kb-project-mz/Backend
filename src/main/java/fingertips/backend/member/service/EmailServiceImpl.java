package fingertips.backend.member.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;

@Service
public class EmailServiceImpl implements EmailService {

    private String username;
    private String password;
    private String host;
    private String port;

    private Map<String, String> verificationCodes = new HashMap<>();

    public EmailServiceImpl() {
        loadEmailProperties();
    }

    private void loadEmailProperties() {
        try {
            Resource resource = new ClassPathResource("env.properties");
            Properties props = new Properties();
            InputStream input = resource.getInputStream();
            props.load(input);
            host = props.getProperty("mail.smtp.host");
            port = props.getProperty("mail.smtp.port");
            username = props.getProperty("mail.username");
            password = props.getProperty("mail.password");
        } catch (Exception e) {
            throw new ApplicationException(ApplicationError.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("이메일 인증 코드");
            message.setText("인증 코드는 " + verificationCode + "입니다.");

            Transport.send(message);
            System.out.println("이메일이 성공적으로 전송되었습니다.");

            verificationCodes.put(email, verificationCode);

        } catch (MessagingException e) {
            throw new ApplicationException(ApplicationError.EMAIL_SENDING_FAILED);
        }
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        String storedCode = verificationCodes.get(email);
        if (storedCode == null) {
            throw new ApplicationException(ApplicationError.INVALID_VERIFICATION_CODE);
        }
        if (!storedCode.equals(inputCode)) {
            throw new ApplicationException(ApplicationError.INVALID_VERIFICATION_CODE);
        }

        verificationCodes.remove(email);
        return true;
    }

    @Override
    public boolean verifyEmail(String email, String inputCode) {
        return verifyCode(email, inputCode);
    }

    @Override
    public String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }
}
