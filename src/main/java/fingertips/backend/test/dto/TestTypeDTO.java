package fingertips.backend.test.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestTypeDTO {

    private Integer typeIdx;
    private String typeName;
    private String typeImage;
}
