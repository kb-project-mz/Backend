package fingertips.backend.home.service;

import fingertips.backend.home.dto.*;

import java.util.List;

public interface HomeService {


    List<BalanceDTO> getBalanceByMemberIdx(int memberIdx);
    void setMemberIdx(int memberIdx);
    List<HomeChallengeDTO> getChallengeByMemberIdx(Integer memberIdx);
    List<PeerChallengeDTO> getPeerChallenge(Integer memberIdx);
    CompareAuthDTO getAuth(Integer memberIdx);
    TestDTO getTest(Integer memberIdx);

}
