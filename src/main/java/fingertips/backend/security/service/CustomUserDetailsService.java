package fingertips.backend.security.service;

import fingertips.backend.security.account.dto.UserDTO;
import fingertips.backend.security.account.mapper.UserDetailsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Log4j
@Service
@RequiredArgsConstructor

// Spring Security에서 사용자의 인증 정보를 로드하는 데 사용
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDetailsMapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO dto = mapper.get(username);
        if (dto == null) {
            throw new UsernameNotFoundException(username + "은 없는 id입니다.");
        }

        return new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getRoles().stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}


