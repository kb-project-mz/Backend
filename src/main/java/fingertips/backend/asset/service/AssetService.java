package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetService {

    List<AssetDTO> getAllAssets(Integer memberIdx);
    void connectCard(Integer assetIdx);
    void connectAccount(Integer assetIdx);
    void disconnectCard(Integer assetIdx);
    void disconnectAccount(Integer assetIdx);
}
