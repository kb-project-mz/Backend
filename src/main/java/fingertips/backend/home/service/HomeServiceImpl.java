package fingertips.backend.home.service;

import fingertips.backend.home.dto.*;
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
    private int memberIdx;

    @Override
    public void setMemberIdx(int memberIdx) {
        this.memberIdx = memberIdx;
    }

    @Override
    public List<BalanceDTO> getBalanceByMemberIdx(int memberIdx) {
        return homeMapper.getBalanceByMemberIdx(memberIdx);
    }

    public CompareAuthDTO getAuth(Integer memberIdx) { return homeMapper.getAuth(memberIdx); }


//    @Scheduled(fixedRate = 1000)
    public void checkForBalanceUpdates() {
       List<BalanceDTO> currentBalances = homeMapper.getBalanceByMemberIdx(memberIdx);
       CompareAuthDTO auth = homeMapper.getAuth(memberIdx);

       if (!currentBalances.equals(lastBalances)) {
           String balanceUrl = "http://localhost:3000/updateBalance";
           try {
               HttpHeaders headers = new HttpHeaders();
               headers.setContentType(MediaType.APPLICATION_JSON);
               headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

               HttpEntity<List<BalanceDTO>> entity = new HttpEntity<>(currentBalances, headers);
               restTemplate.postForEntity(balanceUrl, entity, String.class);

           } catch (Exception e) {
               System.out.println("Failed to send balance data to Node.js: " + e.getMessage());
           }
           lastBalances = currentBalances;
       } else {
           System.out.println("No balance changes detected.");
       }
       String authUrl = "http://localhost:3000/updateAuth";
       try {
           HttpHeaders headers = new HttpHeaders();
           headers.setContentType(MediaType.APPLICATION_JSON);
           headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");
           HttpEntity<CompareAuthDTO> authEntity = new HttpEntity<>(auth, headers);
           restTemplate.postForEntity(authUrl, authEntity, String.class);
       } catch (Exception e) {
           System.out.println("Failed to send auth data to Node.js: " + e.getMessage());
       }
    }

    @Override
    public List<HomeChallengeDTO> getChallengeByMemberIdx(Integer memberIdx) {
        return homeMapper.getChallengeByMemberIdx(memberIdx);
    }

    @Override
    public List<PeerChallengeDTO> getPeerChallenge(Integer memberIdx) {
        return homeMapper.getPeerChallenge(memberIdx);
    }

//    @Scheduled(fixedRate = 1000)
    public void updateChallengeStatus() {
       homeMapper.updateChallengeStatus();
    }

    @Override
    public TestDTO getTest(Integer memberIdx) {
       return homeMapper.getTest(memberIdx);
    }
}
