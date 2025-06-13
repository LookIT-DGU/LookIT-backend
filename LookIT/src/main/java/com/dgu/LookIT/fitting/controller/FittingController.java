package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.dto.response.FittingResultResponse;
import com.dgu.LookIT.fitting.service.S3FileService;
import com.dgu.LookIT.global.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class FittingController {

    private final S3FileService s3FileService;

    // 1. 일반 파일 업로드
    @PostMapping("/upload")
    public ResponseDto<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileService.uploadFile(file);
        return ResponseDto.ok(url);
    }

    // 2. 비동기 방식 - 가상 피팅 요청
    @PostMapping("/api/v0/virtual-fitting")
    public ResponseDto<?> requestFitting(
            @UserId Long userId,
            @RequestParam("clothes") MultipartFile clothesImage,
            @RequestParam("body") MultipartFile bodyImage
    ) throws IOException {
        s3FileService.processFittingAsync(userId, clothesImage, bodyImage);
        return ResponseDto.ok("가상 피팅 요청이 처리 완료됐습니다.");
    }

    // 동기 방식 - 가상 피팅 요청
    @PostMapping("/api/v0/virtual-fitting/sync")
    public ResponseDto<?> requestFittingSync(
            @UserId Long userId,
            @RequestParam("clothes") MultipartFile clothesImage,
            @RequestParam("body") MultipartFile bodyImage
    ) throws IOException {
        s3FileService.processFitting(userId, clothesImage, bodyImage);
        return ResponseDto.ok("가상 피팅 요청이 처리 완료됐습니다.");
    }

    // 3 가상피팅 결과 반환
    @GetMapping("/api/v0/virtual-fitting/result")
    public ResponseDto<List<FittingResultResponse>> getFittingResults(@UserId Long userId) {
        List<FittingResultResponse> results = s3FileService.getFittingResults(userId);
        return ResponseDto.ok(results);
    }



}
