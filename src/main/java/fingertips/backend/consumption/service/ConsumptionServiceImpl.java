package fingertips.backend.consumption.service;

import fingertips.backend.consumption.dto.CardConsumptionDTO;
import fingertips.backend.consumption.dto.PeriodDTO;
import fingertips.backend.consumption.mapper.ConsumptionMapper;
import fingertips.backend.openai.service.OpenAiService;

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
    private final OpenAiService openAiService;

    @Override
    public List<CardConsumptionDTO> getCardHistoryList(int memberId) {
        return consumptionMapper.getCardHistoryList(memberId);
    }

    @Override
    public List<CardConsumptionDTO> getCardHistoryListByPeriod(PeriodDTO period) {
        return consumptionMapper.getCardHistoryListByPeriod(period);
    }

    // TODO : 프롬프트 수정
    @Override
    public Map<String, String> getMostAndMaximumUsed(PeriodDTO period) {

        Map<String, String> response = new HashMap<>();

        List<CardConsumptionDTO> cardHistoryListByPeriod = getCardHistoryListByPeriod(period);
        String data = formatConsumptionListAsTable(cardHistoryListByPeriod);

        String prompt = data.concat("이 테이블의 content 테이블은 돈을 쓴 사용처야. " +
                "동일한 브랜드에 속하는 상호명을 인식하여 하나의 브랜드로 통일해줘. " +
                "예를 들어, '스타벅스 대화역점'과 '스타벅스 어린이대공원'은 '스타벅스'로 인식하고 통일해. " +
                "공백, 특수문자, 지점명(예: 'OO점', '역', '어린이대공원', '_34') 같은 불필요한 부분은 제거하고, 핵심 브랜드명만 추출해줘. " +
                "예를 들어, '충칭 마라탕'과 '충칭마라탕'은 동일한 브랜드로 인식해. " +
                "단, '마라탕', '치킨', '커피'와 같은 공통 단어를 기준으로 통합하지 말고, 브랜드명에서 일관된 부분만 통일해줘." +
                "두 가지 질문에 대해 정확하게 대답해. " +
                "1. 동일한 브랜드로 방문한 사용처를 테이블에서 찾아 정확히 카운트해서 방문 횟수 기준으로 내림차순으로 3개를 추출해주고," +
                "결과를 확실하게 검토한 후 각 브랜드의 방문 횟수를 적어줘. " +
                "2. 브랜드가 동일한 상호명을 그룹화하고, 각 브랜드별로 총 금액을 테이블에서 찾아 정확히 계산해서 총 금액 내림차순으로 3개를 추출해주고, " +
                "결과를 확실하게 검토한 후 각 브랜드에서 사용한 총액을 적어줘. " +
                "대답할 때 다른 말 붙이지 말고 [지점명:방문 횟수, 지점명:방문 횟수, 지점명:방문 횟수], [지점명:총 금액, 지점명:총 금액, 지점명:총 금액]" +
                "이렇게 리스트로만 대답하는데 이때 방문 횟수 내림차순으로, 총 금액 내림차순으로 정렬해서 대답해.");

        String result = openAiService.askOpenAi(prompt);

        response.put("data", result);
        return response;
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
