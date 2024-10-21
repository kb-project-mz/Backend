package fingertips.backend.security.service;

import fingertips.backend.member.dto.MemberDTO;
import fingertips.backend.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Log4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberDTO memberDTO = mapper.getMemberByMemberId(username);

        if (memberDTO == null) {
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }

        return new User(
                memberDTO.getMemberId(),
                memberDTO.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(memberDTO.getRole()))
        );
    }
}
