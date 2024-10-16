package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

    private Integer financeKind;
    private Integer prdtId;
    private String financeName;
    private String prdtName;
    private String image;
    private Integer totalAmount;
    private Integer connectedStatus;
    private Integer balance;
}
