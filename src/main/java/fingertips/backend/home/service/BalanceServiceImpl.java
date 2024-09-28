package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.mapper.BalanceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceMapper balanceMapper;
    @Autowired
    private final RestTemplate restTemplate;

    private List<BalanceDTO> lastBalances;

    @Override
    public List<BalanceDTO> getBalance(int member_index) {
        return balanceMapper.getBalance(member_index);
    }

    @Scheduled(fixedRate = 5000)  // 5초마다 실행
    public void checkForBalanceUpdates() {
        List<BalanceDTO> currentBalances = balanceMapper.getBalance(1); // 예시로 member_index=1
        if (!currentBalances.equals(lastBalances)) {
            System.out.println("Balance changed, sending update to Node.js server");

            // Node.js 서버로 업데이트 전송
            String socketUrl = "http://localhost:3000/update";
            // Node.js 서버로 업데이트 전송
            try {
                //인코딩 진행
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

                HttpEntity<List<BalanceDTO>> entity = new HttpEntity<>(currentBalances, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(socketUrl, entity, String.class);
                System.out.println("Data sent to Node.js successfully: " + currentBalances);
            } catch (Exception e) {
                System.out.println("Failed to send data to Node.js: " + e.getMessage());
            }

            lastBalances = currentBalances;  // 마지막 상태 업데이트
        } else {
            System.out.println("No balance changes detected.");
        }
    }
}
