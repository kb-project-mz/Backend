package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.mapper.ConsumptionMapper;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsumptionServiceImpl implements ConsumptionService {

    private final ConsumptionMapper consumptionMapper;
    private final OpenAiService openAiService;

    @Override
    public List<CardConsumptionDTO> getCardHistoryList(int memberId) {
        return consumptionMapper.getCardHistoryList(memberId);
    }

    @Override
    public List<CardConsumptionDTO> getCardHistoryListByPeriod(PeriodDTO period) {
        return consumptionMapper.getCardHistoryListByPeriod(period);
    }

    @Override
    public String getMostUsedHistory(PeriodDTO period) {

        List<CardConsumptionDTO> cardHistoryListByPeriod = getCardHistoryListByPeriod(period);

        String data = formatConsumptionListAsTable(cardHistoryListByPeriod);
        String prompt = data.concat("이 테이블의 content 컬럼에서 가장 많이 나온 단어를 기준으로 " +
                "해당 단어와 횟수를 [단어:횟수, 단어:횟수, 단어:횟수] 형식으로 다른 말 붙이지 말고 리스트만으로 대답해. " +
                "단어가 아닌, 공백을 포함한 복합 명사를 추출해줘. " +
                "횟수 내림차순으로 3개만 대답하고 지점명이나 지역 이름은 빼고 계산해.");

        log.info(prompt);
        return openAiService.askOpenAi(prompt);
    }

    @Override
    public List<Map<String, Integer>> getMaximumAmountHistory(PeriodDTO period) {
        return null;
    }

    public String formatConsumptionListAsTable(List<CardConsumptionDTO> cardConsumption) {

        StringBuilder table = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        table.append("|----------|-------------------------------------|").append(lineSeparator);
        table.append("|  amount  |               content               |").append(lineSeparator);
        table.append("|----------|-------------------------------------|").append(lineSeparator);

        for (CardConsumptionDTO history : cardConsumption) {
            table.append(String.format("| %-8d | %-35s |",
                    history.getAmount(), history.getContent())).append(lineSeparator);
        }

        table.append("|----------|-------------------------------------|").append(lineSeparator);

        return table.toString();
    }
}
