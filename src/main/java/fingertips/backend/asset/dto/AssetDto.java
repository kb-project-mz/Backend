package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDto {
    // 카드면
    private int financeKind;
    private String financeName;
    private String prdtName;
    private String image;
    private int totalAmount;
    private boolean connStatus;
}
