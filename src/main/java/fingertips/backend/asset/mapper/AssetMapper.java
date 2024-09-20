package fingertips.backend.asset.mapper;

import fingertips.backend.asset.dto.AssetDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssetMapper {

    List<AssetDto> getAllAssets(@Param("memberId") int memberId);
}
