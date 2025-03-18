package com.example.fileservice.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.example.fileservice.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/api/v1/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity< List<String> > saveFile(MultipartHttpServletRequest request) {
        List<MultipartFile> files = request.getFiles("file");
        String prefix = request.getParameter("prefix");

        List<String> result = fileService.saveFile(prefix, files);
        return ResponseEntity.ok(result);
    }
}
