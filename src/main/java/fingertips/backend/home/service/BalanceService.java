package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BalanceService {
    // 계좌 총 잔액 받아오기
    List<BalanceDTO> getBalance(int member_index);
}
