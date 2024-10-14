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
    public void connectCard(Integer cardIdx) {
        assetMapper.connectCard(cardIdx);
    }

    @Override
    public void connectAccount(Integer accountIdx) {
        assetMapper.connectAccount(accountIdx);
    }

    @Override
    public void disconnectCard(Integer cardIdx) {
        assetMapper.disconnectCard(cardIdx);
    }

    @Override
    public void disconnectAccount(Integer accountIdx) {
        assetMapper.disconnectAccount(accountIdx);
    }
}
