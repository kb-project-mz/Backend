package fingertips.backend.transaction.service;

import fingertips.backend.transaction.dto.AccountTransactionDTO;
import fingertips.backend.transaction.dto.CardTransactionDTO;
import fingertips.backend.transaction.dto.PeriodDTO;
import fingertips.backend.transaction.mapper.TransactionMapper;
import fingertips.backend.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper consumptionMapper;
    private final OpenAiService openAiService;

    @Override
    public List<CardTransactionDTO> getCardTransactionList(Integer memberId) {
        return consumptionMapper.getCardTransactionList(memberId);
    }

    @Override
    public List<CardTransactionDTO> getCardTransactionListByPeriod(PeriodDTO period) {
        return consumptionMapper.getCardTransactionListByPeriod(period);
    }

    // TODO : 프롬프트 수정
    @Override
    public String getMostAndMaximumUsed(PeriodDTO period) {

        List<CardTransactionDTO> cardTransactionListByPeriod = getCardTransactionListByPeriod(period);
        String data = formatConsumptionListAsTable(cardTransactionListByPeriod);

        String prompt = data.concat("이 테이블의 cardTransactionDescription 컬럼은 돈을 쓴 사용처야. " +
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

        return openAiService.askOpenAi(prompt);
    }

    @Override
    public List<AccountTransactionDTO> getAccountTransactionList(Integer memberId) {
        return consumptionMapper.getAccountTransactionList(memberId);
    }

    @Override
    public String getAiRecommendation(PeriodDTO period) {

        List<CardTransactionDTO> cardTransactionListByPeriod = getCardTransactionListByPeriod(period);
        String data = formatConsumptionListAsTable(cardTransactionListByPeriod);

        String prompt = data.concat("이 테이블의 cardTransactionDescription 컬럼은 한 달 동안 돈을 쓴 사용처야. " +
                "이 소비 내역을 보고 이 사람이 다음 달에 어떤 식으로 소비를 하면 얼마나 소비를 줄일 수 있을 지 간단하게 " +
                "이번 달에 어떤 곳에 소비를 많이 했네요. 다음 달에 이런 식으로, 여기에서 소비를 줄이면 얼마를 절약할 수 있을 것 같아요. " +
                "다음 달에는 이렇게 하면 어떨까요? 이런 식으로 세 줄로 간단하게 조언을 해줘. 꼭 Description 뿐만 아니라 " +
                "특정 카테고리에 너무 많이 사용하고 있거나 하는 등의 소비 패턴을 분석해서 조언해줘. 다른 말 붙이지 말고 " +
                "마지막 문장 끝에 ☺ 이모티콘을 붙여줘. 조언 여러 개 하지 말고 하나만 해.");

        return openAiService.askOpenAi(prompt);
    }

    public String formatConsumptionListAsTable(List<CardTransactionDTO> cardConsumption) {

        StringBuilder table = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        table.append("|----------|-------------------------------------|").append(lineSeparator);
        table.append("|  amount  |               content               |").append(lineSeparator);
        table.append("|----------|-------------------------------------|").append(lineSeparator);

        for (CardTransactionDTO transaction : cardConsumption) {
            table.append(String.format("| %-8d | %-35s |",
                    transaction.getAmount(), transaction.getCardTransactionDescription())).append(lineSeparator);
        }

        table.append("|----------|-------------------------------------|").append(lineSeparator);

        return table.toString();
    }
}
