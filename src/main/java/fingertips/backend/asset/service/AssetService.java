package fingertips.backend.asset.service;

import fingertips.backend.asset.dto.AssetDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetService {

    // 모든 계좌와 카드 불러오기(카드 추가 하기 팝업 화면에서 보여줄 것)
    List<AssetDTO> getAllAssets(@Param("memberId") int memberId);
    // 연동이 된 계좌와 카드 정보만 불러오기
    List<AssetDTO> getConnAssets(@Param("memberId") int memberId);
    // 선택된 계좌를 연동
    void connCard(@Param("id") int id);
    // 선택된 카드를 연동
    void connAccount(@Param("id") int id);
}
