package fingertips.backend.member.service;

import fingertips.backend.member.mapper.EmailMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.*;
import javax.mail.internet.*;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final EmailMapper emailMapper;
    private JavaMailSenderImpl javaMailSender;

    private String username;
    private String password;
    private String host;
    private String port;

    private Map<String, String> verificationCodes = new HashMap<>();

    @PostConstruct
    private void init() {
        loadEmailProperties();
        configureJavaMailSender();
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

    private void configureJavaMailSender() {
        javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(Integer.parseInt(port));
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
    }

    @Override
    public void sendVerificationEmail(String email, String verificationCode) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            message.setFrom(new InternetAddress(username));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("이메일 인증 코드");
            message.setText("인증 코드는 " + verificationCode + "입니다.");

            javaMailSender.send(message);
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

    @Override
    public boolean isEmailTaken(String email) {
        int isTaken = emailMapper.isEmailTaken(email);
        return isTaken != 0;
    }
}
