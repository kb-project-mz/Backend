package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetService {

    List<AssetDTO> getAllAssets(int id);
    void connectCard(int id);
    void connectAccount(int id);
}
