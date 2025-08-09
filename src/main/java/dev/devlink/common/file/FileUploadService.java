package dev.devlink.common.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService {
    
    private final AmazonS3 amazonS3;
    
    @Value("${aws.s3.bucket}")
    private String bucket;
    
    @Value("${aws.s3.url}")
    private String s3Url;
    
    public FileUploadService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }
    
    public String uploadFile(MultipartFile file, String directory) {
        validateFile(file);
        String fileName = generateFileName(file, directory);
        
        try {
            ObjectMetadata metadata = createMetadata(file);
            amazonS3.putObject(bucket, fileName, file.getInputStream(), metadata);
            return s3Url + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.", e);
        }
    }
    
    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith(s3Url)) {
            String fileName = fileUrl.replace(s3Url + "/", "");
            amazonS3.deleteObject(bucket, fileName);
        }
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
        
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다.");
        }
    }
    
    private String generateFileName(MultipartFile file, String directory) {
        String originalName = file.getOriginalFilename();
        String extension = originalName != null && originalName.contains(".") 
            ? originalName.substring(originalName.lastIndexOf(".")) : "";
        return directory + "/" + UUID.randomUUID().toString() + extension;
    }
    
    private ObjectMetadata createMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }
}
