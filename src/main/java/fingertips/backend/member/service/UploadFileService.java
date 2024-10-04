package fingertips.backend.member.service;


import fingertips.backend.member.util.UploadFile;
import org.springframework.web.multipart.MultipartFile;

// 이미지 업로드 및 URL 생성
public interface UploadFileService {

    UploadFile storeFile(MultipartFile file);
    void deleteFile(String memberId, String defaultImage);
    String getFullPath(String fileName);
    String createStoreFileName(String originalFileName);
    String extractExt(String originalFileName);

}
