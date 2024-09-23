package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.CardHIstoryDto;
import fingertips.backend.challenge.dto.ChallengeDto;
import fingertips.backend.challenge.service.ChallengeService;
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
    public ResponseEntity<HashMap<String, Object>> getList(ChallengeDto dto) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", challengeService.getList(dto));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<HashMap<String, Object>> write(@RequestBody ChallengeDto dto) {

        challengeService.insert(dto);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<HashMap<String, Object>> delete(@RequestBody ChallengeDto dto) {

        challengeService.delete(dto);
        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askOpenAi(@RequestBody Map<String, String> request) {

        String prompt = request.get("prompt");
        String response = openAiService.askOpenAi(prompt);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/detailedCategory")
    public ResponseEntity<List<String>> getDetailedCategories(@RequestBody CardHIstoryDto dto) {
        // 카테고리에 맞는 카드 기록의 내용을 가져옴
        List<CardHIstoryDto> cardHistoryList = challengeService.getList_card(dto);

        System.out.println(cardHistoryList);
        System.out.println(cardHistoryList);
        System.out.println(cardHistoryList);
        System.out.println(cardHistoryList);
        System.out.println(cardHistoryList);

        // 가져온 기록에서 content 값만 추출
        List<String> contents = cardHistoryList.stream()
                .map(CardHIstoryDto::getContent)
                .collect(Collectors.toList());

        // OpenAI에 질문을 생성할 prompt 내용 준비
        String prompt = "다음 항목 중에서 3번 이상 반복된 항목들을 설명해줘: " + String.join(", ", contents);

        // OpenAI에 질문을 전송하고 응답 받기
        String openAiResponse = openAiService.askOpenAi(prompt);

        // 프론트에 응답으로 반환
        return ResponseEntity.ok(List.of(openAiResponse));
    }
}


