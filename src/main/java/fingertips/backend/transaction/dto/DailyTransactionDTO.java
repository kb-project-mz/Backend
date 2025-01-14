package fingertips.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyTransactionDTO {
    private String transactionDateTime;
    private String transactionDescription;
    private String assetName;
    private String transactionType;
    private String formattedAmount;
}
