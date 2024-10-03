package fingertips.backend.member.service;


import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.dto.UploadFileDTO;
import fingertips.backend.member.util.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Log4j
@RequiredArgsConstructor
@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Value("${file.dir}")
    private String fileDir;

    @Override
    public UploadFile storeFile(MultipartFile file) {
        if(file.isEmpty()) {
            throw new ApplicationException(ApplicationError.FILE_EMPTY);
        }
        String storeFileName = createStoreFileName(file.getOriginalFilename());
        try {
            file.transferTo(new File(getFullPath(storeFileName)));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationError.FILE_UPLOAD_FAILED);
        }
        return new UploadFile(file.getOriginalFilename(), storeFileName);
    }

    @Override
    public void deleteFile(UploadFile uploadFile) {
        File file = new File(getFullPath(uploadFile.getStoreFileName()));
        if (file.exists()) {
            if (!file.delete()) {
                throw new ApplicationException(ApplicationError.FILE_DELETE_FAILED);
            }
        } else {
            throw new ApplicationException(ApplicationError.FILE_NOT_FOUND);
        }
    }

    @Override
    public String getFullPath(String fileName) {
        return fileDir + fileName;
    }

    @Override
    public String createStoreFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    @Override
    public String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1) ;
    }
}
