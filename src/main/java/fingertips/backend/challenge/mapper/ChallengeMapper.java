package fingertips.backend.challenge.mapper;

import fingertips.backend.challenge.dto.CardHIstoryDto;
import fingertips.backend.challenge.dto.ChallengeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChallengeMapper
{
    List<ChallengeDto> challenge_getList(ChallengeDto dto);
    void challenge_insert(ChallengeDto dto);
    void challenge_delete(ChallengeDto dto);
    List<CardHIstoryDto> cardHistory_getList(CardHIstoryDto dto);
}
