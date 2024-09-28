package fingertips.backend.home.controller;

import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.home.dto.BalanceDTO;
import fingertips.backend.home.service.BalanceService;
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

    @GetMapping("/balance/{member_index}")
    public ResponseEntity<JsonResponse<List<BalanceDTO>>> getBalance(@PathVariable int member_index) {
        List<BalanceDTO> resultList=balanceService.getBalance(member_index);
//        socketIOService.sendBalanceUpdate(resultList);

        return ResponseEntity.ok().body(JsonResponse.success(resultList));
    }

}
