package fingertips.backend.consumption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardConsumptionDTO {

    private String bankName;
    private String cardName;
    private String cardImage;
    private String consumptionDate;
    private String consumptionTime;
    private String category;
    private String content;
    private int amount;
    private int cardId;
}
