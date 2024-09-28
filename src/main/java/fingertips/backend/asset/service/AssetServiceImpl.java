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
    public List<AssetDTO> getAllAssets(Integer id) {
        return assetMapper.getAllAssets(id);
    }

    @Override
    public void connectCard(Integer id) {
        assetMapper.connectCard(id);
    }

    @Override
    public void connectAccount(Integer id) {
        assetMapper.connectAccount(id);
    }
}
