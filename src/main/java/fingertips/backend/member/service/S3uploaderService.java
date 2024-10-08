package fingertips.backend.member.service;

import fingertips.backend.member.util.UploadFile;
import org.springframework.web.multipart.MultipartFile;

public interface S3uploaderService {

    UploadFile uploadFile(MultipartFile file);
    void deleteFile(String fileUrl);
    String generateRandomFilename(MultipartFile multipartFile);
    String validateFileExtension(String originalFilename);
}
