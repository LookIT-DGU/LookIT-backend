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

}
