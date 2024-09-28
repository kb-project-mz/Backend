package fingertips.backend.consumption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountConsumptionDTO {

    private String bankName;
    private String accountName;
    private String accountBookImage;
    private String accountDate;
    private String accountTime;
    private String type;
    private String category;
    private String content;
    private Integer amount;
}
