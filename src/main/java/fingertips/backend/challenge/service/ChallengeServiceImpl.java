package fingertips.backend.challenge.service;


import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.mapper.ChallengeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeMapper challengeMapper;

    @Override
    public List<ChallengeDTO> getChallengeList(Integer memberId)
    {
        return challengeMapper.getChallengeList(memberId);
    }

    @Override
    public void insertChallenge(ChallengeDTO challengeDto)
    {
        challengeMapper.insertChallenge(challengeDto);
    }

    @Override
    public void deleteChallenge(int challengeId)
    {
        challengeMapper.deleteChallenge(challengeId);
    }

    @Override
    public List<CardHistoryDTO> getCardHistoryByCategory(CardHistoryDTO cardDto) {
        return challengeMapper.getCardHistoryByCategory(cardDto);
    }

    @Override
    public List<ProgressDTO> getChallengeLimitAndCardHistoryCount(Integer memberId) {
        return challengeMapper.getChallengeLimitAndCardHistoryCount(memberId);
    }
}
