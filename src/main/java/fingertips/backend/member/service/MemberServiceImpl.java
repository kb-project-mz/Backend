package fingertips.backend.member.service;

import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.dto.MemberIdFindDTO;
import fingertips.backend.member.dto.ProfileDTO;
import fingertips.backend.member.dto.UpdateProfileDTO;
import fingertips.backend.member.mapper.MemberMapper;
import fingertips.backend.security.util.JwtProcessor;
import lombok.extern.log4j.Log4j;
import lombok.RequiredArgsConstructor;
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
        MemberDTO memberDTO = mapper.getMemberByMemberId(username);
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

        MemberDTO member = mapper.getMemberByMemberId(memberId);
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
    public ProfileDTO getProfile(String memberId) {
        return memberMapper.getProfile(memberId);
    }

    @Override
    public void updateProfile(String memberId, UpdateProfileDTO updateProfile) {
        ProfileDTO existingProfile = memberMapper.getProfile(memberId);
        String existingPassword = memberMapper.getPassword(memberId);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        validateCurrentPassword(updateProfile.getPassword(), existingPassword, passwordEncoder);

        String updatedPassword = (updateProfile.getNewPassword() != null && !updateProfile.getNewPassword().isEmpty())
                ? passwordEncoder.encode(updateProfile.getNewPassword())
                : existingPassword;

        UpdateProfileDTO updatedProfile = UpdateProfileDTO.builder()
                .memberId(memberId)
                .password(updatedPassword)
                .email(updateProfile.getEmail() != null ? updateProfile.getEmail() : existingProfile.getEmail())
                .imageUrl(updateProfile.getImageUrl() != null ? updateProfile.getImageUrl() : existingProfile.getImageUrl())
                .build();

        memberMapper.updateProfile(updatedProfile);
    }

    private void validateCurrentPassword (String inputPassword,
                                          String existingPassword,
                                          BCryptPasswordEncoder passwordEncoder){
        if (inputPassword == null || !passwordEncoder.matches(inputPassword, existingPassword)) {
            throw new ApplicationException(ApplicationError.PASSWORD_MISMATCH);
        }
    }
}

