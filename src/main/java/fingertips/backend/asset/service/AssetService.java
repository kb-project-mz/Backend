package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetService {

    List<AssetDTO> getAllAssets(Integer id);
    void connectCard(Integer id);
    void connectAccount(Integer id);
}
