package fingertips.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardTransactionDTO {

    private Integer cardIdx;
    private String bankName;
    private String cardName;
    private String cardImage;
    private String cardTransactionDate;
    private String cardTransactionTime;
    private String categoryName;
    private String cardTransactionDescription;
    private Integer amount;
}
