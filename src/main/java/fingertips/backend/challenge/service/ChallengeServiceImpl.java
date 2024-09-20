package fingertips.backend.challenge.service;


import fingertips.backend.challenge.dto.ChallengeDto;
import fingertips.backend.challenge.mapper.ChallengeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChallengeServiceImpl implements ChallengeService
{
    private final ChallengeMapper challengeMapper;

    public ChallengeServiceImpl(ChallengeMapper challengeMapper)
    {
        this.challengeMapper = challengeMapper;
    }

    @Override
    public List<ChallengeDto> getList(ChallengeDto challengeDto)
    {
        return challengeMapper.challenge_getList(challengeDto);
    }

    @Override
    public void insert(ChallengeDto challengeDto)
    {
        challengeMapper.challenge_insert(challengeDto);
    }

    @Override
    public void delete(ChallengeDto challengeDto)
    {
        challengeMapper.challenge_delete(challengeDto);
    }


}
