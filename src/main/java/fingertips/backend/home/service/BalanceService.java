package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BalanceService {

    // 계좌 총 잔액 받아오기
    List<BalanceDTO> getBalanceByMemberIdx(int memberIdx);

    // localStorage에서 받아온 memberIdx 전역변수로 저장하기 위한 setter 메소드
    void setMemberIdx(int memberIdx);
}
