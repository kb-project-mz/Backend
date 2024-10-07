package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.dto.CompareAuthDTO;
import fingertips.backend.home.dto.HomeChallengeDTO;
import fingertips.backend.home.dto.PeerChallengeDTO;
import fingertips.backend.home.mapper.HomeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class HomeServiceImpl implements HomeService {

    private final HomeMapper homeMapper;

    @Autowired
    private final RestTemplate restTemplate;

    private List<BalanceDTO> lastBalances;

    // localStorage에서 받아온 memberIdx 전역변수로 저장
    private int memberIdx;

    @Override
    public void setMemberIdx(int memberIdx) {
        this.memberIdx = memberIdx;
    }


    @Override
    public List<BalanceDTO> getBalanceByMemberIdx(int memberIdx) {
        return homeMapper.getBalanceByMemberIdx(memberIdx);
    }


    @Override
    public CompareAuthDTO getAuth(Integer memberIdx) { return homeMapper.getAuth(memberIdx); }

    @Scheduled(fixedRate = 1000)
    public void checkForBalanceUpdates() {
        List<BalanceDTO> currentBalances = homeMapper.getBalanceByMemberIdx(memberIdx);
        CompareAuthDTO auth = homeMapper.getAuth(memberIdx);

        // Node.js 서버 url
        String socketUrl = "http://localhost:3000/update";

        // balance가 변화가 있는 경우에만 balance 전송 플래그 설정
        boolean balanceUpdated = !currentBalances.equals(lastBalances);

        // Node.js 서버로 balance와 auth 전송
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

            // Balance와 Auth를 같은 요청에 필드로 포함
            Map<String, Object> payload = new HashMap<>();
            payload.put("balance", balanceUpdated ? currentBalances : lastBalances); // balance가 변화가 있을 때만 포함
            payload.put("auth", auth); // auth는 항상 포함

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(socketUrl, entity, String.class);

            // balance 변화가 있을 경우 마지막 balance 업데이트
            if (balanceUpdated) {
                lastBalances = currentBalances;
            }
        } catch (Exception e) {
            System.out.println("Failed to send data to Node.js: " + e.getMessage());
        }
    }

    // 챌린지 받아오기
    @Override
    public List<HomeChallengeDTO> getChallengeByMemberIdx(Integer memberIdx) {
        return homeMapper.getChallengeByMemberIdx(memberIdx);
    }

    // 또래 챌린지 가져오기
    @Override
    public List<PeerChallengeDTO> getPeerChallenge(Integer memberIdx) {
        return homeMapper.getPeerChallenge(memberIdx);
    }

//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(fixedRate = 1000)
    public void updateChallengeStatus() {
        homeMapper.updateChallengeStatus();
    }

}
