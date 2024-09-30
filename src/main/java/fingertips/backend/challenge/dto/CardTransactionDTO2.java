package fingertips.backend.challenge.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDTO2 {

    private Integer cardTransactionIdx;
    private Integer cardIdx;
    private String cardTransactionDate;
    private String cardTransactionTime;
    private Integer categoryIdx;
    private String cardTransactionDescription;
    private Integer amount;
}
