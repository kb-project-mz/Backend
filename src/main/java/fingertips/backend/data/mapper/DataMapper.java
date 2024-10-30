package fingertips.backend.data.mapper;

import fingertips.backend.data.dto.DataDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DataMapper {
    void insertData(DataDTO dataDTO);
}
