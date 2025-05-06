package com.dgu.LookIT.fitting.controller;

import com.dgu.LookIT.fitting.service.S3FileUploadService;
import com.dgu.LookIT.global.ResponseDto;
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
    private final S3FileUploadService s3FileUploadService;

    @PostMapping("/upload")
    public ResponseDto<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String url = s3FileUploadService.uploadFile(file);
        return ResponseDto.ok(url);
    }
}
