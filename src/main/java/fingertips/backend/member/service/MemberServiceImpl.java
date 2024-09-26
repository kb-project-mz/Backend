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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;
    private final MemberMapper memberMapper;

    public String authenticate(String username, String password) {
        MemberDTO memberDTO = mapper.getMember(username);
        if (memberDTO != null && passwordEncoder.matches(password, memberDTO.getPassword())) {
            return jwtProcessor.generateAccessToken(username, memberDTO.getRole());
        }
        throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
    }

    public void joinMember(MemberDTO memberDTO) {

        String encodedPassword = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encodedPassword);

        mapper.insertMember(memberDTO);
    }

    public MemberDTO getMemberByMemberId(String memberId) {

        MemberDTO member = mapper.getMember(memberId);
        if (member == null) {
            throw new ApplicationException(ApplicationError.MEMBER_NOT_FOUND);
        }
        return member;
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

    @Override
    public MemberDTO getMemberInfo(String memberId) {
        return memberMapper.getMemberInfo(memberId);
    }

    @Override
    public void updateMemberInfo(String memberId, MemberDTO memberDTO) {
        // 1. 로그인 한 사용자의 정보 가져오기
        MemberDTO currentInfo = memberMapper.getMemberInfo(memberId);

        String inputPassword = memberDTO.getPassword(); // 사용자가 입력한 비밀번호 : 값 들어옴
        String inputNewPassword = memberDTO.getNewPassword(); // 사용자가 입력한 새 비밀번호
        String inputCheckNewPassword = memberDTO.getCheckNewPassword(); // 사용자가 입력한 비밀번호 확인 필드

        String currentPassword = currentInfo.getPassword(); // 이건 현재 DB에 저장되어 있는 암호화된 비밀번호임 : 값 들어옴
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // 2. 현재 비밀번호 맞는지 확인(입력받은 password랑 현재 저장 되어있는 멤버의 정보에서 password 가져온거랑 비교)
        if (inputPassword != null && !passwordEncoder.matches(inputPassword, currentPassword)) {
            throw new ApplicationException(ApplicationError.PASSWORD_MISMATCH);
        }
        if(inputNewPassword != null && !inputNewPassword.equals(inputCheckNewPassword)){
            throw new ApplicationException(ApplicationError.PASSWORDCHECK_MISMATCH);
        }
        // 3. 디비 비밀번호 변경
        MemberDTO updateDTO = MemberDTO.builder()
//                //.memberId(memberId)
                    .newPassword(inputNewPassword) // 새 비밀번호 이거나,, 기존 비밀번호가 들어가 있겠지?
//                //.newEmail(memberDTO.getNewEmail() != null ? memberDTO.getNewEmail() : currentInfo.getEmail())
//                //.imageUrl(memberDTO.getImageUrl() != null ? memberDTO.getImageUrl() : currentInfo.getImageUrl())
                   .build();
        log.info("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + updateDTO.getNewPassword());
        memberMapper.updateMemberInfo(memberId, updateDTO);


//        // 4. 비밀번호 완료 메세지 띄우고 로그아웃 처리
//
//        // 새 비밀번호 암호화
//        String encodedNewPassword = inputNewPassword != null
//                ? passwordEncoder.encode(inputNewPassword)
//                : currentInfo.getPassword(); // 비밀번호 변경이 없는 경우 현재 비밀번호 유지
//
//        // 업데이트할 DTO 생성
//        MemberDTO updateDTO = MemberDTO.builder()
//                //.memberId(memberId)
//                .newPassword(encodedNewPassword) // 새 비밀번호 이거나,, 기존 비밀번호가 들어가 있겠지?
//                //.newEmail(memberDTO.getNewEmail() != null ? memberDTO.getNewEmail() : currentInfo.getEmail())
//                //.imageUrl(memberDTO.getImageUrl() != null ? memberDTO.getImageUrl() : currentInfo.getImageUrl())
//                .build();
//
//        // DB 업데이트
//        memberMapper.updateMemberInfo(memberId, updateDTO);
    }


}
