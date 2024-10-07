package fingertips.backend.transaction.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MostSpentCategoryDTO {
    private String categoryName;
    private Integer totalSpent;
    private String transactionDate;
}
