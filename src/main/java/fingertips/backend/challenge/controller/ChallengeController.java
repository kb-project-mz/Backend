package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardHistoryDTO;
import fingertips.backend.challenge.dto.CardHistoryFilterDTO;
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

    @GetMapping("/{memberIdx}")
    public ResponseEntity<JsonResponse<List<ChallengeDTO>>> getChallengeList(@PathVariable Integer memberIdx) {

        List<ChallengeDTO> response = challengeService.getChallengeList(memberIdx);
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

    @GetMapping("/detailed-category/{memberIdx}/{category}")
    public ResponseEntity<JsonResponse<List<String>>> getDetailedCategories(@PathVariable Integer memberIdx,
                                                                            @PathVariable Integer category) {

        CardHistoryFilterDTO cardHistoryFilterDTO = CardHistoryFilterDTO.builder()
                .memberId(memberIdx)
                .category(category)
                .build();

        List<String> response = challengeService.getDetailedCategories(cardHistoryFilterDTO);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }

    @GetMapping("/status/{memberIdx}")
    public ResponseEntity<JsonResponse<List<ProgressDTO>>> getChallengeStatus(@PathVariable Integer memberIdx) {

        List<ProgressDTO> response = challengeService.getChallengeStatus(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(response));
    }
}
