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

//    @GetMapping("/{memberIdx}")
//    public ResponseEntity<JsonResponse<List<AssetDTO>>> getAllAssets(@PathVariable int memberIdx) {
//        List<AssetDTO> assetList = assetService.getAllAssets(memberIdx);
//        return ResponseEntity.ok(JsonResponse.success(assetList));
//    }

    @GetMapping
    public ResponseEntity<JsonResponse<List<AssetDTO>>> getAllAssets(@RequestHeader("Authorization") String token) {
        String accessToken = jwtProcessor.extractToken(token);
        Integer memberIdx = jwtProcessor.getMemberIdx(accessToken);
        List<AssetDTO> assetList = assetService.getAllAssets(memberIdx);
        return ResponseEntity.ok(JsonResponse.success(assetList));
    }

    @PostMapping("/card/{cardIdx}")
    public ResponseEntity<JsonResponse<String>> updateCardStatus(@PathVariable int cardIdx) {
        assetService.connectCard(cardIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/account/{accountIdx}")
    public ResponseEntity<JsonResponse<String>> updateAccountStatus(@PathVariable int accountIdx) {
        assetService.connectAccount(accountIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/card/disconnect/{cardIdx}")
    public ResponseEntity<JsonResponse<String>> disconnectCard(@PathVariable int cardIdx) {
        assetService.disconnectCard(cardIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }

    @PostMapping("/account/disconnect/{accountIdx}")
    public ResponseEntity<JsonResponse<String>> disconnectAccount(@PathVariable int accountIdx) {
        assetService.disconnectAccount(accountIdx);
        return ResponseEntity.ok(JsonResponse.success("Update Success"));
    }
}
