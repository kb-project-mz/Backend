package fingertips.backend.member.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import fingertips.backend.exception.error.ApplicationError;
import fingertips.backend.exception.error.ApplicationException;
import fingertips.backend.member.util.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploadServiceImpl implements S3uploaderService {

    private final AmazonS3 amazonS3;
    private final String bucket = "fingertips-bucket-local";

    public UploadFile uploadFile(MultipartFile file) {
        try {
            String randomFilename = "upload-image/" + generateRandomFilename(file);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            amazonS3.putObject(new PutObjectRequest(bucket, randomFilename, file.getInputStream(), metadata));
            return UploadFile.builder()
                    .uploadFileName(file.getOriginalFilename())
                    .storeFileName(randomFilename)
                    .build();

        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException: " + e.getErrorMessage());
        } catch (SdkClientException e) {
            log.error("SdkClientException: " + e.getMessage());
        } catch (IOException e) {
            log.error("IOException: " + e.getMessage());
        }
        return null;
    }
    @Override
    public void deleteFile(String fileUrl) {
        String[] urlParts = fileUrl.split("/");
        String fileBucket = urlParts[2].split("\\.")[0];
        if (!fileBucket.equals(bucket)) {
            throw new ApplicationException(ApplicationError.FILE_EMPTY);
        }
        String objectKey = String.join("/", Arrays.copyOfRange(urlParts, 3, urlParts.length));
        if (!amazonS3.doesObjectExist(bucket, objectKey)) {
            throw new ApplicationException(ApplicationError.FILE_EMPTY);
        }
        try {
            amazonS3.deleteObject(bucket, objectKey);
        } catch (AmazonS3Exception e) {
            log.error("File delete fail : " + e.getMessage());
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR);
        } catch (SdkClientException e) {
            log.error("AWS SDK client error : " + e.getMessage());
            throw new ApplicationException(ApplicationError.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

    @Override
    public String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg", "jfif");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new ApplicationException(ApplicationError.FILE_EMPTY);
        }
        return fileExtension;
    }
}
