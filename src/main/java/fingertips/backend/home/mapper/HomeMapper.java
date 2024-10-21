package fingertips.backend.home.mapper;

import fingertips.backend.home.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {

    List<BalanceDTO> getBalanceByMemberIdx(int memberIdx);
    List<HomeChallengeDTO> getChallengeByMemberIdx(Integer memberIdx);
    List<PeerChallengeDTO> getPeerChallenge(Integer memberIdx);
    void updateChallengeStatus();
    CompareAuthDTO getAuth(Integer memberIdx);
    TestDTO getTest(Integer memberIdx);
}
