package com.dgu.LookIT.fitting.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3FileService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://ai-server.com") // ← 실제 AI 서버 주소로 바꾸세요
            .build();

    private String defaultUrl = "https://s3.amazonaws.com/" + bucketName;

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file);
        try{
            s3Client.putObject(bucketName, fileName, file.getInputStream(), getObjectMetadata(file));
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (SdkClientException e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }
    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }

    public String processFitting(Long userId, MultipartFile clothesImage) throws IOException {
        // 사용자 정보에서 bodyImage URL 가져오기
        String bodyImageUrl = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER))
                .getBodyImage();

        // Multipart 요청 구성
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("bodyImageUrl", bodyImageUrl);  // 문자열 파라미터
        builder.part("clothesImage", clothesImage.getResource()) // 파일 파라미터
                .filename(clothesImage.getOriginalFilename())      // 파일 이름 지정
                .contentType(MediaType.IMAGE_PNG);                // 이미지 타입 명시 (예시)

        // WebClient로 AI 서버에 요청 보내기
        return webClient.post()
                .uri("http://ai-server.com/fitting") // 실제 AI 서버 주소
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기 방식으로 결과 대기
    }


}
