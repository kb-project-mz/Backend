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
    private final OpenAiService openAiService;

    @GetMapping("/{memberId}")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getList(@PathVariable Integer memberId) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("data", challengeService.getChallengeList(memberId));
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

    @PostMapping("")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> write(@RequestBody ChallengeDTO dto) {

        challengeService.insertChallenge(dto);
        HashMap<String, Object> map = new HashMap<>();
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> delete(@PathVariable Integer challengeId) {

        challengeService.deleteChallenge(challengeId);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

    @PostMapping("/detailedCategory")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getDetailedCategories(@RequestBody CardHistoryDTO dto) {

        List<CardHistoryDTO> cardHistoryList = challengeService.getCardHistoryByCategory(dto);

        List<String> contents = cardHistoryList.stream()
                .map(CardHistoryDTO::getContent)
                .collect(Collectors.toList());

        String prompt = "content가 교통수단 관련이면 3번 이상 반복되는 교통수단을 명칭만 알려줘." +
                "그리고 dcontent가 카페 관련이면 3번이상 반복되는 카페이름을 알려줘. 이때 지점명은 제외해줘." +
                "그리고 해당하는 값들만 콤마로 나열해서 보내줘  " + String.join(", ", contents);

        String openAiResponse = openAiService.askOpenAi(prompt);

        List<String> detailedCategories = Arrays.asList(openAiResponse.split(", "));

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("detailedCategories", detailedCategories);

        return ResponseEntity.ok(JsonResponse.success(resultMap));
    }

//    @GetMapping("/donutchart")
//    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getList_donutchart() {
//        HashMap<String, Object> map = new HashMap<>();
//
//        // 진행 값과 전체 값을 직접 설정 (예시로 진행값 30과 전체값 100)
//        int completed = 30;  // 진행된 값
//        int total = 100;     // 전체 값
//
//        // 진행 값과 전체 값을 맵에 추가
//        map.put("completed", completed);
//        map.put("total", total);
//
//        // JSON 응답으로 반환
//        return ResponseEntity.ok().body(JsonResponse.success(map));
//    }

    @GetMapping("/challenge-limit-card-count/{memberId}")
    public ResponseEntity<JsonResponse<List<ProgressDTO>>> getChallengeLimitAndCardHistoryCount(@PathVariable Integer memberId) {

        List<ProgressDTO> resultList = challengeService.getChallengeLimitAndCardHistoryCount(memberId);
        return ResponseEntity.ok().body(JsonResponse.success(resultList));
    }
}
