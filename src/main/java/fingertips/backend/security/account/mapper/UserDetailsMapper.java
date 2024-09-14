package fingertips.backend.security.account.mapper;

import fingertips.backend.security.account.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailsMapper {
    UserDTO get(String username);

    void insert(UserDTO userDTO);
}
