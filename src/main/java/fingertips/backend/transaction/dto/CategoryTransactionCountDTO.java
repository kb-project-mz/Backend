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

    private String categoryName;  // 카테고리 이름
    private int transactionCount; // 거래 건수
    private double percentage;    // 카테고리별 거래 비율
}
