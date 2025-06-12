package com.dgu.LookIT.fitting.controller;


import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.dto.request.DiagnosisRequest;
import com.dgu.LookIT.fitting.dto.request.FaceMoodRequest;
import com.dgu.LookIT.fitting.service.AnalysisService;
import com.dgu.LookIT.fitting.service.S3FileService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v0")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalysisService analysisService;

    // 3. 비동기 방식 - 얼굴형 분석 요청
    @PostMapping("/face-analysis")
    public ResponseDto<?> requestFaceAnalysis(@UserId Long userId, @RequestParam("analysis") MultipartFile analysisImage) throws IOException {
        analysisService.processFaceAnalysisAsync(userId, analysisImage);
        return ResponseDto.ok("얼굴형 분석 완료");
    }

    // 4. 비동기 방식 - 체형 분석 요청
    @PostMapping("/body-analysis")
    public ResponseDto<?> requestBodyAnalysis(@UserId Long userId, @RequestParam("analysis") MultipartFile analysisImage) throws IOException {
        analysisService.processBodyAnalysisAsync(userId, analysisImage);
        return ResponseDto.ok("체형 분석 완료");
    }

    // 1. 얼굴형 분석 결과 조회 API
    @GetMapping("/face-analysis/result")
    public ResponseDto<?> getFaceAnalysisResult(@UserId Long userId) {
        String result = analysisService.getFaceAnalysisResult(userId);
        return ResponseDto.ok(result != null ? result : "진행중");
    }

    // 2. 체형 분석 결과 조회 API
    @GetMapping("/body-analysis/result")
    public ResponseDto<?> getBodyAnalysisResult(@UserId Long userId) {
        String result = analysisService.getBodyAnalysisResult(userId);
        return ResponseDto.ok(result != null ? result : "진행중");
    }

    @PostMapping("/diagnosis")
    public ResponseDto<?> postBodyDiagnosis(@UserId Long userId, @RequestBody DiagnosisRequest request) {
        analysisService.postBodyType(userId, request.bodyType());
        return ResponseDto.ok(request.bodyType());
    }

    @PostMapping("/face-mood")
    public ResponseDto<?> postFaceMood(@UserId Long userId, @RequestBody FaceMoodRequest request) {
        analysisService.postFaceMood(userId, request.faceMood());
        return ResponseDto.ok(request.faceMood());
    }
}
