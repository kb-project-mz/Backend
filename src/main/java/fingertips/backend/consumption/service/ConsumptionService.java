package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.AccountConsumptionDTO;
import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;

import java.util.List;
import java.util.Map;

public interface ConsumptionService {

    List<CardConsumptionDTO> getCardHistoryList(int memberId);
    List<CardConsumptionDTO> getCardHistoryListByPeriod(PeriodDTO period);
    String getMostAndMaximumUsed(PeriodDTO period);
    List<AccountConsumptionDTO> getAccountHistoryList(int memberId);
}
