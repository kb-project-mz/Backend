package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardHIstoryDto;
import fingertips.backend.challenge.dto.ChallengeDto;
import fingertips.backend.challenge.service.ChallengeService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value="/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final OpenAiService openAiService;

    @GetMapping("/list")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getList(ChallengeDto dto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", challengeService.getList(dto));
        return ResponseEntity.ok().body(JsonResponse.success(map));
//        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> write(@RequestBody ChallengeDto dto) {

        challengeService.insert(dto);
        HashMap<String, Object> map = new HashMap<>();
//        map.put("result", "success");3
//        return new ResponseEntity<>(map, HttpStatus.OK);
        return ResponseEntity.ok().body(JsonResponse.success(map));
    }

    @PostMapping("/delete")
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> delete(@RequestBody ChallengeDto dto) {

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
    public ResponseEntity<JsonResponse<HashMap<String, Object>>> getDetailedCategories(@RequestBody CardHIstoryDto dto) {
        // 카테고리에 맞는 카드 기록의 내용을 가져옴
        List<CardHIstoryDto> cardHistoryList = challengeService.getList_card(dto);

        System.out.println(cardHistoryList);

        // 가져온 기록에서 content 값만 추출
        List<String> contents = cardHistoryList.stream()
                .map(CardHIstoryDto::getContent)
                .collect(Collectors.toList());

        // OpenAI에 질문을 생성할 prompt 내용 준비
        String prompt = "dto의 category가 3번이면 3번 이상 반복되는 교통수단을 명칭만 알려줘. 그리고 dto의 category가 2번이면 3번이상 반복되는 카페이름을 알려줘. 이때 지점명은 제외해줘 " + String.join(", ", contents);


        // OpenAI에 질문을 전송하고 응답 받기
        String openAiResponse = openAiService.askOpenAi(prompt);

        // 결과를 담을 맵 생성
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("response", openAiResponse);
//        System.out.println(resultMap);

        // 성공 시 프론트에 응답으로 반환
        return ResponseEntity.ok(JsonResponse.success(resultMap));
    }
}


