package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;

import java.util.List;
import java.util.Map;

public interface ConsumptionService {

    List<CardConsumptionDTO> getCardHistoryList(int memberId);
}