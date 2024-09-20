package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDto;

import java.util.List;

public interface AssetService {

    // 카드 + 계좌 = 자산
    List<AssetDto> getAllAssets(int memberId);
}
