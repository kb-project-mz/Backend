package fingertips.backend.asset.controller;


import fingertips.backend.asset.dto.AssetDTO;
import fingertips.backend.asset.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/connection")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    // 사용자가 소유하는 계좌와 카드 정보 가져오기
    @GetMapping("/asset/allAssets/{memberId}")
    public ResponseEntity<List<AssetDTO>> getAllAssets(@PathVariable("memberId") int memberId) {
        List<AssetDTO> assetList = assetService.getAllAssets(memberId);
        return ResponseEntity.ok(assetList);
    }

    // 사용자가 연동시킨 계좌와 카드 정보 가져오기
    @GetMapping("/asset/connAssets/{memberId}")
    public ResponseEntity<List<AssetDTO>> getConnAssets(@PathVariable("memberId") int memberId) {
        List<AssetDTO> assetList = assetService.getConnAssets(memberId);
        return ResponseEntity.ok(assetList);
    }

    // 카드 연동
    @PostMapping("/card/{cardId}")
    public ResponseEntity<Void> updateCardStatus(@PathVariable("cardId") int cardId) {
        assetService.connCard(cardId);
        return ResponseEntity.noContent().build();
    }

    // 계좌 연동
    @PostMapping("/account/{accountId}")
    public ResponseEntity<Void> updateAccountStatus(@PathVariable("accountId") int accountId) {
        assetService.connAccount(accountId);
        return ResponseEntity.noContent().build();
    }
}