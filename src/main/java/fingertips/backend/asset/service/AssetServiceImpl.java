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
    public List<AssetDTO> getAllAssets(int id) {
        return assetMapper.getAllAssets(id);
    }

    @Override
    public List<AssetDTO> getConnAssets(int id) {
        return assetMapper.getConnAssets(id);
    }

    @Override
    public void connCard(int id) {
        assetMapper.connCard(id);
    }

    @Override
    public void connAccount(int id) {
        assetMapper.connAccount(id);
    }


}