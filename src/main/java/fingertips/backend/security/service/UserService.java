package fingertips.backend.security.service;


import fingertips.backend.security.account.dto.UserDTO;
import fingertips.backend.security.account.mapper.UserDetailsMapper;
import fingertips.backend.security.util.JwtProcessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Service

// 사용자 등록, 사용자 정보 업데이트, 사용자 삭제, 사용자 인증 등을 처리하는 서비스 관리
public class UserService {

    private final UserDetailsMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProcessor jwtProcessor;

    public String authenticate(String username, String password) {
        UserDTO userDTO = mapper.get(username);
        if (userDTO != null && passwordEncoder.matches(password, userDTO.getPassword())) {
            List<String> roles = userDTO.getRoles(); // Assume roles are stored in userDTO
            return jwtProcessor.generateAccessToken(username, roles);
        }
        throw new RuntimeException("Invalid username or password");
    }

    public void registerUser(UserDTO userDTO) {

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        userDTO.setPassword(encodedPassword);

        mapper.insert(userDTO);
    }

    public UserDTO getUserByUsername(String username) {
        return mapper.get(username);
    }

    public boolean validateUser(String username, String password) {
        UserDTO userDTO = getUserByUsername(username);

        if (userDTO != null) {
            return passwordEncoder.matches(password, userDTO.getPassword());
        }

        return false;
    }
}



