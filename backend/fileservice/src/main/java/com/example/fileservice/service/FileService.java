package com.example.fileservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    List<String> saveFile(String prefix, List<MultipartFile> files);

}
