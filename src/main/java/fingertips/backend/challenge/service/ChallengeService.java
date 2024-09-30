package fingertips.backend.challenge.service;
import fingertips.backend.challenge.dto.CardTransactionDTO2;
import fingertips.backend.challenge.dto.CardTransactionFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;

import java.util.List;

public interface ChallengeService {

    List<ChallengeDTO> getChallengeList(Integer memberIdx);
    void insertChallenge(ChallengeDTO challengeDto);
    void deleteChallenge(Integer challengeId);
    List<CardTransactionDTO2> getCardHistoryContentByCategory(CardTransactionFilterDTO cardHistoryFilterDTO);
    List<ProgressDTO> getChallengeStatus(Integer memberIdx);
    List<String> getDetailedCategories(CardTransactionFilterDTO cardHistoryFilterDTO);
}
