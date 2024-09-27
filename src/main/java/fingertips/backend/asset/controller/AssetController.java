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
@RequestMapping("/api/v1/asset")
@RequiredArgsConstructor
@Slf4j
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/{memberIdx}")
    public ResponseEntity<JsonResponse<List<AssetDTO>>> getAllAssets(@PathVariable int memberIdx) {

        List<AssetDTO> assetList = assetService.getAllAssets(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(assetList));
    }

    @PostMapping("/card/{memberIdx}")
    public ResponseEntity<JsonResponse<String>> updateCardStatus(@PathVariable int memberIdx) {

        assetService.connectCard(memberIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/account/{memberIdx}")
    public ResponseEntity<JsonResponse<String>> updateAccountStatus(@PathVariable int memberIdx) {

        assetService.connectAccount(memberIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }
}
