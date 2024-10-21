package fingertips.backend.home.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.dto.HomeChallengeDTO;
import fingertips.backend.home.dto.PeerChallengeDTO;
import fingertips.backend.home.dto.TestDTO;
import fingertips.backend.home.service.HomeService;
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

    @PostMapping("/balance/{memberIdx}")
    public ResponseEntity<JsonResponse<List<BalanceDTO>>> getBalanceByMemberIdx(@PathVariable int memberIdx) {
        homeService.setMemberIdx(memberIdx);
        List<BalanceDTO> balanceByMemberIdx = homeService.getBalanceByMemberIdx(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(balanceByMemberIdx));
    }

    @GetMapping("/challenge/{memberIdx}")
    public ResponseEntity<JsonResponse<List<HomeChallengeDTO>>> getChallengeByMemberIdx(@PathVariable Integer memberIdx) {
        List<HomeChallengeDTO> challengeByMemberIdx = homeService.getChallengeByMemberIdx(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(challengeByMemberIdx));
    }

    @GetMapping("/peerChallenge/{memberIdx}")
    public ResponseEntity<JsonResponse<List<PeerChallengeDTO>>> getPeerChallenge(@PathVariable Integer memberIdx) {
        List<PeerChallengeDTO> peerChallengeList = homeService.getPeerChallenge(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(peerChallengeList));
    }

    @GetMapping("/test/{memberIdx}")
    public ResponseEntity<JsonResponse<TestDTO>> getTest(@PathVariable Integer memberIdx) {
        TestDTO testResult = homeService.getTest(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(testResult));
    }

}
