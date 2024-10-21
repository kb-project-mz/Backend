package fingertips.backend.openai.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiResponse {

    private List<ChoiceDTO> choices;
}
