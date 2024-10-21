package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardTransactionFilterDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.service.ChallengeService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.security.util.JwtProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value="/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final JwtProcessor jwtProcessor;

    @GetMapping("")
    public ResponseEntity<JsonResponse<List<ChallengeDTO>>> getChallengeList(@RequestHeader("Authorization") String token) {

        Integer memberIdx = jwtProcessor.getMemberIdx(token);

        List<ChallengeDTO> response = challengeService.getChallengeList(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @PostMapping("")
    public ResponseEntity<JsonResponse<String>> insertChallenge(@RequestBody ChallengeDTO dto) {
        challengeService.insertChallenge(dto);
        return ResponseEntity.ok().body(JsonResponse.success("Create Challenge Success"));
    }

    @PostMapping("/{challengeIdx}")
    public ResponseEntity<JsonResponse<String>> deleteChallenge(@PathVariable Integer challengeIdx) {
        challengeService.deleteChallenge(challengeIdx);
        return ResponseEntity.ok().body(JsonResponse.success("Delete Challenge Success"));
    }

    @GetMapping("/detailed-category/{memberIdx}/{category}")
    public ResponseEntity<JsonResponse<List<String>>> getDetailedCategories(@PathVariable Integer memberIdx,
                                                                            @PathVariable Integer category) {
        CardTransactionFilterDTO cardHistoryFilterDTO = CardTransactionFilterDTO.builder()
                .memberIdx(memberIdx)
                .categoryIdx(category)
                .build();

        List<String> response = challengeService.getDetailedCategories(cardHistoryFilterDTO);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/status")
    public ResponseEntity<JsonResponse<List<ProgressDTO>>> getChallengeStatus(@RequestHeader("Authorization") String token) {

        Integer memberIdx = jwtProcessor.getMemberIdx(token);

        List<ProgressDTO> response = challengeService.getChallengeStatus(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/peer")
    public ResponseEntity<JsonResponse<List<ChallengeDTO>>> getPeerChallengeList(@RequestHeader("Authorization") String token) {

        Integer memberIdx = jwtProcessor.getMemberIdx(token);

        List<ChallengeDTO> response = challengeService.getPeerChallengeList(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
