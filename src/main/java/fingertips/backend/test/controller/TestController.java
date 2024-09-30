package fingertips.backend.test.controller;

import fingertips.backend.consumption.service.ConsumptionService;
import fingertips.backend.exception.dto.JsonResponse;
import fingertips.backend.openai.service.OpenAiService;
import fingertips.backend.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value="/api/v1/test")
@RequiredArgsConstructor
public class TestController {


    private final TestService testService;


}
