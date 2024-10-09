package fingertips.backend.home.service;

import fingertips.backend.home.dto.*;

import java.util.List;

public interface HomeService {

    // 계좌 총 잔액 받아오기
    List<BalanceDTO> getBalanceByMemberIdx(int memberIdx);

    // localStorage에서 받아온 memberIdx 전역변수로 저장하기 위한 setter 메소드
    void setMemberIdx(int memberIdx);

    // 챌린지 목록 가져오기
    List<HomeChallengeDTO> getChallengeByMemberIdx(Integer memberIdx);

    // 또래 챌린지 가져오기
    List<PeerChallengeDTO> getPeerChallenge(Integer memberIdx);

    // 로그인한 사용자와 DB 비교
    CompareAuthDTO getAuth(Integer memberIdx);

    TestDTO getTest(Integer memberIdx);

}
