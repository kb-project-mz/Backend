package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

//    private Integer financeKind;
//    private Integer prdtId;
//    private String financeName;
//    private String prdtName;
//    private String image;
//    private Integer totalAmount;
//    private Integer connectedStatus;
//    private Integer balance;
    private Integer assetIdx;
    private String assetType;         // 'card' 또는 'account'
    private String bankName;          // 금융사 이름
    private String assetName;         // 자산 이름
    private String assetImage;        // 이미지 URL
    private Integer connectedStatus;  // 연결 상태
}
