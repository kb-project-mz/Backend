package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetService {

    List<AssetDTO> getAllAssets(Integer memberIdx);
    void connectCard(Integer cardIdx);
    void connectAccount(Integer accountIdx);
    void disconnectCard(Integer cardIdx);
    void disconnectAccount(Integer accountIdx);
}
