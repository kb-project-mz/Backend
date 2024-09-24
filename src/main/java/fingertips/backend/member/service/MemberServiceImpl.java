package fingertips.backend.member.service;

import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.extern.log4j.Log4j;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    public String authenticate(String username, String password) {
        MemberDTO memberDTO = mapper.getMember(username);
        if (memberDTO != null && passwordEncoder.matches(password, memberDTO.getPassword())) {
            return jwtProcessor.generateAccessToken(username, memberDTO.getRole());
        }
        throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
    }

    public void joinMember(MemberDTO memberDTO) {
        String memberId = memberDTO.getMemberId();
        String email = memberDTO.getEmail();

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);

        if (getMemberByMemberId(memberId) != null) {
            throw new ApplicationException(ApplicationError.MEMBER_ID_DUPLICATED);
        }

        if (isEmailTaken(email)) {
            throw new ApplicationException(ApplicationError.EMAIL_DUPLICATED);
        }

        mapper.insertMember(memberDTO);
    }

    public MemberDTO getMemberByMemberId(String memberId) {

        return mapper.getMember(memberId);
    }

    public void deleteMember(String username) {
        mapper.deleteMember(username);
    }

    public String findByNameAndEmail(MemberIdFindDTO memberIdFindDTO) {

        String memberId = mapper.findByNameAndEmail(memberIdFindDTO);

        if (memberId == null) {
            throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
        }

        return memberId;
    }

    @Override
    public void setRefreshToken(MemberDTO memberDTO) {

        mapper.setRefreshToken(memberDTO);
    }

    @Override
    public boolean isEmailTaken(String email) {

        int isTaken = mapper.isEmailTaken(email);
        return isTaken != 0;
    }

    @Override
    public boolean existsMemberId(String memberId) {
        return mapper.existsMemberId(memberId) != 0;
    }
}
