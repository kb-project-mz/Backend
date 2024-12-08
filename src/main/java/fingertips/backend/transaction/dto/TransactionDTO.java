package fingertips.backend.transaction.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionDTO {

    private Integer assetIdx;
    private String assetType;
    private String bankName;
    private String assetName;
    private String assetImage;
    private String transactionDate;
    private String transactionTime;
    private String transactionType;
    private String categoryName;
    private String transactionDescription;
    private Integer amount;
}
