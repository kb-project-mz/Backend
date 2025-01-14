package fingertips.backend.home.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.dto.HomeChallengeDTO;
import fingertips.backend.home.dto.PeerChallengeDTO;
import fingertips.backend.home.dto.TestDTO;
import fingertips.backend.home.service.HomeService;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final HomeService homeService;
    private final JwtProcessor jwtProcessor;

    @PostMapping("/balance")
    public ResponseEntity<JsonResponse<List<BalanceDTO>>> getBalanceByMemberIdx(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        homeService.setMemberIdx(memberIdx);
        List<BalanceDTO> balanceByMemberIdx = homeService.getBalanceByMemberIdx(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(balanceByMemberIdx));
    }

    @GetMapping("/challenge")
    public ResponseEntity<JsonResponse<List<HomeChallengeDTO>>> getChallengeByMemberIdx(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<HomeChallengeDTO> challengeByMemberIdx = homeService.getChallengeByMemberIdx(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(challengeByMemberIdx));
    }

    @GetMapping("/peerChallenge")
    public ResponseEntity<JsonResponse<List<PeerChallengeDTO>>> getPeerChallenge(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<PeerChallengeDTO> peerChallengeList = homeService.getPeerChallenge(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(peerChallengeList));
    }

    @GetMapping("/test")
    public ResponseEntity<JsonResponse<TestDTO>> getTest(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        TestDTO testResult = homeService.getTest(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(testResult));
    }

}
