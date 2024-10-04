package fingertips.backend.home.service;

import fingertips.backend.home.dto.BalanceDTO;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
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

    @Scheduled(fixedRate = 1000)
    public void checkForBalanceUpdates() {
        boolean update_flag = false;
        List<BalanceDTO> currentBalances = homeMapper.getBalanceByMemberIdx(memberIdx);
        // db의 balance가 변화가 있다면 실행
        if (!currentBalances.equals(lastBalances)) {

            // Node.js 서버 url
            String socketUrl = "http://localhost:3000/update";
//  update랑 select를 분리 update
//  update
//  select
//  한명만 update > select
//  나머지는 그냥 select
            // Node.js 서버로 업데이트 전송
            try {
                //인코딩 후 node.js로 데이터 보내기
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

                HttpEntity<List<BalanceDTO>> entity = new HttpEntity<>(currentBalances, headers);
                if(update_flag) {
                    ResponseEntity<String> response = restTemplate.postForEntity(socketUrl, entity, String.class);
                }
            } catch (Exception e) {
                // Node.js로 데이터 보내기 실패
                System.out.println("Failed to send data to Node.js: " + e.getMessage());
            }

            lastBalances = currentBalances;
        } else {
            // db의 balance가 변화가 없다면 실행
            System.out.println("No balance changes detected.");
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
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        log.info("Updating challenge status...");
        homeMapper.updateChallengeStatus();
    }

}
