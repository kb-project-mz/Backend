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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Log4j
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

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
                                          BCryptPasswordEncoder passwordEncoder) {
        if (inputPassword == null || !passwordEncoder.matches(inputPassword, existingPassword)) {
            throw new ApplicationException(ApplicationError.PASSWORD_MISMATCH);
        }
    }

    public void withdrawMember(String memberIdx) {
        memberMapper.withdrawMember(memberIdx);
    }
}
