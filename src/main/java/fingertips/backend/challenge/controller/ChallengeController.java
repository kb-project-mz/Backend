package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardHIstoryDTO;
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

    @GetMapping("/list/{memberId}")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getList(@PathVariable Integer memberId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", challengeService.getList(memberId));
        return ResponseEntity.ok().body(JsonResponse.success(map));
//        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> write(@RequestBody ChallengeDTO dto) {

        challengeService.insert(dto);
        HashMap<String, Object> map = new HashMap<>();
//        map.put("result", "success");3
//        return new ResponseEntity<>(map, HttpStatus.OK);
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

    @PostMapping("/delete")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> delete(@RequestBody ChallengeDTO dto) {

        challengeService.delete(dto);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
//        return new ResponseEntity<>(map, HttpStatus.OK);
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

//    @PostMapping("/ask")
//    public ResponseEntity<String> askOpenAi(@RequestBody Map<String, String> request) {
//
//        String prompt = request.get("prompt");
//        String response = openAiService.askOpenAi(prompt);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/detailedCategory")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getDetailedCategories(@RequestBody CardHIstoryDTO dto) {
        // 카테고리에 맞는 카드 기록의 내용을 가져옴
        List<CardHIstoryDTO> cardHistoryList = challengeService.getList_card(dto);

        // 가져온 기록에서 content 값만 추출
        List<String> contents = cardHistoryList.stream()
                .map(CardHIstoryDTO::getContent)
                .collect(Collectors.toList());

        // OpenAI에 질문할 내용 생성
        String prompt = "content가 교통수단 관련이면 3번 이상 반복되는 교통수단을 명칭만 알려줘. 그리고 dcontent가 카페 관련이면 3번이상 반복되는 카페이름을 알려줘. 이때 지점명은 제외해줘. 그리고 해당하는 값들만 콤마로 나열해서 보내줘  " + String.join(", ", contents);

        // OpenAI에 질문 전송 및 응답 받기
        String openAiResponse = openAiService.askOpenAi(prompt);

        // 응답 데이터를 리스트로 변환
        List<String> detailedCategories = Arrays.asList(openAiResponse.split(", "));

        // 결과를 담은 맵 생성 및 반환
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


