package fingertips.backend.member.service;

import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.member.mapper.EmailMapper;
import fingertips.backend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
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

@Log4j
@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

  private final EmailMapper emailMapper;
  private JavaMailSenderImpl javaMailSender;
  private final MemberMapper memberMapper;
  private final PasswordEncoder passwordEncoder;

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
      message.setSubject("[fingertips] 이메일 인증 코드");
      message.setText("안녕하세요,\n"
              + "fingertips 입니다.\n\n"
              + "회원님의 계정 보호 및 확인을 위해 아래 인증 코드를 입력해 주세요\n\n"
              + "인증 코드: " + verificationCode + "\n\n"
              + "추가 문의사항이 있으시면 언제든지 연락 주시기 바랍니다.\n"
              + "감사합니다.\n\n"
              + "fingertips 팀 드림");
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

  @Override
  public String generateRandomPassword() {

    String randomPassword = RandomStringUtils.randomAlphanumeric(8);

    return randomPassword;
  }

  @Override
  public PasswordFindDTO processFindPasswordAndUpdate(PasswordFindDTO passwordFindDTO) {

    String newPassword = generateRandomPassword();

    passwordFindDTO.setNewPassword(newPassword);

    try {
      updatePasswordByEmail(passwordFindDTO);
    } catch (Exception e) {
      throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR);
    }

    try {
      sendNewPasswordEmail(passwordFindDTO.getEmail(), newPassword);
    } catch (Exception e) {
      throw new ApplicationException(ApplicationError.EMAIL_SENDING_FAILED);
    }

    return PasswordFindDTO.builder()
            .memberName(passwordFindDTO.getMemberName())
            .email(passwordFindDTO.getEmail())
            .newPassword(null)
            .build();
  }

  public void updatePasswordByEmail(PasswordFindDTO passwordFindDTO) throws Exception {
    String encryptedPassword = passwordEncoder.encode(passwordFindDTO.getNewPassword());
    passwordFindDTO.setNewPassword(encryptedPassword);
    memberMapper.updatePasswordByEmail(passwordFindDTO);
  }

  @Override
  public void sendNewPasswordEmail(String email, String newPassword) {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      message.setFrom(new InternetAddress(username));
      message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(email));
      message.setSubject("[fingertips] 임시 비밀번호 안내");
      message.setText("안녕하세요, fingertips입니다.\n\n"
              + "회원님의 계정 보호를 위해 임시 비밀번호가 발급되었습니다.\n"
              + "아래 비밀번호를 사용하여 로그인해 주시고, 보안을 위해 로그인 후 반드시 비밀번호를 변경해 주세요.\n\n"
              + "임시 비밀번호: " + newPassword + "\n\n"
              + "[로그인 후 비밀번호 변경 방법]\n\n"
              + "1. 로그인 후, 오른쪽 상단에 있는 '마이페이지' 를 이동합니다.\n"
              + "2. '비밀번호 변경' 을 통해 새 비밀번호를 설정합니다.\n"
              + "3. 비밀번호는 보안을 위해 8자 이상, 대문자, 소문자, 숫자, 특수문자를 포함해 주세요.\n\n"
              + "항상 최고의 서비스를 제공하기 위해 노력하는 fingertips가 되겠습니다.\n"
              + "추가적인 도움이 필요하시다면 언제든지 문의해 주세요.\n\n"
              + "fingertips를 이용해 주셔서 감사합니다.\n\n"
              + "fingertips 팀 드림");
      javaMailSender.send(message);
    } catch (MessagingException e) {
      throw new ApplicationException(ApplicationError.EMAIL_SENDING_FAILED);
    }
  }
}
