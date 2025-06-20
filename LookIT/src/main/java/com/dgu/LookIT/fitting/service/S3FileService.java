package com.dgu.LookIT.fitting.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dgu.LookIT.exception.CommonException;
import com.dgu.LookIT.exception.ErrorCode;
import com.dgu.LookIT.fitting.domain.VirtualFitting;
import com.dgu.LookIT.fitting.dto.response.FittingResultResponse;
import com.dgu.LookIT.fitting.repository.VirtualFittingRepository;
import com.dgu.LookIT.user.domain.User;
import com.dgu.LookIT.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
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
        return virtualFittingRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(FittingResultResponse::from) // record의 정적 메서드 사용
                .toList();
    }


    @Async
    public void processFittingAsync(Long userId, MultipartFile clothesImage, MultipartFile bodyImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));

        VirtualFitting fitting = VirtualFitting.builder()
                .user(user)
                .build();
        virtualFittingRepository.save(fitting);  // 1. 먼저 PENDING 상태 저장

        try {
            ByteArrayResource bodyResource = new ByteArrayResource(bodyImage.getBytes()) {
                @Override public String getFilename() { return bodyImage.getOriginalFilename(); }
            };
            ByteArrayResource clothesResource = new ByteArrayResource(clothesImage.getBytes()) {
                @Override public String getFilename() { return clothesImage.getOriginalFilename(); }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("body", bodyResource)
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(bodyImage.getContentType()));
            builder.part("clothes", clothesResource)
                    .filename(clothesImage.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(clothesImage.getContentType()));

            webClient.post()
                    .uri("/fitting")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(responseStr -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode root = mapper.readTree(responseStr);
                            String imageUrl = root.path("image").path("url").asText();

                            fitting.setResultImageUrl(imageUrl);
                            virtualFittingRepository.save(fitting);  // 3. 성공 시 업데이트
                            log.info("가상 피팅 성공: {}", imageUrl);

                        } catch (Exception e) {
                            virtualFittingRepository.delete(fitting);  // 4. 파싱 실패 시 삭제
                            log.error("JSON 파싱 실패 → 레코드 삭제", e);
                        }
                    }, error -> {
                        virtualFittingRepository.delete(fitting);  // 4. AI 서버 실패 시 삭제
                        log.error("가상 피팅 요청 실패 → 레코드 삭제", error);
                    });

        } catch (Exception e) {
            virtualFittingRepository.delete(fitting);  // 4. 파일 처리 실패 시 삭제
            log.error("파일 준비 실패 → 레코드 삭제", e);
        }
    }

    public String deleteVirtualFitting(Long userId, Long fittingId) {
        User user = userRepository.findById(userId).orElseThrow();

        VirtualFitting virtualFitting = virtualFittingRepository.findById(fittingId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUNT_VIRTUAL_FITTING));

        virtualFittingRepository.delete(virtualFitting);
        return fittingId + "삭제되었습니다.";
    }

    public String processFitting(Long userId, MultipartFile clothesImage, MultipartFile bodyImage) {
        try {
            ByteArrayResource bodyResource = new ByteArrayResource(bodyImage.getBytes()) {
                @Override public String getFilename() {
                    return bodyImage.getOriginalFilename();
                }
            };

            ByteArrayResource clothesResource = new ByteArrayResource(clothesImage.getBytes()) {
                @Override public String getFilename() {
                    return clothesImage.getOriginalFilename();
                }
            };

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("body", bodyResource)
                    .filename(bodyImage.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(bodyImage.getContentType()));
            builder.part("clothes", clothesResource)
                    .filename(clothesImage.getOriginalFilename())
                    .contentType(MediaType.parseMediaType(clothesImage.getContentType()));

            String result = webClient.post()
                    .uri("/fitting")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .bodyValue(builder.build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result);
            String imageUrl = root.path("image").path("url").asText();

            User user = userRepository.findById(userId).orElseThrow(()-> new CommonException(ErrorCode.NOT_FOUND_USER));
            // DB 저장
            VirtualFitting fitting = VirtualFitting.builder()
                    .user(user)
                    .resultImageUrl(imageUrl)
                    .build();
            virtualFittingRepository.save(fitting);

            return imageUrl;

        } catch (Exception e) {
            log.error("가상 피팅 처리 실패", e);
            return null;
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
