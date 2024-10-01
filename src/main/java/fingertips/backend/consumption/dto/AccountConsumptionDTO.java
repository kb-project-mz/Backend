package fingertips.backend.consumption.dto;

import lombok.*;

@Data
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
