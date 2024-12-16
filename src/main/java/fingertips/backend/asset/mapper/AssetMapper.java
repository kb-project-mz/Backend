package fingertips.backend.asset.mapper;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    List<AssetDTO> getAllAssets(Integer memberIdx);
    void connectCard(Integer assetIdx);
    void connectAccount(Integer assetIdx);
    void disconnectCard(Integer assetIdx);
    void disconnectAccount(Integer assetIdx);
}
