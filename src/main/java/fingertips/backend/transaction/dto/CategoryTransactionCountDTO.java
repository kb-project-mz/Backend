package fingertips.backend.transaction.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryTransactionCountDTO {

    private String categoryName;
    private Integer totalSpent;
    private Double percentage;
}
