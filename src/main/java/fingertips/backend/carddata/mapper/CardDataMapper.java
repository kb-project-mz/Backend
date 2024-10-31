package fingertips.backend.carddata.mapper;

import fingertips.backend.carddata.dto.CardDataDTO;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface CardDataMapper {
    void insertCardData(CardDataDTO cardDataDTO);
}
