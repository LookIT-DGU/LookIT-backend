package com.dgu.LookIT.fitting.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dgu.LookIT.fitting.domain.VirtualFitting;
import com.dgu.LookIT.fitting.dto.response.FittingResultResponse;
import com.dgu.LookIT.fitting.repository.VirtualFittingRepository;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;
    private final UserRepository userRepository;
    private final VirtualFittingRepository virtualFittingRepository;
    private final WebClient webClient;

    //사용자 이미지 저장
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file);
        try {
            s3Client.putObject(bucketName, fileName, file.getInputStream(), getObjectMetadata(file));
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (SdkClientException e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }

    //AI 이미지 저장
    public String uploadByteImage(byte[] imageBytes, String fileName) throws IOException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/png");

        s3Client.putObject(bucketName, fileName, new java.io.ByteArrayInputStream(imageBytes), metadata);
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    //전체 이미지 조회
    public List<FittingResultResponse> getFittingResults(Long userId) {
        return virtualFittingRepository.findAllByUserId(userId).stream()
                .map(FittingResultResponse::from) // record의 정적 메서드 사용
                .toList();
    }


    @Async
    public void processFittingAsync(Long userId,
                                    MultipartFile clothesImage, MultipartFile bodyImage) {

        String taskId = UUID.randomUUID().toString();

        try {
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("clothesImage", clothesImage.getResource())
                    .filename(clothesImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);
            builder.part("bodyImage", bodyImage.getResource())
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            // 비동기 방식: subscribe 사용
            webClient.post()
                    .uri("/fitting")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .subscribe(resultImage -> {
                        try {
                            // S3 업로드
                            String resultImageUrl = uploadByteImage(resultImage, "result-" + taskId + ".png");

                            // DB 저장
                            VirtualFitting fitting = VirtualFitting.builder()
                                    .userId(userId)
                                    .resultImageUrl(resultImageUrl)
                                    .createdAt(LocalDateTime.now())
                                    .build();

                            virtualFittingRepository.save(fitting);
                        } catch (Exception ex) {
                            log.error("Error during fitting result handling", ex);
                        }
                    }, error -> {
                        log.error("AI 서버 응답 에러", error);
                    });

        } catch (Exception e) {
            log.error("Fitting async process setup failed: {}", e.getMessage(), e);
        }
    }


    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        return metadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
    }
}
