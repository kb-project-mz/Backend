package fingertips.backend.data.dto;

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
public class DataDTO {
    private int accountTransactionId;
    private int accountIdx;
    private LocalDate accountTransactionDate;
    private LocalTime accountTransactionTime;
    private String accountTransactionType;
    private int categoryIdx;
    private String accountTransactionDescription;
    private int amount;
}
