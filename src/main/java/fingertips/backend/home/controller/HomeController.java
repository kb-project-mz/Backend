package fingertips.backend.home.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final BalanceService balanceService;

    @PostMapping("/balance/{memberIdx}")
    public ResponseEntity<JsonResponse<List<BalanceDTO>>> getBalanceByMemberIdx(@PathVariable int memberIdx) {

        balanceService.setMemberIdx(memberIdx);
        List<BalanceDTO> balanceByMemberIdx = balanceService.getBalanceByMemberIdx(memberIdx);
        return ResponseEntity.ok().body(JsonResponse.success(balanceByMemberIdx));
    }

}
