package com.dgu.LookIT.fitting.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
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
import java.util.stream.Collectors;

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

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = generateFileName(file);
        try {
            s3Client.putObject(bucketName, fileName, file.getInputStream(), getObjectMetadata(file));
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (SdkClientException e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }

    public String requestFittingAsync(Long userId, MultipartFile clothesImage) {
        String taskId = UUID.randomUUID().toString();
        processFittingAsync(taskId, userId, clothesImage);
        return taskId;
    }

    @Async
    public void processFittingAsync(String taskId, Long userId, MultipartFile clothesImage) {
        try {
            String bodyImageUrl = userRepository.findById(userId)
                    .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER))
                    .getBodyImage();

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("bodyImageUrl", bodyImageUrl);
            builder.part("clothesImage", clothesImage.getResource())
                    .filename(clothesImage.getOriginalFilename())
                    .contentType(MediaType.IMAGE_PNG);

            String resultUrl = webClient.post()
                    .uri("/fitting")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            VirtualFitting fitting = VirtualFitting.builder()
                    .userId(userId)
                    .resultImageUrl(resultUrl)
                    .createdAt(LocalDateTime.now())
                    .build();

            virtualFittingRepository.save(fitting);
        } catch (Exception e) {
            log.error("Fitting async process failed: {}", e.getMessage());
        }
    }

    public List<FittingResultResponse> getFittingResults(Long userId) {
        return virtualFittingRepository.findAllByUserId(userId).stream()
                .map(FittingResultResponse::from)
                .collect(Collectors.toList());
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
