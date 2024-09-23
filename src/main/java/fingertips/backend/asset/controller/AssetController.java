package fingertips.backend.asset.controller;


import fingertips.backend.asset.dto.AssetDTO;
import fingertips.backend.asset.service.AssetService;
import fingertips.backend.exception.dto.JsonResponse;
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
    @GetMapping("/asset/{id}")
    public ResponseEntity<JsonResponse<List<AssetDTO>>> getAllAssets(@PathVariable("id") int id) {
        List<AssetDTO> assetList = assetService.getAllAssets(id);
        return ResponseEntity.ok(JsonResponse.success(assetList));
    }

    // 사용자가 연동시킨 계좌와 카드 정보 가져오기
    @GetMapping("/asset/connected/{id}")
    public ResponseEntity<JsonResponse<List<AssetDTO>>> getConnAssets(@PathVariable("id") int id) {
        List<AssetDTO> assetList = assetService.getConnAssets(id);
        return ResponseEntity.ok(JsonResponse.success(assetList));
    }

    // 카드 연동
    @PostMapping("/card/{id}")
    public ResponseEntity<JsonResponse<String>> updateCardStatus(@PathVariable("id") int id) {
        assetService.connCard(id);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    // 계좌 연동
    @PostMapping("/account/{id}")
    public ResponseEntity<JsonResponse<String>> updateAccountStatus(@PathVariable("id") int id) {
        assetService.connAccount(id);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }
}