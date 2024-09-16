package fingertips.backend.openai.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {

    private String role;
    private String content;
}
