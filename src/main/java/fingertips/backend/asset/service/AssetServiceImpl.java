package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
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
    public List<AssetDTO> getAllAssets(Integer memberIdx) {
        return assetMapper.getAllAssets(memberIdx);
    }

    @Override
    public void connectCard(Integer assetIdx) {
        assetMapper.connectCard(assetIdx);
    }

    @Override
    public void connectAccount(Integer assetIdx) {
        assetMapper.connectAccount(assetIdx);
    }

    @Override
    public void disconnectCard(Integer assetIdx) {
        assetMapper.disconnectCard(assetIdx);
    }

    @Override
    public void disconnectAccount(Integer assetIdx) {
        assetMapper.disconnectAccount(assetIdx);
    }
}
