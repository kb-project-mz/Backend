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
//
//    private Set<String> uploadedFileNames = new HashSet<>();
//    private Set<Long> uploadedFileSizes = new HashSet<>();


    private String bucket = "fingertips-bucket-local";

//    @Value("${spring.servlet.multipart.max-file-size}")
//    private String maxSizeString;

    public UploadFile uploadFile(MultipartFile file) {
        try {
            // 파일을 S3에 업로드
            String randomFilename = generateRandomFilename(file);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();


            // ObjectMetadata 생성 및 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); // ContentLength 설정
            metadata.setContentType(file.getContentType());
            log.info("meeeeeeeeeeeeeeeeeeeeee" + metadata);
            log.info("File size: " + file.getSize());
            log.info("File content type: " + file.getContentType());
            InputStream inputStream = file.getInputStream();
            log.info("InputStream available: " + inputStream.available()); // 읽을 수 있는 바이트 수

            amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
            return UploadFile.builder()
                    .uploadFileName(file.getOriginalFilename()) // 고객이 업로드한 파일명
                    .storeFileName(fileName) // 서버 내부에서 관리하는 파일명
                    .build();
        } catch (AmazonServiceException e) {
            log.error("AmazonServiceException: " + e.getErrorMessage());
        } catch (SdkClientException e) {
            log.error("SdkClientException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // 단일 파일 저장
//    @Override
//    public UploadFile saveFile(MultipartFile file) {
//        String randomFilename = generateRandomFilename(file);
//
//        log.info("File upload started: " + randomFilename);
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());
//
//        log.info("++++++++++++++++" + metadata);
//        try {
//            log.info("Attempting to upload file to S3...");
//            amazonS3.putObject(new PutObjectRequest(bucket, randomFilename, file.getInputStream(), metadata));
//        } catch (AmazonS3Exception e) {
//            log.error("Amazon S3 error while uploading file: " + e.getMessage());
//            throw new ApplicationException(ApplicationError.FILE_UPLOAD_FAILED);
//        } catch (SdkClientException e) {
//            log.error("AWS SDK client error while uploading file: " + e.getMessage());
//            throw new ApplicationException(ApplicationError.FILE_UPLOAD_FAILED);
//        } catch (IOException e) {
//            log.error("IO error while uploading file: " + e.getMessage());
//            throw new ApplicationException(ApplicationError.FILE_UPLOAD_FAILED);
//        }
//
//        log.info("File upload completed: " + randomFilename);
//
//        // UploadFile 객체 생성 후 반환
//        return UploadFile.builder()
//                .uploadFileName(file.getOriginalFilename()) // 고객이 업로드한 파일명
//                .storeFileName(randomFilename) // 서버 내부에서 관리하는 파일명
//                .build();
//    }

    // 파일 삭제
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

        log.info("File delete complete: " + objectKey);
    }

//    private void clear() {
//        uploadedFileNames.clear();
//        uploadedFileSizes.clear();
//    }

    // 랜덤파일명 생성 (파일명 중복 방지)
    @Override
    public String generateRandomFilename(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = validateFileExtension(originalFilename);
        String randomFilename = UUID.randomUUID() + "." + fileExtension;
        return randomFilename;
    }

    // 파일 확장자 체크
    @Override
    public String validateFileExtension(String originalFilename) {
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new ApplicationException(ApplicationError.FILE_EMPTY);
        }
        return fileExtension;
    }
}
