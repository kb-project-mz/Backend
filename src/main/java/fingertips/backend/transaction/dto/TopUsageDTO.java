package fingertips.backend.transaction.dto;

import lombok.*;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopUsageDTO {

    private Map<String, Integer> mostUsage;
    private Map<String, Integer> maximumUsage;
}
