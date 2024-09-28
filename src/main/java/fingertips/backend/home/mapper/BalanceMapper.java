package fingertips.backend.home.mapper;

import fingertips.backend.home.dto.BalanceDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BalanceMapper {

    List<BalanceDTO> getBalance(int member_index);

}
