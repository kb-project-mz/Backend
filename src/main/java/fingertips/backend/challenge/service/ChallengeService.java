package fingertips.backend.challenge.service;
import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.CardHistoryFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;

import java.util.List;

public interface ChallengeService {

    List<ChallengeDTO> getChallengeList(Integer memberIdx);
    void insertChallenge(ChallengeDTO challengeDto);
    void deleteChallenge(Integer challengeId);
    List<CardHistoryDTO> getCardHistoryContentByCategory(CardHistoryFilterDTO cardHistoryFilterDTO);
    List<ProgressDTO> getChallengeStatus(Integer memberIdx);
    List<String> getDetailedCategories(CardHistoryFilterDTO cardHistoryFilterDTO);
}
