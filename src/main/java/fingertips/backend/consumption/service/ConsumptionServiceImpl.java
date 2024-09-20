package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.mapper.ConsumptionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService {

    private final ConsumptionMapper consumptionMapper;

    @Override
    public List<CardConsumptionDTO> getCardHistoryList(int memberId) {
        return consumptionMapper.getCardHistoryList(memberId);
    }
}
