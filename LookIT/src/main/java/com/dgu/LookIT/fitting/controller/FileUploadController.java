package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.annotaion.UserId;
import com.dgu.LookIT.fitting.service.S3FileService;
import com.dgu.LookIT.global.ResponseDto;
import com.dgu.LookIT.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class FileUploadController {
    private final S3FileService s3FileService;

    @PostMapping("/upload")
    public ResponseDto<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileService.uploadFile(file);
        return ResponseDto.ok(url);
    }

    @PostMapping("/api/v0/virtual-fitting")
    public ResponseDto<?> fitClothes(@UserId Long userId, @RequestParam("clothes") MultipartFile clothesImage
    ) throws IOException {
        String fittedImageUrl = s3FileService.processFitting(userId, clothesImage);
        return ResponseDto.ok(fittedImageUrl);
    }
}
