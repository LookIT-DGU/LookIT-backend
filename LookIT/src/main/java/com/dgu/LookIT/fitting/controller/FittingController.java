package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.service.AnalysisService;
import com.dgu.LookIT.fitting.service.S3FileService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class FittingController {

    private final S3FileService s3FileService;
    private final AnalysisService analysisService;

    // ✅ 1. 일반 파일 업로드
    @PostMapping("/upload")
    public ResponseDto<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileService.uploadFile(file);
        return ResponseDto.ok(url);
    }

    // ✅ 2. 비동기 방식 - 가상 피팅 요청
    @PostMapping("/api/v0/virtual-fitting")
    public ResponseDto<?> requestFitting(@UserId Long userId, @RequestParam("clothes") MultipartFile clothesImage) throws IOException {
        String taskId = s3FileService.requestFittingAsync(userId, clothesImage);
        return ResponseDto.ok(taskId);  // taskId로 나중에 결과 조회
    }

    // ✅ 3. 비동기 방식 - 얼굴형 분석 요청
    @PostMapping("/api/v0/face-analysis")
    public ResponseDto<?> requestFaceAnalysis(@UserId Long userId, @RequestParam("analysis") MultipartFile analysisImage) throws IOException {
        String taskId = analysisService.requestFaceAnalysis(userId, analysisImage);
        return ResponseDto.ok(taskId);
    }

    // ✅ 4. 비동기 방식 - 체형 분석 요청
    @PostMapping("/api/v0/body-analysis")
    public ResponseDto<?> requestBodyAnalysis(@UserId Long userId, @RequestParam("analysis") MultipartFile analysisImage) throws IOException {
        String taskId = analysisService.requestBodyAnalysis(userId, analysisImage);
        return ResponseDto.ok(taskId);
    }

    // ✅ 5. 결과 조회 - 얼굴형
    @GetMapping("/api/v0/face-analysis/result")
    public ResponseDto<?> getFaceResult(@RequestParam("taskId") String taskId) {
        String result = analysisService.getFaceAnalysisResult(taskId);
        return ResponseDto.ok(result != null ? result : "PENDING");
    }

    // ✅ 6. 결과 조회 - 체형
    @GetMapping("/api/v0/body-analysis/result")
    public ResponseDto<?> getBodyResult(@RequestParam("taskId") String taskId) {
        String result = analysisService.getBodyAnalysisResult(taskId);
        return ResponseDto.ok(result != null ? result : "PENDING");
    }

    // ✅ 7. 결과 조회 - 피팅 이미지 전체 목록
    @GetMapping("/api/v0/virtual-fitting/result")
    public ResponseDto<?> getFittingResults(@UserId Long userId) {
        return ResponseDto.ok(s3FileService.getFittingResults(userId));
    }
}
