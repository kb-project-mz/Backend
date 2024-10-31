package fingertips.backend.carddata.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDataDTO {
    private int cardTransactionIdx;
    private int cardIdx;
    private LocalDate cardTransactionDate;
    private LocalTime cardTransactionTime;
    private int categoryIdx;
    private String cardTransactionDescription;
    private int amount;
}
