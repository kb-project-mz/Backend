package fingertips.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileDTO {

    private String memberId;
    private String uploadFileName;  // 고객이 업로드한 파일명
    private String storeFileName;   // 서버 내부에서 관리하는 파일명

}
