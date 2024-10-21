package fingertips.backend.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountTransactionDTO {

    private String bankName;
    private String accountName;
    private String accountImage;
    private String accountTransactionDate;
    private String accountTransactionTime;
    private String accountTransactionType;
    private String categoryName;
    private String accountTransactionDescription;
    private Integer amount;
}
