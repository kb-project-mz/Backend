package fingertips.backend.asset.controller;

import fingertips.backend.asset.dto.AssetDTO;
import fingertips.backend.asset.service.AssetService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.security.util.JwtProcessor;
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
    private final JwtProcessor jwtProcessor;

    @GetMapping
    public ResponseEntity<JsonResponse<List<AssetDTO>>> getAllAssets(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<AssetDTO> assetList = assetService.getAllAssets(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(assetList));
    }

    @PostMapping("/card/{assetIdx}")
    public ResponseEntity<JsonResponse<String>> updateCardStatus(@PathVariable int assetIdx) {
        assetService.connectCard(assetIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/account/{assetIdx}")
    public ResponseEntity<JsonResponse<String>> updateAccountStatus(@PathVariable int assetIdx) {
        assetService.connectAccount(assetIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/card/disconnect/{assetIdx}")
    public ResponseEntity<JsonResponse<String>> disconnectCard(@PathVariable int assetIdx) {
        assetService.disconnectCard(assetIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/account/disconnect/{assetIdx}")
    public ResponseEntity<JsonResponse<String>> disconnectAccount(@PathVariable int assetIdx) {
        assetService.disconnectAccount(assetIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }
}
