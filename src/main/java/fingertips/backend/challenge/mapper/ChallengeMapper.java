package fingertips.backend.challenge.mapper;

import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.CardHistoryFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeMapper {

    List<ChallengeDTO> getChallengeList(Integer memberId);
    void insertChallenge(ChallengeDTO dto);
    void deleteChallenge(Integer challengeId);
    List<CardHistoryDTO> getCardHistoryContentByCategory(CardHistoryFilterDTO cardHistoryFilterDTO);
    List<ProgressDTO> getChallengeStatus(Integer memberId);
}
