package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDto;
import fingertips.backend.asset.mapper.AssetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetMapper assetMapper;

    @Override
    public List<AssetDto> getAllAssets(int memberId) {
        List<AssetDto> assetList = assetMapper.getAllAssets(memberId);
        return assetList;
    }
}