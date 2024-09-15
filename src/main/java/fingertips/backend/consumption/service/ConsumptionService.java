package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.CardConsumptionDTO;

import java.util.List;

public interface ConsumptionService {

    List<CardConsumptionDTO> getCardHistoryList(int memberId);
}
