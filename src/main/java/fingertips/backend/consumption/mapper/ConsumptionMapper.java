package fingertips.backend.consumption.mapper;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConsumptionMapper {

    List<CardConsumptionDTO> getCardHistoryList(int memberId);
}
