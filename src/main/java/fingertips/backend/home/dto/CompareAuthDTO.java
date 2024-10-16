package fingertips.backend.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompareAuthDTO {

    private Integer memberIdx;
    private String memberId;
    private String memberName;
}
