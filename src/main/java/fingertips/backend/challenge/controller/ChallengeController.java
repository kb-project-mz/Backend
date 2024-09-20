package fingertips.backend.challenge.controller;

import fingertips.backend.challenge.dto.ChallengeDto;
import fingertips.backend.challenge.service.ChallengeService;
import fingertips.backend.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final OpenAiService openAiService;

    @GetMapping("/list")
    public ResponseEntity<HashMap<String, Object>> getList(ChallengeDto dto)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("data", challengeService.getList(dto));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity<HashMap<String, Object>> write(@RequestBody ChallengeDto dto)
    {

        challengeService.insert(dto);
        System.out.println("카테고리 id: " + dto.getCategory());
        System.out.println("카테고리 id: " + dto.getCategory());
        System.out.println("카테고리 id: " + dto.getCategory());
        System.out.println("카테고리 id: " + dto.getCategory());
        System.out.println("카테고리 id: " + dto.getCategory());
        System.out.println("카테고리 id: " + dto.getCategory());



        HashMap<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<HashMap<String, Object>> delete(@RequestBody ChallengeDto dto)
    {
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
        System.out.println("삭제할 챌린지 ID: " + dto.getId());
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



}
