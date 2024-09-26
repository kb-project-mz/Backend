package fingertips.backend.challenge.mapper;

import fingertips.backend.challenge.dto.CardHIstoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChallengeMapper
{
    List<ChallengeDTO> challenge_getList(int memberId);
    void challenge_insert(ChallengeDTO dto);
    void challenge_delete(ChallengeDTO dto);

    List<CardHIstoryDTO> cardHistory_getList(CardHIstoryDTO dto);
    List<ProgressDTO> getChallengeLimitAndCardHistoryCount(Integer memberId);
}
