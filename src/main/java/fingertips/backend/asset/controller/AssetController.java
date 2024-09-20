package fingertips.backend.asset.controller;


import fingertips.backend.asset.dto.AssetDto;
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
@CrossOrigin(origins = "*", methods = RequestMethod.GET)
public class AssetController {

    private final AssetService assetService;

    @GetMapping("/asset/{memberId}")
    public ResponseEntity<List<AssetDto>> getAssets(@PathVariable("memberId") int memberId) {
        List<AssetDto> assetList = assetService.getAllAssets(memberId);
        return ResponseEntity.ok(assetList);
    }

}