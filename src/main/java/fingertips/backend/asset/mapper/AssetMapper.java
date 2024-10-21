package fingertips.backend.asset.mapper;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    List<AssetDTO> getAllAssets(Integer memberIdx);
    void connectCard(Integer cardIdx);
    void connectAccount(Integer accountIdx);
    void disconnectCard(Integer cardIdx);
    void disconnectAccount(Integer accountIdx);
}
