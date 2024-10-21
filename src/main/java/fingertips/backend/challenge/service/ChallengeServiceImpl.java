package fingertips.backend.challenge.service;

import fingertips.backend.challenge.dto.CardTransactionFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.mapper.ChallengeMapper;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeMapper challengeMapper;
    private final OpenAiService openAiService;

    @Override
    public List<ChallengeDTO> getChallengeList(Integer memberIdx) {
        return challengeMapper.getChallengeList(memberIdx);
    }

    @Override
    public void insertChallenge(ChallengeDTO challengeDto) {
        challengeMapper.insertChallenge(challengeDto);
    }

    @Override
    public void deleteChallenge(Integer challengeId) {
        challengeMapper.deleteChallenge(challengeId);
    }

    @Override
    public List<String> getCardHistoryContentByCategory(CardTransactionFilterDTO cardHistoryFilterDTO) {
        return challengeMapper.getCardHistoryContentByCategory(cardHistoryFilterDTO);
    }

    @Override
    public List<ProgressDTO> getChallengeStatus(Integer memberId) {
        return challengeMapper.getChallengeStatus(memberId);
    }

    // TODO : 프롬프트 수정
    @Override
    public List<String> getDetailedCategories(CardTransactionFilterDTO cardHistoryFilterDTO) {

        List<String> cardHistoryList = getCardHistoryContentByCategory(cardHistoryFilterDTO);

        String data = formatConsumptionListAsTable(cardHistoryList);
        String prompt = data.concat("이건 사용자가 돈을 쓴 사용처 목록이야." +
                "동일한 브랜드에 속하는 상호명을 인식하여 하나의 브랜드로 통일해줘. " +
                "예를 들어, '스타벅스 대화역점'과 '스타벅스 어린이대공원'은 '스타벅스'로 인식하고 통일해. " +
                "공백, 특수문자, 숫자, 지점명(예: 'OO점', '역', '어린이대공원', '_34') 같은 불필요한 부분은 제거하고, 핵심 브랜드명만 추출해줘. " +
                "예를 들어, '충칭 마라탕'과 '충칭마라탕'은 동일한 브랜드로 인식해. " +
                "단, '마라탕', '치킨', '커피'와 같은 공통 단어를 기준으로 통합하지 말고, 브랜드명에서 일관된 부분만 통일해줘." +
                "여기서 3번 이상 반복되는 사용처를 정확하게 알려줘. 다른 말 붙이지 말고 반복되는 사용처가 어떤 게 있는지 콤마로 나열해서 보내줘.");

        String openAiResponse = openAiService.askOpenAi(prompt);

        return Arrays.asList(openAiResponse.split(", "));
    }

    @Override
    public List<ChallengeDTO> getPeerChallengeList(Integer memberIdx) {

        return challengeMapper.getPeerChallengeList(memberIdx);
    }

    public String formatConsumptionListAsTable(List<String> cardHistory) {

        StringBuilder table = new StringBuilder();
        String lineSeparator = System.lineSeparator();

        table.append("|-----------------------|").append(lineSeparator);
        table.append("|        content        |").append(lineSeparator);
        table.append("|-----------------------|").append(lineSeparator);

        for (String history : cardHistory) {
            table.append(String.format("| %-25s |", history)).append(lineSeparator);
        }

        table.append("|-----------------------|").append(lineSeparator);

        return table.toString();
    }
}
