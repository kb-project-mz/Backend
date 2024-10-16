package fingertips.backend.openai.service;

import fingertips.backend.openai.dto.MessageDTO;
import fingertips.backend.openai.dto.OpenAiRequest;
import fingertips.backend.openai.dto.OpenAiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api.model}")
    private String model;
    private final RestTemplate openAiRestTemplate;

    public String askOpenAi(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        MessageDTO message = MessageDTO.builder()
                .role("user")
                .content(prompt)
                .build();

        List<MessageDTO> messageList = new ArrayList<>();
        messageList.add(message);

        OpenAiRequest request = OpenAiRequest.builder()
                .model(model)
                .messages(messageList)
                .maxTokens(200)
                .build();

        OpenAiResponse response = openAiRestTemplate.postForObject(url, request, OpenAiResponse.class);

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            log.info(response.getChoices().get(0).getMessage().getContent());
            return response.getChoices().get(0).getMessage().getContent();
        } else {
            throw new RuntimeException("OpenAI API 호출 실패 또는 응답 없음");
        }
    }
}
