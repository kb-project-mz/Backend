package fingertips.backend.openai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceDTO {

    private int index;
    private MessageDTO message;
}
