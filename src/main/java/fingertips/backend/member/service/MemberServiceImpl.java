package fingertips.backend.member.service;

import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.PasswordFindDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.extern.log4j.Log4j;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    private static final Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);

    public String authenticate(String username, String password) {
        MemberDTO memberDTO = memberMapper.getMemberByMemberId(username);
        if (memberDTO != null && passwordEncoder.matches(password, memberDTO.getPassword())) {
            return jwtProcessor.generateAccessToken(username, memberDTO.getRole());
        }
        throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
    }

    public void joinMember(MemberDTO memberDTO) {

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);

        memberMapper.insertMember(memberDTO);
    }

    public MemberDTO getMemberByMemberId(String memberId) {

        MemberDTO member = memberMapper.getMemberByMemberId(memberId);
        if (member == null) {
            throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
        }
        return member;
    }

    @Override
    public void setRefreshToken(MemberDTO memberDTO) {

        memberMapper.setRefreshToken(memberDTO);
    }

    @Override
    public boolean existsMemberId(String memberId) {
        return memberMapper.existsMemberId(memberId) != 0;
    }

    @Override
    public boolean existsMemberName(String memberName) {
        return memberMapper.existsMemberName(memberName) != 0;
    }

    public boolean checkEmailDuplicate(String email) {
        System.out.println("입력된 이메일: " + email);
        return memberMapper.checkEmailDuplicate(email) > 0;
    }

    @Override
    public void clearRefreshToken(String memberId) {
        memberMapper.clearRefreshToken(memberId);
    }

    public String findByNameAndEmail(String memberName, String email) {

        MemberIdFindDTO memberIdFindDTO = MemberIdFindDTO.builder()
                .memberName(memberName)
                .email(email)
                .build();

        try {
            String foundMemberId = memberMapper.findByNameAndEmail(memberIdFindDTO);

            if (foundMemberId == null) {
                throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
            }
            System.out.println(foundMemberId);
            return foundMemberId;
        } catch (Exception e) {

            log.error("Error occurred while finding member by name and email: ", e);
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR);
        }
    }

    public void withdrawMember(String memberId) {

        memberMapper.withdrawMember(memberId);
    }

    @Override
    public void updatePasswordByEmail(PasswordFindDTO passwordFindDTO) {
        logger.info("이메일: {}로 비밀번호 업데이트 요청이 접수되었습니다.", passwordFindDTO.getEmail());

        String encryptedPassword = passwordEncoder.encode(passwordFindDTO.getNewPassword());
        logger.info("입력된 비밀번호가 암호화되었습니다.");

        passwordFindDTO.setNewPassword(encryptedPassword);

        try {
            memberMapper.updatePasswordByEmail(passwordFindDTO);
            logger.info("비밀번호가 성공적으로 업데이트되었습니다. 이메일: {}", passwordFindDTO.getEmail());
        } catch (Exception e) {
            logger.error("비밀번호 업데이트 중 오류 발생. 이메일: {}", passwordFindDTO.getEmail(), e);
            throw e;  // 예외를 다시 던져서 처리할 수 있도록 함
        }
    }
}
