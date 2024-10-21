package fingertips.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceDTO {

    private int accountIdx;
    private int memberIdx;
    private String bankName;
    private String accountName;
    private int balance;

}
