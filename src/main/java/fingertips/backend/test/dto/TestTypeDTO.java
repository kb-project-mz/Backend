package fingertips.backend.test.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTypeDTO {
    private Integer typeIdx;
    private String typeName; //유형 이름
    private String typeImage;
}
