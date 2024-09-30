package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

    private Integer financeKind; // 카드 1, 계좌 2
    private Integer prdtId;
    private String financeName; // 은행 이름
    private String prdtName; // 상품명
    private String image;
    private Integer totalAmount;
    private Integer connectedStatus;
}
