package fingertips.backend.challenge.service;
import fingertips.backend.challenge.dto.CardHIstoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;

import java.util.List;
import java.util.Map;

public interface ChallengeService
{
    List<ChallengeDTO> getList(Integer memberId);
    void insert(ChallengeDTO challengeDto);
    void delete(ChallengeDTO dto);

    List<CardHIstoryDTO> getList_card(CardHIstoryDTO cardDto);

    List<ProgressDTO> getChallengeLimitAndCardHistoryCount(Integer memberId);
}
