package fingertips.backend.asset.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetDTO {

    private Integer assetIdx;
    private String assetType;
    private String bankName;
    private String assetName;
    private String assetImage;
    private Integer connectedStatus;
    private Integer balance;
}
