package fingertips.backend.home.controller;

import fingertips.backend.asset.dto.AssetDTO;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.service.BalanceService;
import fingertips.backend.home.service.SocketIOService;
import fingertips.backend.member.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final BalanceService balanceService;
    private final SocketIOService socketIOService;

    @GetMapping("/balance/{memberId}")
    public ResponseEntity<JsonResponse<List<BalanceDTO>>> getBalance(@PathVariable String memberId) {
       List<BalanceDTO> resultList=balanceService.getBalance(memberId);
        socketIOService.sendBalanceUpdate(memberId, resultList);

        return ResponseEntity.ok().body(JsonResponse.success(resultList));
    }

}
