package fingertips.backend.member.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailMapper {

    int isEmailTaken(String email);
}
