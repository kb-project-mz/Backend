package fingertips.backend.member.service;

import org.springframework.web.multipart.MultipartFile;

public interface S3uploaderService {

    String saveFile(MultipartFile file);
    void deleteFile(String fileUrl);
    String generateRandomFilename(MultipartFile multipartFile);
    String validateFileExtension(String originalFilename);
}
