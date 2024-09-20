package fingertips.backend.challenge.service;
import fingertips.backend.challenge.dto.ChallengeDto;

import java.util.List;

public interface ChallengeService
{
    List<ChallengeDto> getList(ChallengeDto challengeDto);
    void insert(ChallengeDto challengeDto);
    void delete(ChallengeDto dto);

}