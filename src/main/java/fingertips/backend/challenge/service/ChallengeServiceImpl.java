package fingertips.backend.challenge.service;


import fingertips.backend.challenge.dto.CardHIstoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.mapper.ChallengeMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Service
public class ChallengeServiceImpl implements ChallengeService
{
    private final ChallengeMapper challengeMapper;

    public ChallengeServiceImpl(ChallengeMapper challengeMapper)
    {
        this.challengeMapper = challengeMapper;
    }

    @Override
    public List<ChallengeDTO> getList(Integer memberId)
    {
        return challengeMapper.challenge_getList(memberId);
    }

    @Override
    public void insert(ChallengeDTO challengeDto)
    {
        challengeMapper.challenge_insert(challengeDto);
    }


    @Override
    public void delete(ChallengeDTO challengeDto)
    {
        challengeMapper.challenge_delete(challengeDto);
    }

    @Override
    public List<CardHIstoryDTO> getList_card(CardHIstoryDTO cardDto)
    {
        return challengeMapper.cardHistory_getList(cardDto);
    }

    @Override
    public List<ProgressDTO> getChallengeLimitAndCardHistoryCount(Integer memberId) {
        return challengeMapper.getChallengeLimitAndCardHistoryCount(memberId);
    }
}
