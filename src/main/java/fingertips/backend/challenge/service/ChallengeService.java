package fingertips.backend.challenge.service;
import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;

import java.util.List;

public interface ChallengeService {

    List<ChallengeDTO> getChallengeList(Integer memberId);
    void insertChallenge(ChallengeDTO challengeDto);
    void deleteChallenge(int challengeId);
    List<CardHistoryDTO> getCardHistoryContentByCategory(Integer category);
    List<ProgressDTO> getChallengeStatus(Integer memberId);
    List<String> getDetailedCategories(Integer category);
}
