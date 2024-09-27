package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.ChallengeDTO;
import fingertips.backend.challenge.dto.ProgressDTO;
import fingertips.backend.challenge.service.ChallengeService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/{memberId}")
    public ResponseEntity<JsonResponse<List<ChallengeDTO>>> getChallengeList(@PathVariable Integer memberId) {

        List<ChallengeDTO> response = challengeService.getChallengeList(memberId);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @PostMapping("")
    public ResponseEntity<JsonResponse<String>> insertChallenge(@RequestBody ChallengeDTO dto) {

        challengeService.insertChallenge(dto);
        return ResponseEntity.ok().body(JsonResponse.success("Create Challenge Success"));
    }

    @PostMapping("/{challengeId}")
    public ResponseEntity<JsonResponse<String>> deleteChallenge(@PathVariable Integer challengeId) {

        challengeService.deleteChallenge(challengeId);
        return ResponseEntity.ok().body(JsonResponse.success("Delete Challenge Success"));
    }

    @GetMapping("/detailed-category/{category}")
    public ResponseEntity<JsonResponse<List<String>>> getDetailedCategories(@PathVariable Integer category) {

        List<String> response = challengeService.getDetailedCategories(category);
        return ResponseEntity.ok(JsonResponse.success(response));
    }

    @GetMapping("/status/{memberId}")
    public ResponseEntity<JsonResponse<List<ProgressDTO>>> getChallengeStatus(@PathVariable Integer memberId) {

        List<ProgressDTO> response = challengeService.getChallengeStatus(memberId);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
