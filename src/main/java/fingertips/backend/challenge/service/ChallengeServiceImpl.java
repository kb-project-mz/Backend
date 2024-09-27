package fingertips.backend.challenge.service;


import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.CardHistoryFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.mapper.ChallengeMapper;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<CardHistoryDTO> getCardHistoryContentByCategory(CardHistoryFilterDTO cardHistoryFilterDTO) {
        return challengeMapper.getCardHistoryContentByCategory(cardHistoryFilterDTO);
    }

    @Override
    public List<ProgressDTO> getChallengeStatus(Integer memberId) {
        return challengeMapper.getChallengeStatus(memberId);
    }

    // TODO : 프롬프트 수정
    @Override
    public List<String> getDetailedCategories(CardHistoryFilterDTO cardHistoryFilterDTO) {

        List<CardHistoryDTO> cardHistoryList = getCardHistoryContentByCategory(cardHistoryFilterDTO);
        List<String> contents = cardHistoryList.stream()
                .map(CardHistoryDTO::getContent)
                .collect(Collectors.toList());

        String prompt = "content가 교통수단 관련이면 3번 이상 반복되는 교통수단을 명칭만 알려줘." +
                "그리고 dcontent가 카페 관련이면 3번이상 반복되는 카페이름을 알려줘. 이때 지점명은 제외해줘." +
                "그리고 해당하는 값들만 콤마로 나열해서 보내줘  " + String.join(", ", contents);

        String openAiResponse = openAiService.askOpenAi(prompt);

        return Arrays.asList(openAiResponse.split(", "));
    }
}
