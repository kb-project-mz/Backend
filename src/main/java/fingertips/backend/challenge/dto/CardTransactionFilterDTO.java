package fingertips.backend.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionFilterDTO {

    private Integer memberIdx;
    private Integer categoryIdx;
}
