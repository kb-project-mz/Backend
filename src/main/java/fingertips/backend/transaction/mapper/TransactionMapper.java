package fingertips.backend.transaction.mapper;

import fingertips.backend.transaction.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {

    List<TransactionDTO> getTransaction(Integer memberIdx);
}
