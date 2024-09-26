package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.mapper.BalanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService{

    private final BalanceMapper balanceMapper;

    @Override
    public List<BalanceDTO> getBalance(String memberId) {
      return balanceMapper.getBalance(memberId);
    }

}
