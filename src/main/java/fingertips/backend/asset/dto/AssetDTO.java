package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

    // 카드면 1, 계좌면 2
    private int financeKind;
    private int prdtId;
    // 은행 이름
    private String financeName;
    // 상품명
    private String prdtName;
    private String image;
    private int totalAmount;
    // 연동 상태
    private boolean connStatus;
}